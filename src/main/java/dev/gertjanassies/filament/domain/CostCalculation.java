package dev.gertjanassies.filament.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CostCalculation(
    @JsonProperty("id")
    int id,
    @JsonProperty("cost") 
    double cost,
    @JsonProperty("weight")
    double weight 
) {}
