package com.example.quotation.service;

import com.example.quotation.dto.QuoteRequest;
import com.example.quotation.model.Quote;
import com.example.quotation.repository.QuoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuoteServiceTest {

    @Mock
    private QuoteRepository quoteRepository;

    @InjectMocks
    private QuoteService quoteService;

    private QuoteRequest testRequest;
    private Quote savedQuote;

    @BeforeEach
    void setUp() {
        // Configuración común para las pruebas
        testRequest = new QuoteRequest();
        testRequest.setMaterial("PLA");
        testRequest.setVolumeCm3(100.0);
        testRequest.setPrintTimeHours(2.0);

        savedQuote = new Quote();
        savedQuote.setId(1L);
        savedQuote.setMaterial(testRequest.getMaterial());
        savedQuote.setVolumeCm3(testRequest.getVolumeCm3());
        savedQuote.setPrintTimeHours(testRequest.getPrintTimeHours());
        savedQuote.setPrice(new BigDecimal("22.00"));
    }

    @Test
    void createQuote_ShouldCalculatePriceCorrectly() {
        // Arrange
        when(quoteRepository.save(any(Quote.class))).thenReturn(savedQuote);

        // Act
        Quote result = quoteService.createQuote(testRequest);

        // Assert
        assertNotNull(result);
        assertEquals(testRequest.getMaterial(), result.getMaterial());
        assertEquals(testRequest.getVolumeCm3(), result.getVolumeCm3());
        assertEquals(testRequest.getPrintTimeHours(), result.getPrintTimeHours());
        
        // Verificar que el precio se calculó correctamente (100 * 0.12 + 2 * 5.0) * 1.0 = 22.00
        assertEquals(0, new BigDecimal("22.00").compareTo(result.getPrice()));
        
        verify(quoteRepository, times(1)).save(any(Quote.class));
    }

    @Test
    void createQuote_ShouldHandleDifferentMaterials() {
        // Arrange
        testRequest.setMaterial("ABS"); // Factor de 1.2
        savedQuote.setMaterial("ABS");
        savedQuote.setPrice(new BigDecimal("26.40")); // (100*0.12 + 2*5.0) * 1.2 = 26.40
        when(quoteRepository.save(any(Quote.class))).thenReturn(savedQuote);

        // Act
        Quote result = quoteService.createQuote(testRequest);

        // Assert
        assertNotNull(result);
        assertEquals("ABS", result.getMaterial());
        assertEquals(0, new BigDecimal("26.40").compareTo(result.getPrice()));
    }

    @Test
    void createQuote_ShouldHandleUnknownMaterial() {
        // Arrange
        testRequest.setMaterial("UNKNOWN"); // Debería usar el factor por defecto de 1.3
        savedQuote.setMaterial("UNKNOWN");
        savedQuote.setPrice(new BigDecimal("28.60")); // (100*0.12 + 2*5.0) * 1.3 = 28.60
        when(quoteRepository.save(any(Quote.class))).thenReturn(savedQuote);

        // Act
        Quote result = quoteService.createQuote(testRequest);

        // Assert
        assertNotNull(result);
        assertEquals("UNKNOWN", result.getMaterial());
        assertEquals(0, new BigDecimal("28.60").compareTo(result.getPrice()));
    }

    @Test
    void getById_ShouldReturnQuote_WhenExists() {
        // Arrange
        Long quoteId = 1L;
        when(quoteRepository.findById(quoteId)).thenReturn(Optional.of(savedQuote));

        // Act
        Quote result = quoteService.getById(quoteId);

        // Assert
        assertNotNull(result);
        assertEquals(quoteId, result.getId());
        verify(quoteRepository, times(1)).findById(quoteId);
    }

    @Test
    void getById_ShouldThrowException_WhenNotFound() {
        // Arrange
        Long nonExistentId = 999L;
        when(quoteRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> quoteService.getById(nonExistentId));
        verify(quoteRepository, times(1)).findById(nonExistentId);
    }

    @Test
    void createQuote_ShouldSetCorrectFields() {
        // Arrange
        when(quoteRepository.save(any(Quote.class))).thenAnswer(invocation -> {
            Quote quote = invocation.getArgument(0);
            quote.setId(1L);
            return quote;
        });

        // Act
        Quote result = quoteService.createQuote(testRequest);

        // Assert
        assertNotNull(result.getId());
        assertEquals(testRequest.getMaterial(), result.getMaterial());
        assertEquals(testRequest.getVolumeCm3(), result.getVolumeCm3());
        assertEquals(testRequest.getPrintTimeHours(), result.getPrintTimeHours());
        assertNotNull(result.getPrice());
        assertNotNull(result.getCreatedAt());
    }
}
