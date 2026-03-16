package com.example.multi_db_connection.service;

import com.example.multi_db_connection.controller.dto.CreateInvoiceRequest;
import com.example.multi_db_connection.controller.dto.InvoiceResponse;
import com.example.multi_db_connection.db2.entity.InvoiceEntity;
import com.example.multi_db_connection.db2.repository.InvoiceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;

    public InvoiceService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    public InvoiceResponse create(CreateInvoiceRequest request) {
        var invoice = InvoiceEntity.builder()
                .name(request.name())
                .description(request.description())
                .build();

        return toResponse(invoiceRepository.save(invoice));
    }

    public InvoiceResponse get(UUID id) {
        return invoiceRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found: " + id));
    }

    public Page<InvoiceResponse> getAll(Pageable pageable) {
        return invoiceRepository.findAll(pageable)
                .map(this::toResponse);
    }

    private InvoiceResponse toResponse(InvoiceEntity invoice) {
        return new InvoiceResponse(invoice.getId(), invoice.getName(), invoice.getDescription());
    }
}
