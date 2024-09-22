package com.lyncas.financeiro.service.impl;

import com.lyncas.financeiro.exception.RecordNotFoundException;
import com.lyncas.financeiro.model.ContaDTO;
import com.lyncas.financeiro.model.ValorTotalResponse;
import com.lyncas.financeiro.persistence.model.Conta;
import com.lyncas.financeiro.persistence.model.Situacao;
import com.lyncas.financeiro.persistence.repository.ContaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ContaServiceImplTest {

    ContaServiceImpl contaService;

    @Mock
    ContaRepository contaRepository;

    @Mock
    AuthServiceImpl authService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        contaService = new ContaServiceImpl(contaRepository, authService);
    }

    @Test
    void createThrowsExceptionWhenContaIsPagoAndDataPagamentoIsNull() {
        ContaDTO conta = getContaDTO(Situacao.PAGO);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> contaService.create(conta));

        assertEquals("Informe a data de pagamento da conta", exception.getMessage());
    }

    @Test
    void createSavesContaWithValidData() {
        ContaDTO contaDTO = getContaDTO(Situacao.PENDENTE);

        Authentication authentication = mock(Authentication.class);
        when(authService.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testUser");

        Conta conta = new Conta();
        conta.setDataCadastro(LocalDateTime.now());
        conta.setUsuarioCadastro("testUser");

        when(contaRepository.save(any(Conta.class))).thenReturn(conta);

        ContaDTO result = contaService.create(contaDTO);

        assertNotNull(result);
        assertEquals("testUser", result.usuarioCadastro());
        verify(contaRepository, times(1)).save(any(Conta.class));
    }


    private ContaDTO getContaDTO(Situacao situacao) {
        return new ContaDTO(null, null, null, BigDecimal.ZERO, "", situacao,
                "", "", LocalDateTime.now(), LocalDateTime.now());
    }


    @Test
    void updateContaWithValidData() {
        Long id = 1L;
        ContaDTO contaDTO = new ContaDTO();
        Conta conta = new Conta();
        Conta savedConta = new Conta();
        when(contaRepository.findById(id)).thenReturn(Optional.of(conta));
        when(contaRepository.save(conta)).thenReturn(savedConta);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");
        when(authService.getAuthentication()).thenReturn(authentication);

        ContaDTO response = contaService.update(id, contaDTO);

        assertNotNull(response);
        verify(contaRepository, times(1)).save(conta);
        assertEquals("testUser", conta.getUsuarioAtualizacao());
    }

    @Test
    void updateSituacaoThrowsExceptionWhenContaIsNull() {
        Long id = 1L;

        RuntimeException exception = assertThrows(RuntimeException.class, () -> contaService.updateSituacao(id, null));

        assertEquals("Informe a situação da conta", exception.getMessage());
    }

    @Test
    void updateSituacaoThrowsExceptionWhenSituacaoIsNull() {
        Long id = 1L;
        ContaDTO conta = new ContaDTO();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> contaService.updateSituacao(id, conta));

        assertEquals("Informe a situação da conta", exception.getMessage());
    }

    @Test
    void updateSituacaoThrowsExceptionWhenContaIsPagoAndDataPagamentoIsNull() {
        Long id = 1L;
        ContaDTO conta = new ContaDTO(null, null, null, BigDecimal.ZERO, "", Situacao.PAGO,
                "", "", LocalDateTime.now(), LocalDateTime.now());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> contaService.updateSituacao(id, conta));

        assertEquals("Informe a data de pagamento da conta", exception.getMessage());
    }

    @Test
    void updateSituacaoUpdatesContaWhenValid() {
        Long id = 1L;
        ContaDTO conta = new ContaDTO(null, null, LocalDate.now(), BigDecimal.ZERO, "", Situacao.PAGO,
                "", "", LocalDateTime.now(), LocalDateTime.now());
        Conta existingConta = new Conta();
        existingConta.setSituacao(Situacao.PENDENTE);
        when(contaRepository.findById(id)).thenReturn(Optional.of(existingConta));
        when(contaRepository.save(existingConta)).thenReturn(existingConta);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");
        when(authService.getAuthentication()).thenReturn(authentication);

        ContaDTO response = contaService.updateSituacao(id, conta);

        assertNotNull(response);
        verify(contaRepository, times(1)).save(existingConta);
        assertEquals("testUser", existingConta.getUsuarioAtualizacao());
    }

    @Test
    void updateSituacaoThrowsExceptionWhenSituacaoCannotBeChanged() {
        Long id = 1L;
        ContaDTO conta = new ContaDTO(null, null, LocalDate.now(), BigDecimal.ZERO, "", Situacao.PAGO,
                "", "", LocalDateTime.now(), LocalDateTime.now());
        Conta existingConta = new Conta();
        existingConta.setSituacao(Situacao.PAGO);
        when(contaRepository.findById(id)).thenReturn(Optional.of(existingConta));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> contaService.updateSituacao(id, conta));

        assertEquals("Situação da conta não pode ser alterada", exception.getMessage());
    }

    @Test
    void getByIdThrowsExceptionWhenContaNotFound() {
        Long id = 1L;
        when(contaRepository.findById(id)).thenReturn(Optional.empty());

        RecordNotFoundException exception = assertThrows(RecordNotFoundException.class, () -> contaService.getById(id));

        assertEquals("Conta não encontrada com id: " + id, exception.getMessage());
    }

    @Test
    void getByIdReturnsContaResponseWhenContaFound() {
        Long id = 1L;
        Conta conta = new Conta();
        when(contaRepository.findById(id)).thenReturn(Optional.of(conta));

        ContaDTO response = contaService.getById(id);

        assertNotNull(response);
    }

    @Test
    void getValorTotalPeriodoReturnsValorTotalResponse() {
        LocalDate dataVencimentoInicio = LocalDate.now();
        LocalDate dataVencimentoFim = LocalDate.now().plusDays(1);
        BigDecimal total = BigDecimal.TEN;
        when(contaRepository.getValorTotalPeriodo(dataVencimentoInicio, dataVencimentoFim)).thenReturn(Optional.of(total));

        ValorTotalResponse response = contaService.getValorTotalPeriodo(dataVencimentoInicio, dataVencimentoFim);

        assertNotNull(response);
        assertEquals(total, response.valorTotal());
    }


}
