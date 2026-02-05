package dev.gertjanassies.filament.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record Filament(
      @JsonProperty("code")
    String code,
    
    @JsonProperty("type")
    String type,
    
    @JsonProperty("manufacturer")
    String manufacturer,
    
    @JsonProperty("size")
    double size,
    
    @JsonProperty("color")
    String color,
    
    @JsonProperty("price")
    BigDecimal price,
    
    @JsonProperty("weight")
    int weight

) {}
