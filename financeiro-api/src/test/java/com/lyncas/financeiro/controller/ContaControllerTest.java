package com.lyncas.financeiro.controller;

import com.lyncas.financeiro.model.ContaDTO;
import com.lyncas.financeiro.model.ValorTotalResponse;
import com.lyncas.financeiro.persistence.model.Conta;
import com.lyncas.financeiro.service.impl.ContaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ContaControllerTest {

    @InjectMocks
    ContaController contaController;

    @Mock
    ContaServiceImpl contaService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);;
        contaController = new ContaController(contaService);
    }

    @Test
    void saveReturnsContaResponseOnValidConta() {
        ContaDTO conta = new ContaDTO();
        ContaDTO expectedResponse = new ContaDTO();
        when(contaService.create(conta)).thenReturn(expectedResponse);

        ContaDTO response = contaController.save(conta);

        assertEquals(expectedResponse, response);
    }

    @Test
    void updateReturnsContaResponseOnValidConta() {
        ContaDTO conta = new ContaDTO();
        ContaDTO expectedResponse = new ContaDTO();
        when(contaService.update(1L, conta)).thenReturn(expectedResponse);

        ContaDTO response = contaController.update(1L, conta);

        assertEquals(expectedResponse, response);
    }

    @Test
    void updateSituacaoReturnsContaResponseOnValidIdAndConta() {
        Long id = 1L;
        ContaDTO conta = new ContaDTO();
        ContaDTO expectedResponse = new ContaDTO();
        when(contaService.updateSituacao(id, conta)).thenReturn(expectedResponse);

        ContaDTO response = contaController.updateSituacao(id, conta);

        assertEquals(expectedResponse, response);
    }

    @Test
    void getContasPendentesReturnsPageOfContaResponse() {
        LocalDate dataVencimentoInicio = LocalDate.now();
        LocalDate dataVencimentoFim = LocalDate.now().plusDays(1);
        String descricao = "test";
        String sortBy = "id";
        String sortOrder = "desc";
        int page = 0;
        int size = 20;
        Page<ContaDTO> expectedPage = Page.empty();
        when(contaService.findAllPendentesByPeriodoAndDescricao(dataVencimentoInicio, dataVencimentoFim, descricao, sortBy, sortOrder, page, size)).thenReturn(expectedPage);

        Page<ContaDTO> response = contaController.getContasPendentes(dataVencimentoInicio, dataVencimentoFim, descricao, sortBy, sortOrder, page, size);

        assertEquals(expectedPage, response);
    }

    @Test
    void getByIdReturnsContaResponseOnValidId() {
        Long id = 1L;
        ContaDTO expectedResponse = new ContaDTO();
        when(contaService.getById(id)).thenReturn(expectedResponse);

        ContaDTO response = contaController.getById(id);

        assertEquals(expectedResponse, response);
    }

    @Test
    void getTotalPagoPeriodoReturnsValorTotalResponse() {
        LocalDate dataVencimentoInicio = LocalDate.now();
        LocalDate dataVencimentoFim = LocalDate.now().plusDays(1);
        ValorTotalResponse expectedResponse = new ValorTotalResponse();
        when(contaService.getValorTotalPeriodo(dataVencimentoInicio, dataVencimentoFim)).thenReturn(expectedResponse);

        ValorTotalResponse response = contaController.getTotalPagoPeriodo(dataVencimentoInicio, dataVencimentoFim);

        assertEquals(expectedResponse, response);
    }

    @Test
    void uploadCsvReturnsAcceptedOnValidFile() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("user");

        ResponseEntity<String> response = contaController.uploadCsv(file, principal);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals("Arquivo importado com sucesso", response.getBody());
    }


}
