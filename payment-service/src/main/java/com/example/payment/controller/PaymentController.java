package com.example.payment.controller;

import com.example.payment.proto.*;
import com.example.payment.service.PaymentGrpcService;
import io.grpc.StatusRuntimeException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Tag(name = "Payment API", description = "Endpoints for payment processing")
public class PaymentController {

    private final PaymentGrpcService paymentGrpcService;

    @PostMapping
    @Operation(
        summary = "Create a new payment",
        description = "Process a new payment with the provided details"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Payment processed successfully",
        content = @Content(schema = @Schema(implementation = PaymentResponse.class))
    )
    @ApiResponse(
        responseCode = "400",
        description = "Invalid payment request"
    )
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error"
    )
    public ResponseEntity<PaymentResponse> createPayment(@RequestBody CreatePaymentRequest request) {
        try {
            PaymentResponse response = paymentGrpcService.createPayment(request);
            return ResponseEntity.ok(response);
        } catch (StatusRuntimeException e) {
            return ResponseEntity.status(e.getStatus().getCode().value())
                    .body(PaymentResponse.newBuilder()
                            .setPaymentId(0)  // Invalid payment ID for error case
                            .setStatus("ERROR: " + e.getStatus().getDescription())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(PaymentResponse.newBuilder()
                            .setPaymentId(0)  // Invalid payment ID for error case
                            .setStatus("ERROR: Internal server error: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/{paymentId}")
    @Operation(
        summary = "Get payment status",
        description = "Retrieve the status of a payment by its ID"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Payment status retrieved successfully",
        content = @Content(schema = @Schema(implementation = PaymentResponse.class))
    )
    @ApiResponse(
        responseCode = "400",
        description = "Invalid payment ID"
    )
    @ApiResponse(
        responseCode = "404",
        description = "Payment not found"
    )
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error"
    )
    public ResponseEntity<PaymentResponse> getPaymentStatus(@PathVariable Long paymentId) {
        try {
            PaymentStatusRequest request = PaymentStatusRequest.newBuilder()
                    .setPaymentId(paymentId)
                    .build();
            PaymentResponse response = paymentGrpcService.getPaymentStatus(request);
            return ResponseEntity.ok(response);
        } catch (StatusRuntimeException e) {
            return ResponseEntity.status(e.getStatus().getCode().value())
                    .body(PaymentResponse.newBuilder()
                            .setPaymentId(0)  // Invalid payment ID for error case
                            .setStatus("ERROR: " + e.getStatus().getDescription())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(PaymentResponse.newBuilder()
                            .setPaymentId(0)  // Invalid payment ID for error case
                            .setStatus("ERROR: Internal server error: " + e.getMessage())
                            .build());
        }
    }
}
