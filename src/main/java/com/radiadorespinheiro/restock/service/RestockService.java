package com.radiadorespinheiro.restock.service;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.radiadorespinheiro.common.exception.BusinessException;
import com.radiadorespinheiro.product.domain.Product;
import com.radiadorespinheiro.product.repository.ProductRepository;
import com.radiadorespinheiro.restock.domain.RestockOrder;
import com.radiadorespinheiro.restock.domain.RestockOrderItem;
import com.radiadorespinheiro.restock.dto.*;
import com.radiadorespinheiro.restock.repository.RestockOrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class RestockService {

    private final ProductRepository productRepository;
    private final RestockOrderRepository restockOrderRepository;

    public RestockService(ProductRepository productRepository, RestockOrderRepository restockOrderRepository) {
        this.productRepository = productRepository;
        this.restockOrderRepository = restockOrderRepository;
    }

    public List<RestockSuggestionResponse> getSuggestions(Long categoryId) {
        List<Product> products = categoryId != null
                ? productRepository.findAllByCategory_Id(categoryId)
                : productRepository.findAll();

        return products.stream()
                .filter(p -> Boolean.TRUE.equals(p.getActive()) && p.getStock() < p.getMinStock())
                .map(p -> new RestockSuggestionResponse(
                        p.getId(),
                        p.getName(),
                        p.getCategory() != null ? p.getCategory().getName() : null,
                        p.getStock(),
                        p.getMinStock(),
                        p.getMinStock() - p.getStock()
                ))
                .toList();
    }

    @Transactional
    public RestockOrderResponse createOrder(RestockOrderRequest request) {
        List<RestockOrderItem> items = request.items().stream()
                .map(this::buildItem)
                .toList();

        RestockOrder order = RestockOrder.builder()
                .createdAt(LocalDateTime.now())
                .notes(request.notes())
                .items(items)
                .build();

        items.forEach(item -> item.setOrder(order));

        return toResponse(restockOrderRepository.save(order));
    }

    public List<RestockOrderResponse> findAll() {
        return restockOrderRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public byte[] generatePdf(Long orderId) {
        RestockOrder order = restockOrderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("Restock order not found with id: " + orderId));

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("Pedido de Reposição de estoque", titleFont);
            title.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(title);

            Font subFont = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.GRAY);
            String dateStr = order.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            Paragraph date = new Paragraph("Data: " + dateStr, subFont);
            date.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(date);

            if (order.getNotes() != null && !order.getNotes().isBlank()) {
                document.add(new Paragraph(" "));
                Font notesFont = new Font(Font.HELVETICA, 10, Font.ITALIC);
                document.add(new Paragraph("Observações: " + order.getNotes(), notesFont));
            }

            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{3f, 2f, 1.5f, 1.5f, 1.5f});

            Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);
            String[] headers = {"Produto", "Categoria", "Estoque Atual", "Qtd Sugerida", "Qtd Pedida"};

            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                cell.setBackgroundColor(new Color(41, 128, 185));
                cell.setPadding(6);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            Font cellFont = new Font(Font.HELVETICA, 9);
            boolean alternate = false;

            for (RestockOrderItem item : order.getItems()) {
                Color rowColor = alternate ? new Color(236, 240, 241) : Color.WHITE;
                alternate = !alternate;

                String[] values = {
                        item.getProduct().getName(),
                        item.getProduct().getCategory() != null ? item.getProduct().getCategory().getName() : "-",
                        String.valueOf(item.getProduct().getStock()),
                        String.valueOf(item.getSuggestedQuantity()),
                        String.valueOf(item.getOrderedQuantity())
                };

                for (int i = 0; i < values.length; i++) {
                    PdfPCell cell = new PdfPCell(new Phrase(values[i], cellFont));
                    cell.setBackgroundColor(rowColor);
                    cell.setPadding(5);
                    cell.setHorizontalAlignment(i == 0 ? Element.ALIGN_LEFT : Element.ALIGN_CENTER);
                    table.addCell(cell);
                }
            }

            document.add(table);
            document.close();

            return out.toByteArray();

        } catch (Exception e) {
            throw new BusinessException("Error generating PDF: " + e.getMessage());
        }
    }

    private RestockOrderItem buildItem(RestockOrderItemRequest req) {
        Product product = productRepository.findById(req.productId())
                .orElseThrow(() -> new BusinessException("Product not found with id: " + req.productId()));

        return RestockOrderItem.builder()
                .product(product)
                .suggestedQuantity(req.suggestedQuantity())
                .orderedQuantity(req.orderedQuantity())
                .build();
    }

    private RestockOrderResponse toResponse(RestockOrder order) {
        List<RestockOrderItemResponse> items = order.getItems().stream()
                .map(i -> new RestockOrderItemResponse(
                        i.getId(),
                        i.getProduct().getId(),
                        i.getProduct().getName(),
                        i.getProduct().getCategory() != null ? i.getProduct().getCategory().getName() : null,
                        i.getProduct().getStock(),
                        i.getSuggestedQuantity(),
                        i.getOrderedQuantity()
                ))
                .toList();

        return new RestockOrderResponse(order.getId(), order.getCreatedAt(), order.getNotes(), items);
    }
}