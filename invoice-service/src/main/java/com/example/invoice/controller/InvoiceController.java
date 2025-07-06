package com.example.invoice.controller;

import com.example.invoice.dto.GenerateInvoiceRequest;
import com.example.invoice.dto.InvoiceResponse;
import com.example.invoice.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/invoices")
@Tag(name = "Invoice Management", description = "APIs para gestionar facturas de impresión 3D")
public class InvoiceController {
    
    @Autowired
    private InvoiceService invoiceService;
    
    @PostMapping
    @Operation(
        summary = "Generar una nueva factura",
        description = "Crea una nueva factura basada en una cotización existente"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Factura generada exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = InvoiceResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos de entrada inválidos o cotización no encontrada"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor"
        )
    })
    public ResponseEntity<InvoiceResponse> generateInvoice(
        @Parameter(description = "Datos para generar la factura", required = true)
        @RequestBody GenerateInvoiceRequest request
    ) {
        try {
            InvoiceResponse response = invoiceService.generateInvoice(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener factura por ID",
        description = "Recupera una factura específica por su identificador único"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Factura encontrada",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = InvoiceResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Factura no encontrada"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor"
        )
    })
    public ResponseEntity<InvoiceResponse> getInvoice(
        @Parameter(description = "ID de la factura", required = true, example = "1")
        @PathVariable Long id
    ) {
        try {
            InvoiceResponse response = invoiceService.getInvoice(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/quotation/{quotationId}")
    @Operation(
        summary = "Obtener factura por ID de cotización",
        description = "Recupera una factura basada en el ID de la cotización asociada"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Factura encontrada",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = InvoiceResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Factura no encontrada para la cotización especificada"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor"
        )
    })
    public ResponseEntity<InvoiceResponse> getInvoiceByQuotationId(
        @Parameter(description = "ID de la cotización", required = true, example = "1")
        @PathVariable Long quotationId
    ) {
        try {
            InvoiceResponse response = invoiceService.getInvoiceByQuotationId(quotationId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/number/{invoiceNumber}")
    @Operation(
        summary = "Obtener factura por número",
        description = "Recupera una factura basada en su número de factura"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Factura encontrada",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = InvoiceResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Factura no encontrada con el número especificado"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor"
        )
    })
    public ResponseEntity<InvoiceResponse> getInvoiceByNumber(
        @Parameter(description = "Número de factura", required = true, example = "INV-2024-001")
        @PathVariable String invoiceNumber
    ) {
        try {
            InvoiceResponse response = invoiceService.getInvoiceByInvoiceNumber(invoiceNumber);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/health")
    @Operation(
        summary = "Verificar estado del servicio",
        description = "Endpoint de salud para verificar que el servicio de facturación esté funcionando"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Servicio funcionando correctamente",
            content = @Content(
                mediaType = "text/plain",
                schema = @Schema(example = "Invoice Service is running")
            )
        )
    })
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Invoice Service is running");
    }
} 