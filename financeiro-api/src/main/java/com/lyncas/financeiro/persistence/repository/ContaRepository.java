package com.lyncas.financeiro.persistence.repository;

import com.lyncas.financeiro.persistence.model.Conta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {

    @Query("SELECT c FROM Conta c WHERE (CAST(:vencimentoInicio AS DATE) IS NULL OR c.dataVencimento >= :vencimentoInicio) AND (CAST(:vencimentoFim AS DATE) IS NULL OR c.dataVencimento <= :vencimentoFim) AND (:descricao IS NULL OR LOWER(c.descricao) LIKE LOWER(CONCAT('%', :descricao, '%'))) AND (situacao = 'PENDENTE')")
    Page<Conta> findByDataVencimentoAndDescricao(@Param("vencimentoInicio") LocalDate vencimentoInicio,
                                            @Param("vencimentoFim") LocalDate vencimentoFim,
                                            @Param("descricao") String descricao,
                                            Pageable pageable);


    @Query("SELECT SUM(c.valor) FROM Conta c WHERE (CAST(:vencimentoInicio AS DATE) IS NULL OR c.dataVencimento >= :vencimentoInicio) AND (CAST(:vencimentoFim AS DATE) IS NULL OR c.dataVencimento <= :vencimentoFim) AND (situacao = 'PAGO')")
    Optional<BigDecimal> getValorTotalPeriodo(@Param("vencimentoInicio") LocalDate vencimentoInicio, @Param("vencimentoFim") LocalDate vencimentoFim);

}
