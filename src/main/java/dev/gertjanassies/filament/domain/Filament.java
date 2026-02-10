package dev.gertjanassies.filament.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record Filament(
    @JsonProperty("id")
    int id,
    
    @JsonProperty("color")
    String color,
    
    @JsonProperty("filamentTypeId")
    int filamentTypeId,
    
    @JsonProperty("price")
    BigDecimal price,
    
    @JsonProperty("weight")
    int weight

) {}
