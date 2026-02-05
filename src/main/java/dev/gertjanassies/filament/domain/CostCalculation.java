package dev.gertjanassies.filament.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CostCalculation(
    @JsonProperty("code")
    String code,
    @JsonProperty("cost") 
    double cost,
    @JsonProperty("weight")
    double weight 
) {}
