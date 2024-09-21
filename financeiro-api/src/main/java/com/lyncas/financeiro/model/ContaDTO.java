package com.lyncas.financeiro.model;

import com.lyncas.financeiro.persistence.model.Situacao;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ContaDTO(
        Long id,
        @NotNull LocalDate dataVencimento,
        LocalDate dataPagamento,
        @NotNull BigDecimal valor,
        @NotNull String descricao,
        @NotNull Situacao situacao,
        String usuarioCadastro,
        String usuarioAtualizacao,
        LocalDateTime dataCadastro,
        LocalDateTime dataAtualizacao) {

    public ContaDTO() {
        this(null, null, null, BigDecimal.ZERO, "", null,
                "", "", LocalDateTime.now(), LocalDateTime.now());
    }
}
