package dev.gertjanassies.filament.dto;

import java.math.BigDecimal;

/**
 * Flattened data transfer object for a Filament with its type information.
 * Used for CSV and JSON list output.
 */
public record FilamentListWithType(
    int id,
    String name,
    String manufacturer,
    String type,
    String diameter,
    String nozzleTemp,
    String bedTemp,
    String density,
    String color,
    BigDecimal price,
    int weight
) {
}
