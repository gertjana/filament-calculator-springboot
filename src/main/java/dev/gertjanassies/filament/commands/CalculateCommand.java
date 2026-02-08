package dev.gertjanassies.filament.commands;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.gertjanassies.filament.service.FilamentService;
import dev.gertjanassies.filament.util.JsonHelper;

@ShellComponent
public class CalculateCommand {

  private final FilamentService filamentService;
  private final ObjectMapper objectMapper;
  
   CalculateCommand(FilamentService filamentService, ObjectMapper objectMapper) {
    this.filamentService = filamentService;
    this.objectMapper = objectMapper;
  } 

  @ShellMethod(key="calculate", value="Calculates the costs for a print. Usage: calculate <code> <length in cm>")
  public String calculateCost(
    @ShellOption String code,
    @ShellOption double length) {
    return filamentService.calculateCost(code, length).fold(  
      error -> "Failed to calculate cost for filament with code " + code + ": " + error,
      value -> JsonHelper.toJson(objectMapper, value)
    );
  }
}
