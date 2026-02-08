package dev.gertjanassies.filament.commands;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.table.ArrayTableModel;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.TableBuilder;
import org.springframework.shell.table.TableModel;

import dev.gertjanassies.filament.domain.Filament;
import dev.gertjanassies.filament.service.FilamentService;


@ShellComponent
public class FilamentCommands {

  
  private final FilamentService filamentService;
  
   FilamentCommands(FilamentService filamentService) {
    this.filamentService = filamentService;
  } 

  private String formatFilamentsTable(List<Filament> filaments) {
    if (filaments.isEmpty()) {
      return "No filaments found.";
    }

    String[][] data = new String[filaments.size() + 1][7];
    data[0] = new String[] {"Code", "Manufacturer", "Type", "Color", "Diameter", "Price", "Weight"};
    
    for (int i = 0; i < filaments.size(); i++) {
      Filament f = filaments.get(i);
      data[i + 1] = new String[] {
        f.code(),
        f.manufacturer(),
        f.type(),
        f.color(),
        String.format("%.2f mm", f.size()),
        String.format("€%.2f", f.price()),
        f.weight() + "g"
      };
    }

    TableModel model = new ArrayTableModel(data);
    TableBuilder tableBuilder = new TableBuilder(model);
    return tableBuilder.addFullBorder(BorderStyle.fancy_light).build().render(80);
  }

  private String formatFilamentTable(Filament f) {
    LinkedHashMap<String, String> data = new LinkedHashMap<>();
    data.put("Code", f.code());
    data.put("Manufacturer", f.manufacturer());
    data.put("Type", f.type());
    data.put("Color", f.color());
    data.put("Diameter", String.format("%.2f mm", f.size()));
    data.put("Price", String.format("€%.2f", f.price()));
    data.put("Weight", f.weight() + "g");

    String[][] tableData = new String[data.size()][2];
    int i = 0;
    for (var entry : data.entrySet()) {
      tableData[i++] = new String[] {entry.getKey(), entry.getValue()};
    }

    TableModel model = new ArrayTableModel(tableData);
    TableBuilder tableBuilder = new TableBuilder(model);
    return tableBuilder.addFullBorder(BorderStyle.fancy_light).build().render(50);
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
            this::formatFilamentsTable
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
        value -> "Filament added successfully:\n" + formatFilamentTable(value)
    );
  }
  
  @ShellMethod(key="get", value="Gets a filament by its code. Usage: get <code>")
  public String getFilament(@ShellOption String code) {
    return filamentService.getFilamentByCode(code).fold(
       error -> "Failed to get filament with code " + code + ": " + error,
       this::formatFilamentTable
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