package com.radiadorespinheiro.categoria.repository;

import com.radiadorespinheiro.categoria.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);
}