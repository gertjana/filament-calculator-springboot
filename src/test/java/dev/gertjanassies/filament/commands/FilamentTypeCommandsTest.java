package dev.gertjanassies.filament.commands;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.gertjanassies.filament.domain.FilamentType;
import dev.gertjanassies.filament.service.FilamentTypeService;
import dev.gertjanassies.filament.util.Result;

@ExtendWith(MockitoExtension.class)
class FilamentTypeCommandsTest {

    @Mock
    private FilamentTypeService filamentTypeService;

    private FilamentTypeCommands filamentTypeCommands;

    private FilamentType testFilamentType;

    @BeforeEach
    void setUp() {
        filamentTypeCommands = new FilamentTypeCommands(filamentTypeService);
        testFilamentType = new FilamentType(
            1,
            "Test PLA",
            "TestBrand",
            "Standard PLA filament",
            "PLA",
            1.75,
            "190-220",
            "50-60",
            1.24
        );
    }

    @Test
    void testListAll() throws IOException {
        // Given
        List<FilamentType> types = List.of(testFilamentType);
        when(filamentTypeService.getAllFilamentTypes()).thenReturn(new Result.Success<>(types));

        // When
        String result = filamentTypeCommands.listTypes();

        // Then
        assertThat(result).contains("ID");
        assertThat(result).contains("1");
        assertThat(result).contains("Name");
        assertThat(result).contains("Test PLA");
        assertThat(result).contains("Manufacturer");
        assertThat(result).contains("TestBrand");
        assertThat(result).contains("Type");
        assertThat(result).contains("PLA");
        verify(filamentTypeService, times(1)).getAllFilamentTypes();
    }

    @Test
    void testListAllEmpty() throws IOException {
        // Given
        when(filamentTypeService.getAllFilamentTypes()).thenReturn(new Result.Success<>(List.of()));

        // When
        String result = filamentTypeCommands.listTypes();

        // Then
        assertThat(result).isEqualTo("No filament types found.");
        verify(filamentTypeService, times(1)).getAllFilamentTypes();
    }

    @Test
    void testGetFilamentType() throws IOException {
        // Given
        when(filamentTypeService.getFilamentTypeById(1)).thenReturn(new Result.Success<>(testFilamentType));

        // When
        String result = filamentTypeCommands.getType(1);

        // Then
        assertThat(result).contains("ID");
        assertThat(result).contains("1");
        assertThat(result).contains("Name");
        assertThat(result).contains("Test PLA");
        assertThat(result).contains("Manufacturer");
        assertThat(result).contains("TestBrand");
        assertThat(result).contains("Type");
        assertThat(result).contains("PLA");
        assertThat(result).contains("Diameter");
        assertThat(result).contains("1.75");
        assertThat(result).contains("Nozzle Temp");
        assertThat(result).contains("190-220");
        assertThat(result).contains("Bed Temp");
        assertThat(result).contains("50-60");
        assertThat(result).contains("Density");
        assertThat(result).contains("1.24");
        verify(filamentTypeService, times(1)).getFilamentTypeById(1);
    }

    @Test
    void testAddFilamentType() throws IOException {
        // Given
        FilamentType newType = new FilamentType(2, "New PETG", "NewBrand", "New PETG filament", "PETG", 1.75, "220-250", "70-85", 1.27);
        when(filamentTypeService.addFilamentType(any(FilamentType.class))).thenReturn(new Result.Success<>(newType));

        // When
        String result = filamentTypeCommands.addType(
            "New PETG",
            "NewBrand",
            "New PETG filament",
            "PETG",
            1.75,
            "220-250",
            "70-85",
            1.27
        );

        // Then
        assertThat(result).contains("successfully");
        assertThat(result).contains("ID");
        assertThat(result).contains("2");
        assertThat(result).contains("Name");
        assertThat(result).contains("New PETG");
        assertThat(result).contains("Manufacturer");
        assertThat(result).contains("NewBrand");
        assertThat(result).contains("Type");
        assertThat(result).contains("PETG");
        verify(filamentTypeService, times(1)).addFilamentType(any(FilamentType.class));
    }

    @Test
    void testDeleteFilamentType() throws IOException {
        // Given
        when(filamentTypeService.deleteFilamentType(1)).thenReturn(new Result.Success<>(null));

        // When
        String result = filamentTypeCommands.deleteType(1);

        // Then
        assertThat(result).contains("successfully");
        assertThat(result).contains("deleted");
        assertThat(result).contains("1");
        verify(filamentTypeService, times(1)).deleteFilamentType(1);
    }

    @Test
    void testGetFilamentTypeNotFound() throws IOException {
        // Given
        when(filamentTypeService.getFilamentTypeById(999))
            .thenReturn(new Result.Failure<>("Filament type not found: 999"));

        // When
        String result = filamentTypeCommands.getType(999);

        // Then
        assertThat(result).contains("not found").contains("999");
        verify(filamentTypeService, times(1)).getFilamentTypeById(999);
    }
}
