package dev.gertjanassies.filament.commands;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.gertjanassies.filament.domain.Filament;
import dev.gertjanassies.filament.domain.FilamentType;
import dev.gertjanassies.filament.service.FilamentService;
import dev.gertjanassies.filament.util.Result;

@ExtendWith(MockitoExtension.class)
class FilamentCommandsTest {

    @Mock
    private FilamentService filamentService;

    private FilamentCommands filamentCommands;

    private Filament testFilament;
    private FilamentType testFilamentType;

    @BeforeEach
    void setUp() {
        filamentCommands = new FilamentCommands(filamentService);
        testFilamentType = new FilamentType(
            1,
            "Test PLA",
            "TestBrand",
            "Test Description",
            "PLA",
            1.75,
            "190-220",
            "50-60",
            1.24
        );
        testFilament = new Filament(
            1,
            "Blue",
            1,
            new BigDecimal("25.00"),
            1000
        );
    }

    @Test
    void testListAll() throws IOException {
        // Given
        List<Filament> filaments = List.of(testFilament);
        when(filamentService.getAllFilaments()).thenReturn(new Result.Success<>(filaments));
        when(filamentService.getFilamentTypeById(1)).thenReturn(new Result.Success<>(testFilamentType));

        // When
        String result = filamentCommands.listAll();

        // Then
        assertThat(result).contains("│1 │");
        assertThat(result).contains("PLA");
        assertThat(result).contains("TestBrand");
        assertThat(result).contains("Blue");
        verify(filamentService, times(1)).getAllFilaments();
    }

    @Test
    void testListAllEmpty() throws IOException {
        // Given
        when(filamentService.getAllFilaments()).thenReturn(new Result.Success<>(List.of()));

        // When
        String result = filamentCommands.listAll();

        // Then
        assertThat(result).isEqualTo("No filaments found.");
        verify(filamentService, times(1)).getAllFilaments();
    }

    @Test
    void testGetFilament() throws IOException {
        // Given
        when(filamentService.getFilamentById(1)).thenReturn(new Result.Success<>(testFilament));
        when(filamentService.getFilamentTypeById(1)).thenReturn(new Result.Success<>(testFilamentType));

        // When
        String result = filamentCommands.getFilament(1);

        // Then
        assertThat(result).contains("1");
        assertThat(result).contains("PLA");
        assertThat(result).contains("Blue");
        assertThat(result).contains("25.00");
        verify(filamentService, times(1)).getFilamentById(1);
    }

    @Test
    void testAddFilament() throws IOException {
        // Given
        Filament newFilament = new Filament(2, "Red", 1, new BigDecimal("30.00"), 750);
        when(filamentService.addFilament(any(Filament.class))).thenReturn(new Result.Success<>(newFilament));
        when(filamentService.getFilamentTypeById(1)).thenReturn(new Result.Success<>(testFilamentType));

        // When
        String result = filamentCommands.addFilament(
            "Red",
            1,
            30.00,
            750
        );

        // Then
        assertThat(result).contains("2");
        assertThat(result).contains("Red");
        assertThat(result).contains("TestBrand");
        verify(filamentService, times(1)).addFilament(any(Filament.class));
    }

    @Test
    void testDeleteFilament() throws IOException {
        // Given
        when(filamentService.deleteFilament(1)).thenReturn(new Result.Success<>(null));

        // When
        String result = filamentCommands.deleteFilament(1);

        // Then
        assertThat(result).contains("deleted");
        assertThat(result).contains("1");
        verify(filamentService, times(1)).deleteFilament(1);
    }

    @Test
    void testGetFilamentNotFound() throws IOException {
        // Given
        when(filamentService.getFilamentById(999))
            .thenReturn(new Result.Failure<>("Filament not found: 999"));

        // When
        String result = filamentCommands.getFilament(999);

        // Then
        assertThat(result).contains("not found").contains("999");
        verify(filamentService, times(1)).getFilamentById(999);
    }
}
