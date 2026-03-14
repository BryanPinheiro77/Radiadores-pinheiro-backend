package com.radiadorespinheiro.restock.domain;

import com.radiadorespinheiro.product.domain.Product;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "restock_order_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestockOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private RestockOrder order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private Integer suggestedQuantity;

    @Column(nullable = false)
    private Integer orderedQuantity;
}