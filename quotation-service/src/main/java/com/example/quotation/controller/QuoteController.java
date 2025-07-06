package com.example.quotation.controller;

import com.example.quotation.dto.QuoteRequest;
import com.example.quotation.model.Quote;
import com.example.quotation.service.QuoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/quotes")
@Tag(name = "Quotation Management", description = "APIs para gestionar cotizaciones de impresión 3D")
public class QuoteController {

    private final QuoteService quoteService;

    public QuoteController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @PostMapping
    @Operation(
        summary = "Crear una nueva cotización",
        description = "Crea una nueva cotización para un proyecto de impresión 3D"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Cotización creada exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Quote.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos de entrada inválidos"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor"
        )
    })
    public ResponseEntity<Quote> create(
        @Parameter(description = "Datos de la cotización a crear", required = true)
        @RequestBody QuoteRequest request
    ) {
        return ResponseEntity.ok(quoteService.createQuote(request));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener cotización por ID",
        description = "Recupera una cotización específica por su identificador único"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Cotización encontrada",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Quote.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Cotización no encontrada"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor"
        )
    })
    public ResponseEntity<Quote> getById(
        @Parameter(description = "ID de la cotización", required = true, example = "1")
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(quoteService.getById(id));
    }
}
