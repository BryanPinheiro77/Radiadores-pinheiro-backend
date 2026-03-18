package com.radiadorespinheiro.product.controller;

import com.radiadorespinheiro.product.dto.ProductPatchRequest;
import com.radiadorespinheiro.product.dto.ProductRequest;
import com.radiadorespinheiro.product.dto.ProductResponse;
import com.radiadorespinheiro.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Products", description = "Product management endpoints")
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Create Product", description = "Creates a new product")
    @PostMapping
    public ResponseEntity<ProductResponse> create(@RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.create(request));
    }

    @Operation(summary = "Find all Products", description = "Find paginated products")
    @GetMapping
    public ResponseEntity<Page<ProductResponse>> findAll(
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(productService.findAll(pageable));
    }

    @Operation(summary = "List active products")
    @GetMapping("/active")
    public ResponseEntity<Page<ProductResponse>> findActive(
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(productService.findActive(pageable));
    }

    @Operation(summary = "Find product by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @Operation(summary = "Update product (full)")
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(@PathVariable Long id, @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.update(id, request));
    }

    @Operation(summary = "Update product (partial)")
    @PatchMapping("/{id}")
    public ResponseEntity<ProductResponse> patch(@PathVariable Long id, @RequestBody ProductPatchRequest request) {
        return ResponseEntity.ok(productService.patch(id, request));
    }

    @Operation(summary = "Toggle product active status")
    @PatchMapping("/{id}/toggle-active")
    public ResponseEntity<ProductResponse> toggleActive(@PathVariable Long id) {
        return ResponseEntity.ok(productService.toggleActive(id));
    }

    @Operation(summary = "Delete product")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}