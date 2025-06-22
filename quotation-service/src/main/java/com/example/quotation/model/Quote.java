package com.example.quotation.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "quotes")
public class Quote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String material;
    private double volumeCm3;
    private double printTimeHours;
    private BigDecimal price;

    private OffsetDateTime createdAt = OffsetDateTime.now();

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }
    public double getVolumeCm3() { return volumeCm3; }
    public void setVolumeCm3(double volumeCm3) { this.volumeCm3 = volumeCm3; }
    public double getPrintTimeHours() { return printTimeHours; }
    public void setPrintTimeHours(double printTimeHours) { this.printTimeHours = printTimeHours; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
