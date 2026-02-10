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
        CostCalculation calculation = new CostCalculation(1, 0.82, 24.5);
        when(filamentService.calculateCost(1, 1000.0)).thenReturn(new Result.Success<>(calculation));

        // When
        String result = calculateCommand.calculateCost(1, 1000.0);

        // Then
        assertThat(result).contains("Filament ID");
        assertThat(result).contains("1");
        assertThat(result).contains("Weight");
        assertThat(result).contains("0.82");
        assertThat(result).contains("Cost");
        assertThat(result).contains("24.5");
        verify(filamentService, times(1)).calculateCost(1, 1000.0);
    }

    @Test
    void testCalculateCostWithDifferentLength() throws Exception {
        // Given
        CostCalculation calculation = new CostCalculation(1, 0.41, 12.25);
        when(filamentService.calculateCost(1, 500.0)).thenReturn(new Result.Success<>(calculation));

        // When
        String result = calculateCommand.calculateCost(1, 500.0);

        // Then
        assertThat(result).contains("Filament ID");
        assertThat(result).contains("1");
        assertThat(result).contains("Weight");
        assertThat(result).contains("0.41");
        assertThat(result).contains("Cost");
        assertThat(result).contains("12.25");
        verify(filamentService, times(1)).calculateCost(1, 500.0);
    }

    @Test
    void testCalculateCostWithZeroLength() throws Exception {
        // Given
        CostCalculation calculation = new CostCalculation(1, 0.0, 0.0);
        when(filamentService.calculateCost(1, 0.0)).thenReturn(new Result.Success<>(calculation));

        // When
        String result = calculateCommand.calculateCost(1, 0.0);

        // Then
        assertThat(result).contains("Filament ID");
        assertThat(result).contains("1");
        assertThat(result).contains("Weight");
        assertThat(result).contains("0.0");
        assertThat(result).contains("Cost");
        verify(filamentService, times(1)).calculateCost(1, 0.0);
    }

    @Test
    void testCalculateCostFormatsCorrectly() throws Exception {
        // Given
        CostCalculation calculation = new CostCalculation(1, 1.64, 49.0);
        when(filamentService.calculateCost(1, 2000.0)).thenReturn(new Result.Success<>(calculation));

        // When
        String result = calculateCommand.calculateCost(1, 2000.0);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).contains("Filament ID");
        assertThat(result).contains("Cost");
        assertThat(result).contains("Weight");
        assertThat(result).contains("1");
        assertThat(result).contains("1.64");
        assertThat(result).contains("49.0");
        verify(filamentService, times(1)).calculateCost(1, 2000.0);
    }

    @Test
    void testCalculateCostWithInvalidCode() throws Exception {
        // Given
        when(filamentService.calculateCost(999, 1000.0))
            .thenReturn(new Result.Failure<>("Filament not found: 999"));

        // When
        String result = calculateCommand.calculateCost(999, 1000.0);

        // Then
        assertThat(result).contains("Filament not found");
        verify(filamentService, times(1)).calculateCost(999, 1000.0);
    }

    @Test
    void testCalculateCostWithLargeLength() throws Exception {
        // Given
        CostCalculation calculation = new CostCalculation(2, 12.5, 375.0);
        when(filamentService.calculateCost(2, 10000.0)).thenReturn(new Result.Success<>(calculation));

        // When
        String result = calculateCommand.calculateCost(2, 10000.0);

        // Then
        assertThat(result).contains("Filament ID");
        assertThat(result).contains("2");
        assertThat(result).contains("Weight");
        assertThat(result).contains("12.5");
        assertThat(result).contains("Cost");
        assertThat(result).contains("375.0");
        verify(filamentService, times(1)).calculateCost(2, 10000.0);
    }
}
