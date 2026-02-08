package dev.gertjanassies.filament.commands;

import dev.gertjanassies.filament.domain.CostCalculation;
import dev.gertjanassies.filament.service.FilamentService;
import dev.gertjanassies.filament.util.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalculateCommandTest {

    @Mock
    private FilamentService filamentService;

    private CalculateCommand calculateCommand;

    @BeforeEach
    void setUp() {
        calculateCommand = new CalculateCommand(filamentService);
    }

    @Test
    void testCalculateCost() throws Exception {
        // Given
        CostCalculation calculation = new CostCalculation("PPLA", 0.82, 24.5);
        when(filamentService.calculateCost("PPLA", 1000.0)).thenReturn(new Result.Success<>(calculation));

        // When
        String result = calculateCommand.calculateCost("PPLA", 1000.0);

        // Then
        assertThat(result).contains("PPLA");
        assertThat(result).contains("0.82");
        assertThat(result).contains("24.5");
        verify(filamentService, times(1)).calculateCost("PPLA", 1000.0);
    }

    @Test
    void testCalculateCostWithDifferentLength() throws Exception {
        // Given
        CostCalculation calculation = new CostCalculation("PPLA", 0.41, 12.25);
        when(filamentService.calculateCost("PPLA", 500.0)).thenReturn(new Result.Success<>(calculation));

        // When
        String result = calculateCommand.calculateCost("PPLA", 500.0);

        // Then
        assertThat(result).contains("PPLA");
        assertThat(result).contains("0.41");
        assertThat(result).contains("12.25");
        verify(filamentService, times(1)).calculateCost("PPLA", 500.0);
    }

    @Test
    void testCalculateCostWithZeroLength() throws Exception {
        // Given
        CostCalculation calculation = new CostCalculation("PPLA", 0.0, 0.0);
        when(filamentService.calculateCost("PPLA", 0.0)).thenReturn(new Result.Success<>(calculation));

        // When
        String result = calculateCommand.calculateCost("PPLA", 0.0);

        // Then
        assertThat(result).contains("PPLA");
        assertThat(result).contains("0.0");
        verify(filamentService, times(1)).calculateCost("PPLA", 0.0);
    }

    @Test
    void testCalculateCostFormatsCorrectly() throws Exception {
        // Given
        CostCalculation calculation = new CostCalculation("PPLA", 1.64, 49.0);
        when(filamentService.calculateCost("PPLA", 2000.0)).thenReturn(new Result.Success<>(calculation));

        // When
        String result = calculateCommand.calculateCost("PPLA", 2000.0);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).contains("Filament Code");
        assertThat(result).contains("Cost");
        assertThat(result).contains("Weight");
        assertThat(result).contains("PPLA");
        assertThat(result).contains("1.64");
        assertThat(result).contains("49.0");
        verify(filamentService, times(1)).calculateCost("PPLA", 2000.0);
    }

    @Test
    void testCalculateCostWithInvalidCode() throws Exception {
        // Given
        when(filamentService.calculateCost("INVALID", 1000.0))
            .thenReturn(new Result.Failure<>("Filament not found: INVALID"));

        // When
        String result = calculateCommand.calculateCost("INVALID", 1000.0);

        // Then
        assertThat(result).contains("Filament not found");
        verify(filamentService, times(1)).calculateCost("INVALID", 1000.0);
    }

    @Test
    void testCalculateCostWithLargeLength() throws Exception {
        // Given
        CostCalculation calculation = new CostCalculation("CF_nGEN", 12.5, 375.0);
        when(filamentService.calculateCost("CF_nGEN", 10000.0)).thenReturn(new Result.Success<>(calculation));

        // When
        String result = calculateCommand.calculateCost("CF_nGEN", 10000.0);

        // Then
        assertThat(result).contains("CF_nGEN");
        assertThat(result).contains("12.5");
        assertThat(result).contains("375.0");
        verify(filamentService, times(1)).calculateCost("CF_nGEN", 10000.0);
    }
}
