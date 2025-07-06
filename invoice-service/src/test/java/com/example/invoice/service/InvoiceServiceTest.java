package com.example.invoice.service;

import com.example.invoice.dto.GenerateInvoiceRequest;
import com.example.invoice.dto.InvoiceResponse;
import com.example.invoice.model.Invoice;
import com.example.invoice.repository.InvoiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InvoiceServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @InjectMocks
    private InvoiceService invoiceService;

    private GenerateInvoiceRequest testRequest;
    private Invoice savedInvoice;

    @BeforeEach
    void setUp() {
        // Configuración común para las pruebas
        testRequest = new GenerateInvoiceRequest();
        testRequest.setQuotationId(1L);
        testRequest.setPaymentReference("PAY-123");
        testRequest.setCustomerEmail("test@example.com");
        testRequest.setAmount(new BigDecimal("100.00"));

        savedInvoice = new Invoice();
        savedInvoice.setId(1L);
        savedInvoice.setQuotationId(testRequest.getQuotationId());
        savedInvoice.setPaymentReference(testRequest.getPaymentReference());
        savedInvoice.setCustomerEmail(testRequest.getCustomerEmail());
        savedInvoice.setAmount(testRequest.getAmount());
        savedInvoice.setInvoiceNumber("INV-1234567890-ABC12345");
        savedInvoice.setStatus("GENERATED");
        savedInvoice.setPdfUrl("https://api.print3d.com/invoices/INV-1234567890-ABC12345.pdf");
    }

    @Test
    void generateInvoice_ShouldCreateNewInvoice() {
        // Arrange
        when(invoiceRepository.findByQuotationId(testRequest.getQuotationId())).thenReturn(Optional.empty());
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(savedInvoice);

        // Act
        InvoiceResponse result = invoiceService.generateInvoice(testRequest);

        // Assert
        assertNotNull(result);
        assertEquals(testRequest.getQuotationId(), result.getQuotationId());
        assertEquals(testRequest.getPaymentReference(), result.getPaymentReference());
        assertEquals(testRequest.getCustomerEmail(), result.getCustomerEmail());
        assertEquals(testRequest.getAmount(), result.getAmount());
        assertEquals("GENERATED", result.getStatus());
        assertNotNull(result.getInvoiceNumber());
        assertNotNull(result.getPdfUrl());
        
        verify(invoiceRepository, times(1)).findByQuotationId(testRequest.getQuotationId());
        verify(invoiceRepository, times(1)).save(any(Invoice.class));
    }

    @Test
    void generateInvoice_ShouldThrowException_WhenInvoiceAlreadyExists() {
        // Arrange
        when(invoiceRepository.findByQuotationId(testRequest.getQuotationId())).thenReturn(Optional.of(savedInvoice));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> invoiceService.generateInvoice(testRequest));
        verify(invoiceRepository, times(1)).findByQuotationId(testRequest.getQuotationId());
        verify(invoiceRepository, never()).save(any(Invoice.class));
    }

    @Test
    void getInvoice_ShouldReturnInvoice_WhenExists() {
        // Arrange
        Long invoiceId = 1L;
        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(savedInvoice));

        // Act
        InvoiceResponse result = invoiceService.getInvoice(invoiceId);

        // Assert
        assertNotNull(result);
        assertEquals(invoiceId, result.getId());
        verify(invoiceRepository, times(1)).findById(invoiceId);
    }

    @Test
    void getInvoice_ShouldThrowException_WhenNotFound() {
        // Arrange
        Long nonExistentId = 999L;
        when(invoiceRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> invoiceService.getInvoice(nonExistentId));
        verify(invoiceRepository, times(1)).findById(nonExistentId);
    }
} 