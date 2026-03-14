package com.radiadorespinheiro.sale.repository;

import com.radiadorespinheiro.sale.domain.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {
    Page<Sale> findAll(Pageable pageable);
    List<Sale> findBySaleDateBetween(LocalDateTime start, LocalDateTime end);
}