package dev.gertjanassies.filament.commands;

import java.io.IOException;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.gertjanassies.filament.service.FilamentService;


@ShellComponent
public class FilamentCommands {

  
  private final FilamentService filamentService;
  private final ObjectMapper objectMapper;
  
   FilamentCommands(FilamentService filamentService, ObjectMapper objectMapper) {
    this.filamentService = filamentService;
    this.objectMapper = objectMapper;
  } 

  @ShellMethod(key="list", value="Lists all filaments in the collection")
  public String listAll() throws IOException{
    var filaments = filamentService.getAllFilaments().stream()
        .sorted((f1, f2) -> {
          int manufacturerCompare = f1.manufacturer().compareToIgnoreCase(f2.manufacturer());
          if (manufacturerCompare != 0) {
            return manufacturerCompare;
          }
          return f1.type().compareToIgnoreCase(f2.type());
        })
        .toList();
    return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(filaments);
  }

  @ShellMethod(key="add", value="Adds a new filament to the collection. Usage: add <code> <manufacturer> <type> <color> <diameter (mm)> <price (euro)> <weight (grams)")
  public String addFilament(
    @ShellOption String code,
    @ShellOption String manufacturer, 
    @ShellOption String type, 
    @ShellOption String color, 
    @ShellOption double diameter, 
    @ShellOption double price, 
    @ShellOption int weight) throws IOException {

    var filament = new dev.gertjanassies.filament.domain.Filament(code, type, manufacturer, diameter, color, java.math.BigDecimal.valueOf(price), weight);
    var result = filamentService.addFilament(filament);

    if (result.isEmpty()) {
      return "Failed to add filament with code " + code;
    }
    return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(filament);
  }
  
  @ShellMethod(key="get", value="Gets a filament by its code. Usage: get <code>")
  public String getFilament(@ShellOption String code) throws IOException {
    var filament = filamentService.getFilamentByCode(code);
    if (filament.isEmpty()) {
      return "Filament with code " + code + " not found.";
    }
    return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(filament.get());
  }

  @ShellMethod(key="delete", value="Deletes a filament by its code. Usage: delete <code>")
  public String deleteFilament(@ShellOption String code) throws IOException {
    filamentService.deleteFilament(code);
    return "Filament with code " + code + " has been deleted.";
  }
}
