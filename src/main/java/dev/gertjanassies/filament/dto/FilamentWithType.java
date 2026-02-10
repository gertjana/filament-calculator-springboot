package dev.gertjanassies.filament.dto;

import java.math.BigDecimal;

import dev.gertjanassies.filament.domain.FilamentType;

/**
 * Data transfer object for a Filament with its associated FilamentType nested.
 * Used for JSON output to provide complete information in a single object.
 */
public record FilamentWithType(
    int id,
    String color,
    BigDecimal price,
    int weight,
    FilamentType filamentType
) {
}
