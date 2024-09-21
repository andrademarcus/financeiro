package com.lyncas.financeiro.mapper;

import com.lyncas.financeiro.model.ContaDTO;
import com.lyncas.financeiro.persistence.model.Conta;

import java.util.function.Function;

public class ContaMapper {
    public static Function<Conta, ContaDTO> transformToDTO = (conta) -> new ContaDTO(
            conta.getId(),
            conta.getDataVencimento(),
            conta.getDataPagamento(),
            conta.getValor(),
            conta.getDescricao(),
            conta.getSituacao(),
            conta.getUsuarioCadastro(),
            conta.getUsuarioAtualizacao(),
            conta.getDataCadastro(),
            conta.getDataAtualizacao()
    );

    public static Function<ContaDTO, Conta> transformToEntity = (conta) -> new Conta(
            conta.dataVencimento(),
            conta.dataPagamento(),
            conta.valor(),
            conta.descricao(),
            conta.situacao()
    );
}
