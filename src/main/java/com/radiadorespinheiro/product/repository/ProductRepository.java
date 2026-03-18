package com.radiadorespinheiro.product.repository;

import com.radiadorespinheiro.product.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByActiveTrue(Pageable pageable);

    Page<Product> findByCategory_Id(Long categoryId, Pageable pageable);

    // Métodos para uso interno
    List<Product> findAllByCategory_Id(Long categoryId);

    List<Product> findAllByActiveTrue();
}