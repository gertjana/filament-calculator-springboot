package dev.gertjanassies.filament.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FilamentType(
    @JsonProperty("id")
    int id,
    
    @JsonProperty("name")
    String name,
    
    @JsonProperty("manufacturer")
    String manufacturer,
    
    @JsonProperty("description")
    String description,
    
    @JsonProperty("type")
    String type,
    
    @JsonProperty("diameter")
    double diameter,
    
    @JsonProperty("nozzleTemp")
    String nozzleTemp,
    
    @JsonProperty("bedTemp")
    String bedTemp,
    
    @JsonProperty("density")
    double density
) {
}
