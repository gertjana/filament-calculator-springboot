package dev.gertjanassies.filament.config;

import java.util.Locale;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import dev.gertjanassies.filament.util.OutputFormat;

/**
 * Custom converter to handle case-insensitive OutputFormat parsing.
 * Allows users to specify output formats as "table", "json", "csv" instead of "TABLE", "JSON", "CSV".
 */
@Component
public class OutputFormatConverter implements Converter<String, OutputFormat> {

    @Override
    public OutputFormat convert(@NonNull String source) {
        try {
            return OutputFormat.valueOf(source.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                "Invalid output format: '" + source + "'. Valid options are: table, json, csv (case-insensitive)");
        }
    }
}
