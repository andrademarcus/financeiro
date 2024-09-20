package com.lyncas.financeiro.model;

import java.math.BigDecimal;

public record ValorTotalResponse(BigDecimal valorTotal) {

    public ValorTotalResponse() {
        this(BigDecimal.ZERO);
    }

}
