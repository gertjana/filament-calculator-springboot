package dev.gertjanassies.filament.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import dev.gertjanassies.filament.util.OutputFormat;

class OutputFormatConverterTest {

    private final OutputFormatConverter converter = new OutputFormatConverter();

    @Test
    void testConvertUpperCase() {
        assertThat(converter.convert("TABLE")).isEqualTo(OutputFormat.TABLE);
        assertThat(converter.convert("JSON")).isEqualTo(OutputFormat.JSON);
        assertThat(converter.convert("CSV")).isEqualTo(OutputFormat.CSV);
    }

    @Test
    void testConvertLowerCase() {
        assertThat(converter.convert("table")).isEqualTo(OutputFormat.TABLE);
        assertThat(converter.convert("json")).isEqualTo(OutputFormat.JSON);
        assertThat(converter.convert("csv")).isEqualTo(OutputFormat.CSV);
    }

    @Test
    void testConvertMixedCase() {
        assertThat(converter.convert("Table")).isEqualTo(OutputFormat.TABLE);
        assertThat(converter.convert("Json")).isEqualTo(OutputFormat.JSON);
        assertThat(converter.convert("TaBlE")).isEqualTo(OutputFormat.TABLE);
    }

    @Test
    void testConvertInvalid() {
        assertThatThrownBy(() -> converter.convert("invalid"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid output format")
            .hasMessageContaining("invalid")
            .hasMessageContaining("table, json, csv");
    }
}
