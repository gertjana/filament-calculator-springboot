package dev.gertjanassies.filament.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FilamentType(
    @JsonProperty("type") String type,
    @JsonProperty("density") double density
) {
}
