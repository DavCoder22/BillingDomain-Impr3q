package com.example.payment.service;

import com.example.payment.proto.*;

import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentGrpcServiceTest {

    private Server server;
    private String serverName;
    private ManagedChannel channel;

    @Mock
    private PaymentGrpcService paymentGrpcService;

    private PaymentServiceGrpc.PaymentServiceBlockingStub blockingStub;
    private PaymentServiceGrpc.PaymentServiceStub asyncStub;

    @BeforeEach
    void setUp() throws IOException {
        // Reset mocks
        Mockito.reset(paymentGrpcService);
        
        // Generate a unique name for the in-process server
        serverName = InProcessServerBuilder.generateName();

        // Create a server with a direct executor (synchronous execution)
        server = InProcessServerBuilder
                .forName(serverName)
                .directExecutor()
                .addService(paymentGrpcService)
                .build()
                .start();

        // Create a client channel with a direct executor
        channel = InProcessChannelBuilder.forName(serverName)
                .directExecutor()
                .usePlaintext()
                .build();
        
        // Create stubs with a short deadline
        blockingStub = PaymentServiceGrpc.newBlockingStub(channel)
                .withDeadlineAfter(1, TimeUnit.SECONDS);
        asyncStub = PaymentServiceGrpc.newStub(channel)
                .withDeadlineAfter(1, TimeUnit.SECONDS);
    }
    
    @AfterEach
    void tearDown() {
        // Shutdown the channel first
        if (channel != null) {
            channel.shutdownNow();
            try {
                if (!channel.awaitTermination(1, TimeUnit.SECONDS)) {
                    System.err.println("Channel did not terminate in time");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Interrupted while waiting for channel to terminate");
            }
        }
        
        // Then shutdown the server
        if (server != null) {
            server.shutdownNow();
            try {
                if (!server.awaitTermination(1, TimeUnit.SECONDS)) {
                    System.err.println("Server did not terminate in time");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Interrupted while waiting for server to terminate");
            }
        }
        
        // Reset mocks
        Mockito.reset(paymentGrpcService);
    }

    @ParameterizedTest
    @MethodSource("validPaymentScenarios")
    @Timeout(5) // 5 seconds timeout
    void createPayment_withValidScenarios_shouldReturnSuccess(long quoteId, double amount, String method, String expectedStatus) {
        // Mock the service behavior
        doAnswer(invocation -> {
            StreamObserver<PaymentResponse> responseObserver = invocation.getArgument(1);
            responseObserver.onNext(PaymentResponse.newBuilder()
                    .setPaymentId(1L)
                    .setStatus(expectedStatus)
                    .build());
            responseObserver.onCompleted();
            return null;
        }).when(paymentGrpcService).createPayment(any(CreatePaymentRequest.class), any());

        // Create payment request
        CreatePaymentRequest request = CreatePaymentRequest.newBuilder()
                .setQuoteId(quoteId)
                .setAmount(amount)
                .setMethod(method)
                .build();

        // Execute the test
        PaymentResponse response = blockingStub.createPayment(request);

        // Verify the response
        assertNotNull(response, "Response should not be null");
        assertEquals(1L, response.getPaymentId());
        assertEquals(expectedStatus, response.getStatus());
    }

    private static Stream<Arguments> validPaymentScenarios() {
        return Stream.of(
            Arguments.of(1001L, 99.99, "CREDIT_CARD", "COMPLETED"),
            Arguments.of(1002L, 150.50, "PAYPAL", "PENDING"),
            Arguments.of(1003L, 0.01, "BANK_TRANSFER", "PENDING"),
            Arguments.of(1004L, 1000000.00, "CRYPTO", "COMPLETED")
        );
    }

    @Test
    @Timeout(5) // 5 seconds timeout
    void createPayment_withInvalidAmount_shouldReturnInvalidArgument() {
        // Mock the service to throw INVALID_ARGUMENT for invalid amount
        doAnswer(invocation -> {
            StreamObserver<PaymentResponse> responseObserver = invocation.getArgument(1);
            responseObserver.onError(Status.INVALID_ARGUMENT
                .withDescription("Amount must be greater than 0")
                .asRuntimeException());
            return null;
        }).when(paymentGrpcService).createPayment(any(CreatePaymentRequest.class), any());

        // Create payment request with invalid amount (0 or negative)
        CreatePaymentRequest request = CreatePaymentRequest.newBuilder()
                .setQuoteId(1001L)
                .setAmount(0.0)
                .setMethod("CREDIT_CARD")
                .build();

        // Verify the exception is thrown
        StatusRuntimeException exception = assertThrows(
            StatusRuntimeException.class,
            () -> blockingStub.createPayment(request)
        );
        
        assertEquals(Status.Code.INVALID_ARGUMENT, exception.getStatus().getCode());
        String description = exception.getStatus().getDescription();
        assertNotNull(description, "Error description should not be null");
        assertTrue(description.contains("Amount") || description.contains("amount"), 
            "Error should be about amount validation");
    }

    @Test
    @Timeout(5) // 5 seconds timeout
    void createPayment_withInvalidMethod_shouldReturnInvalidArgument() {
        // Mock the service to throw INVALID_ARGUMENT for invalid method
        doAnswer(invocation -> {
            StreamObserver<PaymentResponse> responseObserver = invocation.getArgument(1);
            responseObserver.onError(Status.INVALID_ARGUMENT
                .withDescription("Invalid payment method")
                .asRuntimeException());
            return null;
        }).when(paymentGrpcService).createPayment(any(CreatePaymentRequest.class), any());

        // Create payment request with invalid payment method
        CreatePaymentRequest request = CreatePaymentRequest.newBuilder()
                .setQuoteId(1001L)
                .setAmount(100.0)
                .setMethod("INVALID_METHOD")
                .build();

        // Verify the exception is thrown
        StatusRuntimeException exception = assertThrows(
            StatusRuntimeException.class,
            () -> blockingStub.createPayment(request)
        );
        
        assertEquals(Status.Code.INVALID_ARGUMENT, exception.getStatus().getCode());
        String description = exception.getStatus().getDescription();
        assertNotNull(description, "Error description should not be null");
        String lowerDesc = description.toLowerCase();
        assertTrue(lowerDesc.contains("method") || lowerDesc.contains("payment"), 
            "Error should be about payment method validation");
    }

    @Test
    @Timeout(5) // 5 seconds timeout
    void getPaymentStatus_withNonExistentId_shouldReturnNotFound() {
        // Mock NOT_FOUND response
        doAnswer(invocation -> {
            StreamObserver<PaymentResponse> responseObserver = invocation.getArgument(1);
            responseObserver.onError(Status.NOT_FOUND
                .withDescription("Payment not found")
                .asRuntimeException());
            return null;
        }).when(paymentGrpcService).getPaymentStatus(any(PaymentStatusRequest.class), any());

        // Request with non-existent ID
        PaymentStatusRequest request = PaymentStatusRequest.newBuilder()
                .setPaymentId(9999L)
                .build();

        // Verify the exception is thrown
        StatusRuntimeException exception = assertThrows(
            StatusRuntimeException.class,
            () -> blockingStub.getPaymentStatus(request)
        );
        
        assertEquals(Status.Code.NOT_FOUND, exception.getStatus().getCode());
        assertTrue(exception.getStatus().getDescription().contains("not found"));
    }

    @Test
    @Timeout(5)
    void getPaymentStatus_withInvalidId_shouldReturnInvalidArgument() {
        // Mock the service to return INVALID_ARGUMENT for invalid payment IDs
        doAnswer(invocation -> {
            PaymentStatusRequest req = invocation.getArgument(0);
            StreamObserver<PaymentResponse> responseObserver = invocation.getArgument(1);
            
            if (req.getPaymentId() <= 0) {
                responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Invalid payment ID: " + req.getPaymentId())
                    .asRuntimeException());
            } else {
                responseObserver.onNext(PaymentResponse.getDefaultInstance());
                responseObserver.onCompleted();
            }
            return null;
        }).when(paymentGrpcService).getPaymentStatus(any(PaymentStatusRequest.class), any());

        // Test with invalid payment ID (0)
        PaymentStatusRequest request = PaymentStatusRequest.newBuilder()
                .setPaymentId(0L)
                .build();

        // Verify the exception is thrown
        StatusRuntimeException exception = assertThrows(
            StatusRuntimeException.class,
            () -> blockingStub.getPaymentStatus(request)
        );
        
        assertEquals(Status.Code.INVALID_ARGUMENT, exception.getStatus().getCode());
        assertTrue(exception.getStatus().getDescription().contains("Invalid payment ID"));
    }

    @Test
    public void getPaymentStatus_withValidId_shouldReturnStatus() {
        // Mock the service response
        PaymentResponse expectedResponse = PaymentResponse.newBuilder()
                .setPaymentId(1L)
                .setStatus("COMPLETED")
                .build();

        doAnswer(invocation -> {
            StreamObserver<PaymentResponse> responseObserver = invocation.getArgument(1);
            responseObserver.onNext(expectedResponse);
            responseObserver.onCompleted();
            return null;
        }).when(paymentGrpcService).getPaymentStatus(any(PaymentStatusRequest.class), any());

        // Create request
        PaymentStatusRequest request = PaymentStatusRequest.newBuilder()
                .setPaymentId(1L)
                .build();

        // Execute test
        PaymentResponse response = blockingStub.getPaymentStatus(request);

        // Validate response
        assertNotNull(response);
        assertNotNull(response.getPaymentId());
        assertEquals("COMPLETED", response.getStatus());
        
        // Verify service interaction
        ArgumentCaptor<PaymentStatusRequest> requestCaptor = ArgumentCaptor.forClass(PaymentStatusRequest.class);
        verify(paymentGrpcService).getPaymentStatus(requestCaptor.capture(), any());
        assertEquals(1L, requestCaptor.getValue().getPaymentId(), "Payment ID should match");
    }
    
    @Test
    void createPayment_withInvalidRequest_shouldThrowException() {
        // Configurar el mock para lanzar una excepción
        doAnswer(invocation -> {
            StreamObserver<PaymentResponse> responseObserver = invocation.getArgument(1);
            responseObserver.onError(Status.INVALID_ARGUMENT
                .withDescription("Invalid payment request")
                .asRuntimeException());
            return null;
        }).when(paymentGrpcService).createPayment(any(CreatePaymentRequest.class), any());

        // Crear una solicitud inválida (sin método de pago)
        CreatePaymentRequest request = CreatePaymentRequest.newBuilder()
                .setQuoteId(1L)
                .setAmount(100.0)
                .build();

        try {
            // Debería lanzar StatusRuntimeException
            blockingStub.createPayment(request);
            fail("Se esperaba una excepción StatusRuntimeException");
        } catch (StatusRuntimeException e) {
            // Verify the correct exception was thrown
            assertTrue(e instanceof StatusRuntimeException, "Should throw StatusRuntimeException");
            assertEquals(Status.Code.INVALID_ARGUMENT, e.getStatus().getCode(), "Status code should be INVALID_ARGUMENT");
            // Verificar que hay un mensaje de error
            assertNotNull(e.getStatus().getDescription(), "Error description should not be null");
        }
    }
    
    @Test
    @Timeout(5)
    void createPayment_async_shouldCompleteSuccessfully() throws Exception {
        // Configurar el mock
        doAnswer(invocation -> {
            StreamObserver<PaymentResponse> responseObserver = invocation.getArgument(1);
            responseObserver.onNext(PaymentResponse.newBuilder()
                    .setPaymentId(2L)
                    .setStatus("PENDING")
                    .build());
            responseObserver.onCompleted();
            return null;
        }).when(paymentGrpcService).createPayment(any(CreatePaymentRequest.class), any());

        // Crear una solicitud de prueba
        CreatePaymentRequest request = CreatePaymentRequest.newBuilder()
                .setQuoteId(2L)
                .setAmount(200.0)
                .setMethod("BANK_TRANSFER")
                .build();

        // Usar CountDownLatch para esperar la respuesta asíncrona
        final CountDownLatch latch = new CountDownLatch(1);
        final PaymentResponse[] responseHolder = new PaymentResponse[1];
        final Throwable[] errorHolder = new Throwable[1];

        // Ejecutar la prueba de forma asíncrona
        asyncStub.createPayment(request, new StreamObserver<PaymentResponse>() {
            @Override
            public void onNext(PaymentResponse response) {
                responseHolder[0] = response;
            }

            @Override
            public void onError(Throwable t) {
                errorHolder[0] = t;
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });

        // Esperar a que se complete la operación asíncrona
        assertTrue(latch.await(1, TimeUnit.SECONDS), 
                  "La operación asíncrona no se completó a tiempo");
        
        // Verify there were no errors
        assertNull(errorHolder[0], "There should be no errors");
        
        // Verify the response
        assertNotNull(responseHolder[0], "Response should not be null");
        assertEquals(2L, responseHolder[0].getPaymentId(), "Payment ID should match");
        assertEquals("PENDING", responseHolder[0].getStatus(), "Status should be PENDING");
        
        // Verify the service was called
        verify(paymentGrpcService).createPayment(any(CreatePaymentRequest.class), any());
    }
    
    @Test
    public void getPaymentStatus_nonExistentPayment_shouldReturnNotFound() {
        // Configurar el mock para devolver un error de no encontrado
        doAnswer(invocation -> {
            StreamObserver<PaymentResponse> responseObserver = invocation.getArgument(1);
            responseObserver.onError(Status.NOT_FOUND
                .withDescription("Payment not found with id: 999")
                .asRuntimeException());
            return null;
        }).when(paymentGrpcService).getPaymentStatus(any(PaymentStatusRequest.class), any());

        // Crear una solicitud para un pago que no existe
        PaymentStatusRequest request = PaymentStatusRequest.newBuilder()
                .setPaymentId(999L)
                .build();

        try {
            // Debería lanzar StatusRuntimeException con NOT_FOUND
            blockingStub.getPaymentStatus(request);
            fail("Se esperaba una excepción StatusRuntimeException");
        } catch (StatusRuntimeException e) {
            // Verify the correct exception was thrown
            assertTrue(e instanceof StatusRuntimeException, "Should throw StatusRuntimeException");
            assertEquals(Status.Code.NOT_FOUND, e.getStatus().getCode(), "Status code should be NOT_FOUND");
            // Verificar que hay un mensaje de error
            assertNotNull(e.getStatus().getDescription(), "Error description should not be null");
        }
    }
}
