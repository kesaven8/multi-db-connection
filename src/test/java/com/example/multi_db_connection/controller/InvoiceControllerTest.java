package com.example.multi_db_connection.controller;

import com.example.multi_db_connection.controller.dto.InvoiceResponse;
import com.example.multi_db_connection.service.InvoiceService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class InvoiceControllerTest {

	@Test
	void getAllReturnsPaginatedInvoices() throws Exception {
		InvoiceService invoiceService = mock(InvoiceService.class);
		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new InvoiceController(invoiceService))
				.setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
				.build();
		UUID invoiceId = UUID.randomUUID();

		when(invoiceService.getAll(PageRequest.of(1, 5, org.springframework.data.domain.Sort.by("name").descending())))
				.thenReturn(new PageImpl<>(
						List.of(new InvoiceResponse(invoiceId, "invoice-10", "paged invoice")),
						PageRequest.of(1, 5, org.springframework.data.domain.Sort.by("name").descending()),
						11
				));

		mockMvc.perform(get("/invoices")
						.queryParam("page", "1")
						.queryParam("size", "5")
						.queryParam("sort", "name,desc")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].id").value(invoiceId.toString()))
				.andExpect(jsonPath("$.content[0].name").value("invoice-10"))
				.andExpect(jsonPath("$.content[0].description").value("paged invoice"))
				.andExpect(jsonPath("$.number").value(1))
				.andExpect(jsonPath("$.size").value(5))
				.andExpect(jsonPath("$.totalElements").value(11));

		ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
		verify(invoiceService).getAll(pageableCaptor.capture());
		assertThat(pageableCaptor.getValue().getPageNumber()).isEqualTo(1);
		assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(5);
		Sort.Order nameOrder = pageableCaptor.getValue().getSort().getOrderFor("name");
		assertThat(nameOrder).isNotNull();
		assertThat(nameOrder.isDescending()).isTrue();
	}
}



