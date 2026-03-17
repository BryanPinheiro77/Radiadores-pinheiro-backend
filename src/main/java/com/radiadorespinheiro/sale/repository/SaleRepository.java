package com.radiadorespinheiro.sale.repository;

import com.radiadorespinheiro.sale.domain.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {
    Page<Sale> findAll(Pageable pageable);
    List<Sale> findBySaleDateBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT COALESCE(SUM(s.totalAmount), 0) FROM Sale s WHERE s.saleDate BETWEEN :start AND :end")
    BigDecimal sumTotalAmountBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(SUM(s.totalAmount), 0) FROM Sale s")
    BigDecimal sumTotalAmount();

    @Query("""
    SELECT COALESCE(SUM(
        CASE
            WHEN si.itemType = 'SERVICE' AND si.serviceCost IS NOT NULL
                THEN si.serviceCost * si.quantity
            WHEN si.product IS NOT NULL
                THEN si.product.costPrice * si.quantity
            ELSE 0
        END
    ), 0)
    FROM SaleItem si
    JOIN si.sale s
    WHERE s.saleDate BETWEEN :start AND :end
""")
    BigDecimal sumTotalCostBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}