package com.radiadorespinheiro.product.repository.specification;

import com.radiadorespinheiro.product.domain.Product;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {

    private ProductSpecification() {
    }

    public static Specification<Product> nameContains(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) {
                return cb.conjunction();
            }

            return cb.like(
                    cb.lower(root.get("name")),
                    "%" + search.trim().toLowerCase() + "%"
            );
        };
    }

    public static Specification<Product> categoryEquals(Long categoryId) {
        return (root, query, cb) -> {
            if (categoryId == null) {
                return cb.conjunction();
            }

            return cb.equal(root.get("category").get("id"), categoryId);
        };
    }

    public static Specification<Product> activeEquals(Boolean active) {
        return (root, query, cb) -> {
            if (active == null) {
                return cb.conjunction();
            }

            return cb.equal(root.get("active"), active);
        };
    }
}