package com.lyncas.financeiro.model;

import com.lyncas.financeiro.persistence.model.Situacao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ContaDTO(
        Long id, LocalDate dataVencimento, LocalDate dataPagamento,
        BigDecimal valor, String descricao, Situacao situacao,
        String usuarioCadastro, String usuarioAtualizacao, LocalDateTime dataCadastro, LocalDateTime dataAtualizacao) {

    public ContaDTO() {
        this(null, null, null, BigDecimal.ZERO, "", null, "", "", LocalDateTime.now(), LocalDateTime.now());
    }
}
