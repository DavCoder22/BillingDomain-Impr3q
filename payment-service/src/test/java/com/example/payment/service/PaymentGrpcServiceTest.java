package com.example.payment.service;

import com.example.payment.proto.*;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(JUnit4.class)
public class PaymentGrpcServiceTest {

    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private PaymentGrpcService paymentGrpcService;

    private PaymentServiceGrpc.PaymentServiceBlockingStub blockingStub;

    @Before
    public void setUp() throws Exception {
        // Generar un nombre único para el servidor en memoria
        String serverName = InProcessServerBuilder.generateName();

        // Crear un servidor en memoria para pruebas
        grpcCleanup.register(InProcessServerBuilder
                .forName(serverName)
                .directExecutor()
                .addService(paymentGrpcService)
                .build()
                .start());

        // Crear un cliente que se conecte al servidor en memoria
        blockingStub = PaymentServiceGrpc.newBlockingStub(
                grpcCleanup.register(InProcessChannelBuilder
                        .forName(serverName)
                        .directExecutor()
                        .build()));
    }

    @Test
    public void createPayment_shouldReturnPaymentResponse() {
        // Configurar el mock
        PaymentResponse mockResponse = PaymentResponse.newBuilder()
                .setPaymentId(1L)
                .setStatus("COMPLETED")
                .build();

        doAnswer(invocation -> {
            io.grpc.stub.StreamObserver<PaymentResponse> responseObserver = invocation.getArgument(1);
            responseObserver.onNext(mockResponse);
            responseObserver.onCompleted();
            return null;
        }).when(paymentGrpcService).createPayment(any(CreatePaymentRequest.class), any());

        // Crear una solicitud de prueba
        CreatePaymentRequest request = CreatePaymentRequest.newBuilder()
                .setQuoteId(1L)
                .setAmount(100.0)
                .setMethod("CREDIT_CARD")
                .build();

        // Ejecutar la prueba
        PaymentResponse response = blockingStub.createPayment(request);

        // Verificar los resultados
        assertNotNull(response);
        assertEquals(1L, response.getPaymentId());
        assertEquals("COMPLETED", response.getStatus());
    }

    @Test
    public void getPaymentStatus_shouldReturnPaymentStatus() {
        // Configurar el mock
        PaymentResponse mockResponse = PaymentResponse.newBuilder()
                .setPaymentId(1L)
                .setStatus("COMPLETED")
                .build();

        doAnswer(invocation -> {
            io.grpc.stub.StreamObserver<PaymentResponse> responseObserver = invocation.getArgument(1);
            responseObserver.onNext(mockResponse);
            responseObserver.onCompleted();
            return null;
        }).when(paymentGrpcService).getPaymentStatus(any(PaymentStatusRequest.class), any());

        // Crear una solicitud de prueba
        PaymentStatusRequest request = PaymentStatusRequest.newBuilder()
                .setPaymentId(1L)
                .build();

        // Ejecutar la prueba
        PaymentResponse response = blockingStub.getPaymentStatus(request);

        // Verificar los resultados
        assertNotNull(response);
        assertEquals(1L, response.getPaymentId());
        assertEquals("COMPLETED", response.getStatus());
    }
}
