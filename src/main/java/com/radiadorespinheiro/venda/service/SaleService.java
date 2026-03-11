package com.radiadorespinheiro.venda.service;

import com.radiadorespinheiro.common.exception.BusinessException;
import com.radiadorespinheiro.produto.domain.Product;
import com.radiadorespinheiro.produto.repository.ProductRepository;
import com.radiadorespinheiro.venda.domain.ItemType;
import com.radiadorespinheiro.venda.domain.Sale;
import com.radiadorespinheiro.venda.domain.SaleItem;
import com.radiadorespinheiro.venda.dto.*;
import com.radiadorespinheiro.venda.repository.SaleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;

    public SaleService(SaleRepository saleRepository, ProductRepository productRepository) {
        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public SaleResponse create(SaleRequest request) {
        List<SaleItem> items = request.items().stream()
                .map(this::buildItem)
                .toList();

        BigDecimal total = items.stream()
                .map(SaleItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Sale sale = Sale.builder()
                .customerName(request.customerName())
                .saleDate(LocalDateTime.now())
                .totalAmount(total)
                .notes(request.notes())
                .items(items)
                .build();

        items.forEach(item -> item.setSale(sale));

        return toResponse(saleRepository.save(sale));
    }

    public List<SaleResponse> findAll() {
        return saleRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public SaleResponse findById(Long id) {
        return toResponse(findOrThrow(id));
    }

    private SaleItem buildItem(SaleItemRequest req) {
        BigDecimal total = req.unitPrice().multiply(BigDecimal.valueOf(req.quantity()));

        if (req.itemType() == ItemType.PRODUCT) {
            if (req.productId() == null) {
                throw new BusinessException("productId is required for PRODUCT items");
            }
            Product product = productRepository.findById(req.productId())
                    .orElseThrow(() -> new BusinessException("Product not found"));

            if (product.getStock() < req.quantity()) {
                throw new BusinessException("Insufficient stock for product: " + product.getName());
            }

            product.setStock(product.getStock() - req.quantity());
            productRepository.save(product);

            return SaleItem.builder()
                    .product(product)
                    .description(req.description())
                    .quantity(req.quantity())
                    .unitPrice(req.unitPrice())
                    .totalPrice(total)
                    .itemType(ItemType.PRODUCT)
                    .build();
        }

        return SaleItem.builder()
                .description(req.description())
                .quantity(req.quantity())
                .unitPrice(req.unitPrice())
                .totalPrice(total)
                .itemType(ItemType.SERVICE)
                .build();
    }

    @Transactional
    public void delete(Long id) {
        Sale sale = findOrThrow(id);

        // Devolve estoque dos itens do tipo PRODUCT
        sale.getItems().forEach(item -> {
            if (item.getItemType() == ItemType.PRODUCT && item.getProduct() != null) {
                Product product = item.getProduct();
                product.setStock(product.getStock() + item.getQuantity());
                productRepository.save(product);
            }
        });

        saleRepository.delete(sale);
    }

    private Sale findOrThrow(Long id) {
        return saleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Sale not found with id: " + id));
    }

    private SaleResponse toResponse(Sale sale) {
        List<SaleItemResponse> itemResponses = sale.getItems().stream()
                .map(i -> new SaleItemResponse(
                        i.getId(), i.getItemType(), i.getDescription(),
                        i.getQuantity(), i.getUnitPrice(), i.getTotalPrice()))
                .toList();

        return new SaleResponse(
                sale.getId(), sale.getCustomerName(), sale.getSaleDate(),
                sale.getTotalAmount(), sale.getNotes(), itemResponses);
    }
}