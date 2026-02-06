package dev.gertjanassies.filament.commands;

import dev.gertjanassies.filament.domain.CostCalculation;
import dev.gertjanassies.filament.service.FilamentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalculateCommandTest_NEW {

    @Mock
    private FilamentService filamentService;

    private ObjectMapper objectMapper;

    private CalculateCommand calculateCommand;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        calculateCommand = new CalculateCommand(filamentService, objectMapper);
    }

    @Test
    void testCalculateCost() throws Exception {
        // Given
        CostCalculation calculation = new CostCalculation("PPLA", 0.41, 12.25);
        when(filamentService.calculateCost("PPLA", 500.0)).thenReturn(calculation);

        // When
        String result = calculateCommand.calculateCost("PPLA", 500.0);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).contains("PPLA");
        assertThat(result).contains("0.41");
        assertThat(result).contains("12.25");
        verify(filamentService, times(1)).calculateCost("PPLA", 500.0);
    }

    @Test
    void testCalculateCostZeroLength() throws Exception {
        // Given
        CostCalculation calculation = new CostCalculation("PPLA", 0.0, 0.0);
        when(filamentService.calculateCost("PPLA", 0.0)).thenReturn(calculation);

        // When  
        String result = calculateCommand.calculateCost("PPLA", 0.0);

        // Then
        assertThat(result).isNotEmpty();
        verify(filamentService, times(1)).calculateCost("PPLA", 0.0);
    }

    @Test
    void testCalculateCostLongLength() throws Exception {
        // Given
        CostCalculation calculation = new CostCalculation("PPLA", 1.64, 49.0);
        when(filamentService.calculateCost("PPLA", 2000.0)).thenReturn(calculation);

        // When
        String result = calculateCommand.calculateCost("PPLA", 2000.0);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).contains("code");
        assertThat(result).contains("cost");
        assertThat(result).contains("weight");
        assertThat(result).contains("PPLA");
        assertThat(result).contains("1.64");
        assertThat(result).contains("49.0");
        verify(filamentService, times(1)).calculateCost("PPLA", 2000.0);
    }

    @Test
    void testCalculateCostFormatting() throws Exception {
        // Given
        CostCalculation calculation = new CostCalculation("PPLA", 1.64, 49.0);
        when(filamentService.calculateCost("PPLA", 2000.0)).thenReturn(calculation);

        // When
        String result = calculateCommand.calculateCost("PPLA", 2000.0);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).contains("1.64");
        verify(filamentService, times(1)).calculateCost("PPLA", 2000.0);
    }

    @Test
    void testCalculateCostDifferentFilament() throws Exception {
        // Given
        CostCalculation calculation = new CostCalculation("PETG", 2.5, 75.0);
        when(filamentService.calculateCost("PETG", 3000.0)).thenReturn(calculation);

        // When
        String result = calculateCommand.calculateCost("PETG", 3000.0);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).contains("PETG");
        assertThat(result).contains("2.5");
        assertThat(result).contains("75.0");
        verify(filamentService, times(1)).calculateCost("PETG", 3000.0);
    }

    @Test
    void testCalculateCostSmallValue() throws Exception {
        // Given
        CostCalculation calculation = new CostCalculation("PPLA", 0.08, 2.45);
        when(filamentService.calculateCost("PPLA", 100.0)).thenReturn(calculation);

        // When
        String result = calculateCommand.calculateCost("PPLA", 100.0);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).contains("0.08");
        assertThat(result).contains("2.45");
        verify(filamentService, times(1)).calculateCost("PPLA", 100.0);
    }
}
