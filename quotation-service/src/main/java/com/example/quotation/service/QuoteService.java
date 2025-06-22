package com.example.quotation.service;

import com.example.quotation.dto.QuoteRequest;
import com.example.quotation.model.Quote;
import com.example.quotation.repository.QuoteRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class QuoteService {

    private final QuoteRepository repository;

    public QuoteService(QuoteRepository repository) {
        this.repository = repository;
    }

    public Quote createQuote(QuoteRequest request) {
        Quote quote = new Quote();
        quote.setMaterial(request.getMaterial());
        quote.setVolumeCm3(request.getVolumeCm3());
        quote.setPrintTimeHours(request.getPrintTimeHours());
        quote.setPrice(calculatePrice(request));
        return repository.save(quote);
    }

    public Quote getById(Long id) {
        return repository.findById(id).orElseThrow();
    }

    private BigDecimal calculatePrice(QuoteRequest req) {
        double base = 0.12; // USD por cm3
        double timeRate = 5.0; // USD por hora
        double materialFactor = switch (req.getMaterial().toUpperCase()) {
            case "PLA" -> 1.0;
            case "ABS" -> 1.2;
            case "PETG" -> 1.15;
            default -> 1.3;
        };
        double price = (req.getVolumeCm3() * base + req.getPrintTimeHours() * timeRate) * materialFactor;
        return BigDecimal.valueOf(price).setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}
