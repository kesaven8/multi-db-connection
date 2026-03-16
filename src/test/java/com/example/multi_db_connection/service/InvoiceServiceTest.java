package com.example.multi_db_connection.service;

import com.example.multi_db_connection.controller.dto.InvoiceResponse;
import com.example.multi_db_connection.db2.entity.InvoiceEntity;
import com.example.multi_db_connection.db2.repository.InvoiceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {

	@Mock
	private InvoiceRepository invoiceRepository;

	@InjectMocks
	private InvoiceService invoiceService;

	@Test
	void getAllReturnsPaginatedInvoiceResponses() {
		UUID firstId = UUID.randomUUID();
		UUID secondId = UUID.randomUUID();
		Pageable pageable = PageRequest.of(1, 2);
		Page<InvoiceEntity> invoices = new PageImpl<>(
				List.of(
						InvoiceEntity.builder().id(firstId).name("invoice-3").description("third").build(),
						InvoiceEntity.builder().id(secondId).name("invoice-4").description("fourth").build()
				),
				pageable,
				10
		);

		when(invoiceRepository.findAll(pageable)).thenReturn(invoices);

		Page<InvoiceResponse> result = invoiceService.getAll(pageable);

		assertThat(result.getTotalElements()).isEqualTo(10);
		assertThat(result.getNumber()).isEqualTo(1);
		assertThat(result.getSize()).isEqualTo(2);
		assertThat(result.getContent())
				.containsExactly(
						new InvoiceResponse(firstId, "invoice-3", "third"),
						new InvoiceResponse(secondId, "invoice-4", "fourth")
				);
		verify(invoiceRepository).findAll(pageable);
	}
}

