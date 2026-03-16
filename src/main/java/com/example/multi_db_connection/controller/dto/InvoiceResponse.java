package com.example.multi_db_connection.controller.dto;

import java.util.UUID;

public record InvoiceResponse(UUID id, String name, String description) {
}

