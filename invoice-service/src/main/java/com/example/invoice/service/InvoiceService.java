package com.example.invoice.service;

import com.example.invoice.dto.GenerateInvoiceRequest;
import com.example.invoice.dto.InvoiceResponse;
import com.example.invoice.model.Invoice;
import com.example.invoice.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class InvoiceService {
    
    @Autowired
    private InvoiceRepository invoiceRepository;
    
    public InvoiceResponse generateInvoice(GenerateInvoiceRequest request) {
        // Verificar si ya existe una factura para esta cotización
        Optional<Invoice> existingInvoice = invoiceRepository.findByQuotationId(request.getQuotationId());
        if (existingInvoice.isPresent()) {
            throw new RuntimeException("Ya existe una factura para la cotización: " + request.getQuotationId());
        }
        
        // Crear nueva factura
        Invoice invoice = new Invoice();
        invoice.setQuotationId(request.getQuotationId());
        invoice.setPaymentReference(request.getPaymentReference());
        invoice.setCustomerEmail(request.getCustomerEmail());
        invoice.setAmount(request.getAmount());
        invoice.setInvoiceNumber(generateInvoiceNumber());
        invoice.setStatus("GENERATED");
        invoice.setPdfUrl(generatePdfUrl(invoice.getInvoiceNumber()));
        
        Invoice savedInvoice = invoiceRepository.save(invoice);
        
        return convertToResponse(savedInvoice);
    }
    
    public InvoiceResponse getInvoice(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada con ID: " + id));
        
        return convertToResponse(invoice);
    }
    
    public InvoiceResponse getInvoiceByQuotationId(Long quotationId) {
        Invoice invoice = invoiceRepository.findByQuotationId(quotationId)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada para cotización: " + quotationId));
        
        return convertToResponse(invoice);
    }
    
    public InvoiceResponse getInvoiceByInvoiceNumber(String invoiceNumber) {
        Invoice invoice = invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada con número: " + invoiceNumber));
        
        return convertToResponse(invoice);
    }
    
    private String generateInvoiceNumber() {
        // Generar número de factura único
        return "INV-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    private String generatePdfUrl(String invoiceNumber) {
        // Simular URL del PDF generado
        return "https://api.print3d.com/invoices/" + invoiceNumber + ".pdf";
    }
    
    private InvoiceResponse convertToResponse(Invoice invoice) {
        return new InvoiceResponse(
                invoice.getId(),
                invoice.getInvoiceNumber(),
                invoice.getQuotationId(),
                invoice.getPaymentReference(),
                invoice.getCustomerEmail(),
                invoice.getAmount(),
                invoice.getStatus(),
                invoice.getPdfUrl(),
                invoice.getCreatedAt()
        );
    }
} 