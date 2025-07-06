package com.example.invoice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InvoiceResponse {
    
    private Long id;
    private String invoiceNumber;
    private Long quotationId;
    private String paymentReference;
    private String customerEmail;
    private BigDecimal amount;
    private String status;
    private String pdfUrl;
    private LocalDateTime createdAt;
    
    // Constructors
    public InvoiceResponse() {}
    
    public InvoiceResponse(Long id, String invoiceNumber, Long quotationId, String paymentReference, 
                          String customerEmail, BigDecimal amount, String status, String pdfUrl, LocalDateTime createdAt) {
        this.id = id;
        this.invoiceNumber = invoiceNumber;
        this.quotationId = quotationId;
        this.paymentReference = paymentReference;
        this.customerEmail = customerEmail;
        this.amount = amount;
        this.status = status;
        this.pdfUrl = pdfUrl;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getInvoiceNumber() {
        return invoiceNumber;
    }
    
    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }
    
    public Long getQuotationId() {
        return quotationId;
    }
    
    public void setQuotationId(Long quotationId) {
        this.quotationId = quotationId;
    }
    
    public String getPaymentReference() {
        return paymentReference;
    }
    
    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }
    
    public String getCustomerEmail() {
        return customerEmail;
    }
    
    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getPdfUrl() {
        return pdfUrl;
    }
    
    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
} 