package com.lyncas.financeiro.service;

import com.lyncas.financeiro.model.ContaDTO;
import com.lyncas.financeiro.model.ValorTotalResponse;
import com.lyncas.financeiro.persistence.model.Conta;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

public interface ContaService {

    ContaDTO create(ContaDTO conta);
    ContaDTO update(Long id, ContaDTO conta);
    ContaDTO updateSituacao(Long id, ContaDTO conta);
    Page<ContaDTO> findAllPendentesByPeriodoAndDescricao(LocalDate dataVencimentoInicio,
                                                         LocalDate dataVencimentoFim,
                                                         String descricao, String sortBy, String sortOrder, int page, int size);
    ContaDTO getById(Long id);
    void importFromFile(MultipartFile file, String username) throws IOException;
    ValorTotalResponse getValorTotalPeriodo(LocalDate dataVencimentoInicio, LocalDate dataVencimentoFim);

}
