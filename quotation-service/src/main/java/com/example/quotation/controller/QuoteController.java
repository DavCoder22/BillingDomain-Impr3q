package com.example.quotation.controller;

import com.example.quotation.dto.QuoteRequest;
import com.example.quotation.model.Quote;
import com.example.quotation.service.QuoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/quotes")
public class QuoteController {

    private final QuoteService quoteService;

    public QuoteController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @PostMapping
    public ResponseEntity<Quote> create(@RequestBody QuoteRequest request) {
        return ResponseEntity.ok(quoteService.createQuote(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Quote> getById(@PathVariable Long id) {
        return ResponseEntity.ok(quoteService.getById(id));
    }
}
