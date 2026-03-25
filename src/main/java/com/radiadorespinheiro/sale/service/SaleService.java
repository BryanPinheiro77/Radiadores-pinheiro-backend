package com.radiadorespinheiro.sale.service;

import com.radiadorespinheiro.category.domain.Category;
import com.radiadorespinheiro.category.repository.CategoryRepository;
import com.radiadorespinheiro.common.exception.BusinessException;
import com.radiadorespinheiro.product.domain.Product;
import com.radiadorespinheiro.product.repository.ProductRepository;
import com.radiadorespinheiro.sale.domain.ItemType;
import com.radiadorespinheiro.sale.domain.Sale;
import com.radiadorespinheiro.sale.domain.SaleItem;
import com.radiadorespinheiro.sale.dto.*;
import com.radiadorespinheiro.sale.repository.SaleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.CacheEvict;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public SaleService(SaleRepository saleRepository, ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @CacheEvict(value = "dashboard-summary", allEntries = true)
    @Transactional
    public SaleResponse create(SaleRequest request) {
        List<SaleItem> items = request.items().stream()
                .map(this::buildItem)
                .toList();

        if (request.discountValue() != null && request.discountPercentual() != null) {
            throw new BusinessException("Use either discountValue or discountPercentual, not both");
        }

        BigDecimal subtotal = items.stream()
                .map(SaleItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discount = BigDecimal.ZERO;
        if (request.discountValue() != null) {
            discount = request.discountValue();
        } else if (request.discountPercentual() != null) {
            discount = subtotal.multiply(request.discountPercentual().divide(BigDecimal.valueOf(100)));
        }

        BigDecimal total = subtotal.subtract(discount);
        if (total.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Discount cannot be greater than the total amount");
        }

        Sale sale = Sale.builder()
                .customerName(request.customerName())
                .saleDate(LocalDateTime.now())
                .subtotal(subtotal)
                .discountValue(request.discountValue())
                .discountPercentual(request.discountPercentual())
                .totalAmount(total)
                .notes(request.notes())
                .items(items)
                .build();

        items.forEach(item -> item.setSale(sale));

        return toResponse(saleRepository.save(sale));
    }

    @Transactional(readOnly = true)
    public Page<SaleResponse> findAll(Pageable pageable) {
        return saleRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
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

        Category category = null;
        if (req.categoryId() != null) {
            category = categoryRepository.findById(req.categoryId()).orElse(null);
        }

        return SaleItem.builder()
                .description(req.description())
                .quantity(req.quantity())
                .unitPrice(req.unitPrice())
                .totalPrice(total)
                .itemType(ItemType.SERVICE)
                .category(category)
                .serviceCost(req.serviceCost())
                .build();
    }

    @CacheEvict(value = "dashboard-summary", allEntries = true)
    @Transactional
    public void delete(Long id) {
        Sale sale = findOrThrow(id);

        sale.getItems().forEach(item -> {
            if (item.getItemType() == ItemType.PRODUCT && item.getProduct() != null) {
                Product product = item.getProduct();
                product.setStock(product.getStock() + item.getQuantity());
                productRepository.save(product);
            }
        });

        saleRepository.deleteById(id);
    }

    private Sale findOrThrow(Long id) {
        return saleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Sale not found with id: " + id));
    }

    private Pageable normalizePageable(Pageable pageable) {
        int maxSize = 50;
        int size = Math.min(pageable.getPageSize(), maxSize);
        return PageRequest.of(
                pageable.getPageNumber(),
                size,
                pageable.getSort()
        );
    }

    private SaleResponse toResponse(Sale sale) {
        List<SaleItemResponse> itemResponses = sale.getItems().stream()
                .map(i -> new SaleItemResponse(
                        i.getId(),
                        i.getItemType(),
                        i.getDescription(),
                        i.getQuantity(),
                        i.getUnitPrice(),
                        i.getTotalPrice(),
                        i.getCategory() != null ? i.getCategory().getName() : null,
                        i.getServiceCost()))
                .toList();

        return new SaleResponse(
                sale.getId(), sale.getCustomerName(), sale.getSaleDate(),
                sale.getSubtotal(), sale.getDiscountValue(), sale.getDiscountPercentual(),
                sale.getTotalAmount(), sale.getNotes(), itemResponses);
    }
}