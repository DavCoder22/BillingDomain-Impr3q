package com.example.payment.service;

import com.example.payment.proto.CreatePaymentRequest;
import com.example.payment.proto.PaymentResponse;
import com.example.payment.proto.PaymentServiceGrpc;
import com.example.payment.proto.PaymentStatusRequest;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * gRPC service implementation for payment processing.
 * This service handles both gRPC and REST API requests.
 */
@GrpcService
@Service
@Slf4j
public class PaymentGrpcService extends PaymentServiceGrpc.PaymentServiceImplBase {
    
    // In-memory storage for demo purposes (replace with database in production)
    private final ConcurrentMap<Long, PaymentResponse> paymentStore = new ConcurrentHashMap<>();
    
    /**
     * Process a payment request (gRPC endpoint).
     */
    @Override
    public void createPayment(CreatePaymentRequest request, 
                            StreamObserver<PaymentResponse> responseObserver) {
        try {
            PaymentResponse response = createPayment(request);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        } catch (Exception e) {
            log.error("Error processing payment: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error processing payment: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    /**
     * Get payment status (gRPC endpoint).
     */
    @Override
    public void getPaymentStatus(PaymentStatusRequest request, 
                               StreamObserver<PaymentResponse> responseObserver) {
        try {
            PaymentResponse response = getPaymentStatus(request);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        } catch (Exception e) {
            log.error("Error getting payment status: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error getting payment status: " + e.getMessage())
                    .asRuntimeException());
        }
    }
    
    /**
     * Process a payment request (REST endpoint).
     * @param request The payment request
     * @return The payment response
     * @throws StatusRuntimeException if the request is invalid
     */
    public PaymentResponse createPayment(CreatePaymentRequest request) {
        // Validate request
        if (request.getAmount() <= 0) {
            throw Status.INVALID_ARGUMENT
                    .withDescription("Amount must be greater than 0")
                    .asRuntimeException();
        }
        
        if (request.getQuoteId() <= 0) {
            throw Status.INVALID_ARGUMENT
                    .withDescription("Quote ID is required")
                    .asRuntimeException();
        }
        
        // Process payment (in a real application, this would involve actual payment processing)
        long paymentId = Math.abs(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE);
        
        PaymentResponse response = PaymentResponse.newBuilder()
                .setPaymentId(paymentId)
                .setStatus("COMPLETED")
                .build();
        
        // Store the payment (in-memory for demo)
        paymentStore.put(paymentId, response);
        
        log.info("Processed payment: {}", paymentId);
        return response;
    }
    
    /**
     * Get payment status (REST endpoint).
     * @param request The status request
     * @return The payment response
     * @throws StatusRuntimeException if payment is not found
     */
    public PaymentResponse getPaymentStatus(PaymentStatusRequest request) {
        // Validate request
        if (request.getPaymentId() <= 0) {
            throw Status.INVALID_ARGUMENT
                    .withDescription("Invalid payment ID")
                    .asRuntimeException();
        }
        
        // Get payment from store (in-memory for demo)
        PaymentResponse response = paymentStore.get(request.getPaymentId());
        
        if (response == null) {
            throw Status.NOT_FOUND
                    .withDescription("Payment not found with ID: " + request.getPaymentId())
                    .asRuntimeException();
        }
        
        return response;
    }
}
