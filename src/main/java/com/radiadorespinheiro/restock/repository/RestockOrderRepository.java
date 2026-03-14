package com.radiadorespinheiro.restock.repository;

import com.radiadorespinheiro.restock.domain.RestockOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestockOrderRepository extends JpaRepository<RestockOrder, Long> {}