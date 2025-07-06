package com.example.invoice.dto;

import java.math.BigDecimal;

public class GenerateInvoiceRequest {
    
    private Long quotationId;
    private String paymentReference;
    private String customerEmail;
    private BigDecimal amount;
    
    // Constructors
    public GenerateInvoiceRequest() {}
    
    public GenerateInvoiceRequest(Long quotationId, String paymentReference, String customerEmail, BigDecimal amount) {
        this.quotationId = quotationId;
        this.paymentReference = paymentReference;
        this.customerEmail = customerEmail;
        this.amount = amount;
    }
    
    // Getters and Setters
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
} 