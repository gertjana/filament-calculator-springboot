package dev.gertjanassies.filament.commands;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import dev.gertjanassies.filament.domain.Filament;
import dev.gertjanassies.filament.service.FilamentService;

@ExtendWith(MockitoExtension.class)
class FilamentCommandsTest {

    @Mock
    private FilamentService filamentService;

    private ObjectMapper objectMapper;

    private FilamentCommands filamentCommands;

    private Filament testFilament;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        filamentCommands = new FilamentCommands(filamentService, objectMapper);
        testFilament = new Filament(
            "TEST_PLA",
            "PLA",
            "TestBrand",
            1.75,
            "Blue",
            new BigDecimal("25.00"),
            1000
        );
    }

    @Test
    void testListAll() throws IOException {
        // Given
        List<Filament> filaments = List.of(testFilament);
        when(filamentService.getAllFilaments()).thenReturn(filaments);

        // When
        String result = filamentCommands.listAll();

        // Then
        assertThat(result).contains("TEST_PLA");
        assertThat(result).contains("PLA");
        assertThat(result).contains("TestBrand");
        verify(filamentService, times(1)).getAllFilaments();
    }

    @Test
    void testListAllSortedByManufacturerAndType() throws IOException {
        // Given
        Filament prusaPLA = new Filament("PPLA", "PLA", "Prusa", 1.75, "Natural", new BigDecimal("25.00"), 750);
        Filament prusaPETG = new Filament("PPETG", "PETG", "Prusa", 1.75, "Black", new BigDecimal("28.00"), 750);
        Filament colorfabbABS = new Filament("CF_ABS", "ABS", "ColorFabb", 1.75, "Red", new BigDecimal("35.00"), 750);
        Filament colorfabbNGEN = new Filament("CF_nGEN", "nGEN", "ColorFabb", 1.75, "Lightgrey", new BigDecimal("35.00"), 750);
        
        // Unsorted list
        List<Filament> filaments = List.of(prusaPETG, colorfabbNGEN, prusaPLA, colorfabbABS);
        when(filamentService.getAllFilaments()).thenReturn(filaments);

        // When
        String result = filamentCommands.listAll();

        // Then
        // Verify it's sorted by manufacturer (ColorFabb before Prusa) then by type (alphabetically)
        int colorfabbAbsIndex = result.indexOf("CF_ABS");
        int colorfabbNgenIndex = result.indexOf("CF_nGEN");
        int prusaPlaIndex = result.indexOf("PPLA");
        int prusaPetgIndex = result.indexOf("PPETG");
        
        // ColorFabb comes before Prusa alphabetically
        assertThat(colorfabbAbsIndex).as("ColorFabb ABS should come first").isGreaterThan(-1);
        assertThat(colorfabbAbsIndex).as("ColorFabb ABS before ColorFabb nGEN (ABS < nGEN)").isLessThan(colorfabbNgenIndex);
        assertThat(colorfabbNgenIndex).as("ColorFabb nGEN before Prusa PETG").isLessThan(prusaPetgIndex);
        assertThat(prusaPetgIndex).as("Prusa PETG before Prusa PLA (PETG < PLA alphabetically)").isLessThan(prusaPlaIndex);
        
        verify(filamentService, times(1)).getAllFilaments();
    }

    @Test
    void testListAllEmpty() throws IOException {
        // Given
        when(filamentService.getAllFilaments()).thenReturn(List.of());

        // When
        String result = filamentCommands.listAll();

        // Then
        assertThat(result).isEqualTo("[ ]");
        verify(filamentService, times(1)).getAllFilaments();
    }

    @Test
    void testGetFilament() throws IOException {
        // Given
        when(filamentService.getFilamentByCode("TEST_PLA")).thenReturn(Optional.of(testFilament));

        // When
        String result = filamentCommands.getFilament("TEST_PLA");

        // Then
        assertThat(result).contains("TEST_PLA");
        assertThat(result).contains("PLA");
        assertThat(result).contains("25.00");
        verify(filamentService, times(1)).getFilamentByCode("TEST_PLA");
    }

    @Test
    void testAddFilament() throws IOException {
        // Given
        when(filamentService.addFilament(any(Filament.class))).thenReturn(Optional.of(testFilament));

        // When
        String result = filamentCommands.addFilament(
            "NEW_PLA",
            "NewBrand",
            "PLA",
            "Red",
            1.75,
            30.00,
            750
        );

        // Then
        assertThat(result).contains("NEW_PLA");
        assertThat(result).contains("Red");
        assertThat(result).contains("NewBrand");
        verify(filamentService, times(1)).addFilament(any(Filament.class));
    }

    @Test
    void testDeleteFilament() throws IOException {
        // Given
        when(filamentService.deleteFilament("TEST_PLA")).thenReturn(true);

        // When
        String result = filamentCommands.deleteFilament("TEST_PLA");

        // Then
        assertThat(result).contains("deleted");
        assertThat(result).contains("TEST_PLA");
        verify(filamentService, times(1)).deleteFilament("TEST_PLA");
    }

    @Test
    void testGetFilamentNotFound() throws IOException {
        // Given
        when(filamentService.getFilamentByCode("NONEXISTENT"))
            .thenReturn(Optional.empty());

        // When/Then
        String result = filamentCommands.getFilament("NONEXISTENT");


        verify(filamentService, times(1)).getFilamentByCode("NONEXISTENT");
    }
}
