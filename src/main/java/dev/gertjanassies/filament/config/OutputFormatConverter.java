package dev.gertjanassies.filament.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import dev.gertjanassies.filament.util.OutputFormat;

/**
 * Custom converter to handle case-insensitive OutputFormat parsing.
 * Allows users to specify output formats as "table", "json", "csv" instead of "TABLE", "JSON", "CSV".
 */
@Component
public class OutputFormatConverter implements Converter<String, OutputFormat> {

    @Override
    public OutputFormat convert(String source) {
        try {
            return OutputFormat.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                "Invalid output format: '" + source + "'. Valid options are: table, json, csv (case-insensitive)");
        }
    }
}
