package com.radiadorespinheiro.product.service;

import com.radiadorespinheiro.category.domain.Category;
import com.radiadorespinheiro.category.repository.CategoryRepository;
import com.radiadorespinheiro.common.exception.BusinessException;
import com.radiadorespinheiro.product.domain.Product;
import com.radiadorespinheiro.product.dto.ProductPatchRequest;
import com.radiadorespinheiro.product.dto.ProductRequest;
import com.radiadorespinheiro.product.dto.ProductResponse;
import com.radiadorespinheiro.product.repository.ProductRepository;
import com.radiadorespinheiro.product.repository.specification.ProductSpecification;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @CacheEvict(value = {"products", "products-active", "dashboard-summary"}, allEntries = true)
    public ProductResponse create(ProductRequest request) {
        Category category = resolveCategory(request.categoryId());
        Product product = Product.builder()
                .name(request.name())
                .description(request.description())
                .costPrice(request.costPrice())
                .salePrice(request.salePrice())
                .stock(request.stock())
                .minStock(request.minStock())
                .active(true)
                .category(category)
                .build();
        return toResponse(productRepository.save(product));
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> findAll(String search, Long categoryId, Boolean active, Pageable pageable) {
        pageable = normalizePageable(pageable);

        Specification<Product> spec = Specification
                .where(ProductSpecification.nameContains(search))
                .and(ProductSpecification.categoryEquals(categoryId))
                .and(ProductSpecification.activeEquals(active));

        return productRepository.findAll(spec, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> findActive(Pageable pageable) {
        return findAll(null, null, true, pageable);
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @CacheEvict(value = {"products", "products-active", "dashboard-summary"}, allEntries = true)
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = findOrThrow(id);
        Category category = resolveCategory(request.categoryId());
        product.setName(request.name());
        product.setDescription(request.description());
        product.setCostPrice(request.costPrice());
        product.setSalePrice(request.salePrice());
        product.setStock(request.stock());
        product.setMinStock(request.minStock());
        product.setCategory(category);
        return toResponse(productRepository.save(product));
    }

    @CacheEvict(value = {"products", "products-active", "dashboard-summary"}, allEntries = true)
    public ProductResponse patch(Long id, ProductPatchRequest request) {
        Product product = findOrThrow(id);
        if (request.name() != null && !request.name().isBlank()) product.setName(request.name());
        if (request.description() != null) product.setDescription(request.description());
        if (request.costPrice() != null) product.setCostPrice(request.costPrice());
        if (request.salePrice() != null) product.setSalePrice(request.salePrice());
        if (request.stock() != null) product.setStock(request.stock());
        if (request.minStock() != null) product.setMinStock(request.minStock());
        if (request.categoryId() != null) product.setCategory(resolveCategory(request.categoryId()));
        if (request.active() != null) product.setActive(request.active());
        return toResponse(productRepository.save(product));
    }

    @CacheEvict(value = {"products", "products-active", "dashboard-summary"}, allEntries = true)
    public ProductResponse toggleActive(Long id) {
        Product product = findOrThrow(id);
        product.setActive(!product.getActive());
        return toResponse(productRepository.save(product));
    }

    @CacheEvict(value = {"products", "products-active", "dashboard-summary"}, allEntries = true)
    public void delete(Long id) {
        findOrThrow(id);
        productRepository.deleteById(id);
    }

    private Category resolveCategory(Long categoryId) {
        if (categoryId == null) return null;
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException("Category not found"));
    }

    private Product findOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Product not found with id: " + id));
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

    private ProductResponse toResponse(Product p) {
        return new ProductResponse(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getCostPrice(),
                p.getSalePrice(),
                p.getStock(),
                p.getMinStock(),
                p.getActive(),
                p.getCategory() != null ? p.getCategory().getName() : null
        );
    }
}