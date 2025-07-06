package com.example.payment.service;

import com.example.payment.proto.CreatePaymentRequest;
import com.example.payment.proto.PaymentResponse;
import com.example.payment.proto.PaymentServiceGrpc;
import com.example.payment.proto.PaymentStatusRequest;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class PaymentGrpcService extends PaymentServiceGrpc.PaymentServiceImplBase {
    
    @Override
    public void createPayment(CreatePaymentRequest request, 
                            StreamObserver<PaymentResponse> responseObserver) {
        // Implementación de ejemplo
        PaymentResponse response = PaymentResponse.newBuilder()
                .setPaymentId(1L) // ID de ejemplo
                .setStatus("COMPLETED")
                .build();
                
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getPaymentStatus(PaymentStatusRequest request, 
                               StreamObserver<PaymentResponse> responseObserver) {
        // Implementación de ejemplo
        PaymentResponse response = PaymentResponse.newBuilder()
                .setPaymentId(request.getPaymentId())
                .setStatus("COMPLETED")
                .build();
                
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
