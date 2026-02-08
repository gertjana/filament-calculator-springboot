package dev.gertjanassies.filament.commands;

// import java.io.IOException;
import java.util.Comparator;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.gertjanassies.filament.domain.Filament;
import dev.gertjanassies.filament.service.FilamentService;
import dev.gertjanassies.filament.util.JsonHelper;


@ShellComponent
public class FilamentCommands {

  
  private final FilamentService filamentService;
  private final ObjectMapper objectMapper;
  
   FilamentCommands(FilamentService filamentService, ObjectMapper objectMapper) {
    this.filamentService = filamentService;
    this.objectMapper = objectMapper;
  } 


  @ShellMethod(key="list", value="Lists all filaments in the collection")
  public String listAll(){
    //TODO move sorting to service layer
    return filamentService.getAllFilaments()
        .map(filaments -> filaments.stream()
            .sorted(Comparator.comparing(Filament::manufacturer)
                .thenComparing(Filament::type))
            .toList())
        .fold(
            error -> "Failed to retrieve filaments: " + error,
            filaments -> JsonHelper.toJson(objectMapper, filaments)
        );
  }

  @ShellMethod(key="add", value="Adds a new filament to the collection. Usage: add <code> <manufacturer> <type> <color> <diameter (mm)> <price (euro)> <weight (grams)")
  public String addFilament(
    @ShellOption String code,
    @ShellOption String manufacturer, 
    @ShellOption String type, 
    @ShellOption String color, 
    @ShellOption double diameter, 
    @ShellOption double price, 
    @ShellOption int weight) {

    var filament = new Filament(code, type, manufacturer, diameter, color, java.math.BigDecimal.valueOf(price), weight);
    return filamentService.addFilament(filament).fold(
        error -> "Failed to add filament with code " + code + ": " + error,
        value -> JsonHelper.toJson(objectMapper, filament)
    );
  }
  
  @ShellMethod(key="get", value="Gets a filament by its code. Usage: get <code>")
  public String getFilament(@ShellOption String code) {
    return filamentService.getFilamentByCode(code).fold(
       error -> "Failed to get filament with code " + code + ": " + error,
       value -> JsonHelper.toJson(objectMapper, value)
    );
  }

  @ShellMethod(key="delete", value="Deletes a filament by its code. Usage: delete <code>")
  public String deleteFilament(@ShellOption String code) {
    return filamentService.deleteFilament(code).fold(
        error -> error,
        value -> "Filament deleted successfully: " + code
    );
  }     
}

