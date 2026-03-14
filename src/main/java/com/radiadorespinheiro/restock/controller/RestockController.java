package com.radiadorespinheiro.restock.controller;

import com.radiadorespinheiro.restock.dto.*;
import com.radiadorespinheiro.restock.service.RestockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Restock", description = "Stock replenishment order management")
@RestController
@RequestMapping("/restock")
public class RestockController {
    private final RestockService restockService;

    public RestockController(RestockService restockService) {
        this.restockService = restockService;
    }

    @Operation(summary = "Get restock suggestions (products below minimum stock)")
    @GetMapping("/suggestions")
    public ResponseEntity<List<RestockSuggestionResponse>> getSuggestions(
            @RequestParam(required = false) Long categoryId) {
        return ResponseEntity.ok(restockService.getSuggestions(categoryId));
    }

    @Operation(summary = "Create restock order with selected and edited items")
    @PostMapping("/orders")
    public ResponseEntity<RestockOrderResponse> createOrder(@Valid @RequestBody RestockOrderRequest request) {
        return ResponseEntity.ok(restockService.createOrder(request));
    }

    @Operation(summary = "List all restock orders")
    @GetMapping("/orders")
    public ResponseEntity<List<RestockOrderResponse>> findAll() {
        return ResponseEntity.ok(restockService.findAll());
    }

    @Operation(summary = "Download restock order as PDF")
    @GetMapping("/orders/{id}/pdf")
    public ResponseEntity<byte[]> generatePdf(@PathVariable Long id) {
        byte[] pdf = restockService.generatePdf(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=restock-order-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

}
