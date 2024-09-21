package com.lyncas.financeiro.controller;

import com.lyncas.financeiro.model.ContaDTO;
import com.lyncas.financeiro.model.ValorTotalResponse;
import com.lyncas.financeiro.persistence.model.Conta;
import com.lyncas.financeiro.service.ContaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/contas")
public class ContaController {

    private final ContaService contaService;

    public ContaController(ContaService contaService) {
        this.contaService = contaService;
    }

    @PostMapping(value = "/save")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ContaDTO save(@RequestBody @Valid ContaDTO conta) {
        return contaService.create(conta);
    }

    @PatchMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ContaDTO update(@PathVariable Long id, @RequestBody @Valid ContaDTO conta) {
        return contaService.update(id, conta);
    }

    @PatchMapping(value = "/{id}/situacao")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ContaDTO updateSituacao(@PathVariable Long id, @RequestBody ContaDTO conta) {
        return contaService.updateSituacao(id, conta);
    }

    @GetMapping(value = "/list-pendentes", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public Page<ContaDTO> getContasPendentes(@RequestParam(name = "dataVencInicio", required = false) LocalDate dataVencimentoInicio,
                                             @RequestParam(name = "dataVencFim", required = false) LocalDate dataVencimentoFim,
                                             @RequestParam(name = "descricao", required = false) String descricao,
                                             @RequestParam(name = "sortBy", required = false, defaultValue = "id") String sortBy,
                                             @RequestParam(name = "sortOrder", required = false, defaultValue = "desc") String sortOrder,
                                             @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                             @RequestParam(name = "size", required = false, defaultValue = "20") int size) {
        return contaService.findAllPendentesByPeriodoAndDescricao(dataVencimentoInicio, dataVencimentoFim, descricao, sortBy, sortOrder, page, size);
    }

    @GetMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ContaDTO getById(@PathVariable Long id) {
        return contaService.getById(id);
    }

    @GetMapping(value = "/total-pago", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ValorTotalResponse getTotalPagoPeriodo(@RequestParam(name = "dataVencInicio") LocalDate dataVencimentoInicio,
                                                  @RequestParam(name = "dataVencFim") LocalDate dataVencimentoFim) {
        return contaService.getValorTotalPeriodo(dataVencimentoInicio, dataVencimentoFim);
    }

    @PostMapping("/upload")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> uploadCsv(@RequestParam("file") MultipartFile file, Principal principal) throws IOException {
        contaService.importFromFile(file, principal.getName());
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body("Arquivo importado com sucesso");
    }

}
