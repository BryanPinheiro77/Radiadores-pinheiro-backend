package com.radiadorespinheiro.venda.controller;

import com.radiadorespinheiro.venda.dto.SaleRequest;
import com.radiadorespinheiro.venda.dto.SaleResponse;
import com.radiadorespinheiro.venda.service.SaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Sales", description = "Sales management endpoints")
@RestController
@RequestMapping("/sales")
public class SaleController {

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @Operation(summary = "Create sale")
    @PostMapping
    public ResponseEntity<SaleResponse> create(@Valid @RequestBody SaleRequest request) {
        return ResponseEntity.ok(saleService.create(request));
    }

    @Operation(summary = "List all sales (paginated)")
    @GetMapping
    public ResponseEntity<Page<SaleResponse>> findAll(
            @PageableDefault(size = 10, sort = "saleDate") Pageable pageable) {
        return ResponseEntity.ok(saleService.findAll(pageable));
    }

    @Operation(summary = "Find sale by ID")
    @GetMapping("/{id}")
    public ResponseEntity<SaleResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(saleService.findById(id));
    }

    @Operation(summary = "Delete sale and restore stock")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        saleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}