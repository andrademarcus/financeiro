package com.lyncas.financeiro.service.impl;

import com.lyncas.financeiro.exception.RecordNotFoundException;
import com.lyncas.financeiro.model.ContaDTO;
import com.lyncas.financeiro.model.ValorTotalResponse;
import com.lyncas.financeiro.persistence.model.Conta;
import com.lyncas.financeiro.persistence.model.Situacao;
import com.lyncas.financeiro.persistence.repository.ContaRepository;
import com.lyncas.financeiro.service.ContaService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static com.lyncas.financeiro.mapper.ContaMapper.*;
import static com.lyncas.financeiro.util.SortingHelper.createPageableWithSort;

@Service
@Slf4j
public class ContaServiceImpl implements ContaService {

    private final ContaRepository contaRepository;
    private final AuthServiceImpl authService;

    public ContaServiceImpl(ContaRepository contaRepository, AuthServiceImpl authService) {
        this.contaRepository = contaRepository;
        this.authService = authService;
    }

    @Override
    public ContaDTO create(ContaDTO contaDTO) {

        if (contaDTO != null && contaDTO.situacao() == Situacao.PAGO && contaDTO.dataPagamento() == null) {
            throw new RuntimeException("Informe a data de pagamento da conta");
        }

        Conta conta = transformToEntity.apply(contaDTO);
        conta.setDataCadastro(LocalDateTime.now());
        conta.setUsuarioCadastro(authService.getAuthentication().getName());
        return transformToDTO.apply(contaRepository.save(conta));
    }

    @Override
    public ContaDTO update(Long id, ContaDTO contaDTO) {

        return contaRepository.findById(id)
                .map(c -> {
                    c.setDataVencimento(contaDTO.dataVencimento());
                    c.setDescricao(contaDTO.descricao());
                    c.setSituacao(contaDTO.situacao());
                    c.setDataPagamento(contaDTO.dataPagamento());
                    c.setDataAtualizacao(LocalDateTime.now());
                    c.setUsuarioAtualizacao(authService.getAuthentication().getName());
                    return transformToDTO.apply(contaRepository.save(c));
                })
                .orElseThrow(() -> new RecordNotFoundException("Conta não encontrada com id: " + id));

    }

    @Override
    public ContaDTO updateSituacao(Long id, ContaDTO contaDTO) {

        if (contaDTO == null || contaDTO.situacao() == null) {
            throw new RuntimeException("Informe a situação da conta");
        } else if (contaDTO.situacao() == Situacao.PAGO && contaDTO.dataPagamento() == null) {
            throw new RuntimeException("Informe a data de pagamento da conta");
        }

        Optional<Conta> optionalConta = contaRepository.findById(id);

        return optionalConta.map(c -> {
            // permite alteracao se for uma conta pendente
            if (c.getSituacao() == Situacao.PENDENTE && contaDTO.situacao() != Situacao.PENDENTE) {
                c.setDataPagamento(contaDTO.dataPagamento());
                c.setSituacao(contaDTO.situacao());
                c.setDataAtualizacao(LocalDateTime.now());
                c.setUsuarioAtualizacao(authService.getAuthentication().getName());
                return transformToDTO.apply(contaRepository.save(c));
            }
            throw new RuntimeException("Situação da conta não pode ser alterada");
        }).orElseThrow(() -> new RecordNotFoundException("Conta não encontrada com id: " + id));
    }

    @Override
    public Page<ContaDTO> findAllPendentesByPeriodoAndDescricao(LocalDate dataVencimentoInicio,
                                                                LocalDate dataVencimentoFim,
                                                                String descricao,
                                                                String sortBy, String sortOrder, int page, int size) {
        Pageable pageable = createPageableWithSort(page, size, sortBy, sortOrder);
        Page<Conta> contasPage = contaRepository.findByDataVencimentoAndDescricao(dataVencimentoInicio,
                dataVencimentoFim, descricao, pageable);
        return contasPage.map(transformToDTO);
    }

    @Override
    public ContaDTO getById(Long id) {
        return transformToDTO.apply(contaRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Conta não encontrada com id: " + id)));
    }

    @Override
    @Async
    public void importFromFile(MultipartFile file, String username) throws IOException {

        final DateTimeFormatter dateFormater = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            for (CSVRecord csvRecord : csvParser) {

                String dataVencimento = csvRecord.get("DATA_VENCIMENTO");
                String dataPagamento = csvRecord.get("DATA_PAGAMENTO");
                String valor = csvRecord.get("VALOR");
                String descricao = csvRecord.get("DESCRICAO");
                String situacao = csvRecord.get("SITUACAO");

                Conta conta = new Conta();
                conta.setDataVencimento(LocalDate.parse(dataVencimento, dateFormater));
                if (dataPagamento != null && !dataPagamento.isEmpty()) {
                    conta.setDataPagamento(LocalDate.parse(dataPagamento, dateFormater));
                }
                conta.setValor(new BigDecimal(valor));
                conta.setDescricao(descricao);
                conta.setSituacao(Situacao.valueOf(situacao));
                conta.setDataCadastro(LocalDateTime.now());
                conta.setUsuarioCadastro(username);
                contaRepository.save(conta);

            }
        }

    }

    @Override
    public ValorTotalResponse getValorTotalPeriodo(LocalDate dataVencimentoInicio, LocalDate dataVencimentoFim) {
        BigDecimal total = contaRepository.getValorTotalPeriodo(dataVencimentoInicio, dataVencimentoFim).orElse(BigDecimal.ZERO);
        return new ValorTotalResponse(total);
    }


}
