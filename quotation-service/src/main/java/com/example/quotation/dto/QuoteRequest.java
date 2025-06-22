package com.example.quotation.dto;

import java.math.BigDecimal;

public class QuoteRequest {
    private String material;
    private double volumeCm3;
    private double printTimeHours;

    // getters and setters
    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }
    public double getVolumeCm3() { return volumeCm3; }
    public void setVolumeCm3(double volumeCm3) { this.volumeCm3 = volumeCm3; }
    public double getPrintTimeHours() { return printTimeHours; }
    public void setPrintTimeHours(double printTimeHours) { this.printTimeHours = printTimeHours; }
}
