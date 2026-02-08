package dev.gertjanassies.filament.commands;

import java.util.LinkedHashMap;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.table.ArrayTableModel;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.TableBuilder;
import org.springframework.shell.table.TableModel;

import dev.gertjanassies.filament.domain.CostCalculation;
import dev.gertjanassies.filament.service.FilamentService;

@ShellComponent
public class CalculateCommand {

  private final FilamentService filamentService;
  
   CalculateCommand(FilamentService filamentService) {
    this.filamentService = filamentService;
  } 

  private String formatCostCalculation(CostCalculation calc) {
    LinkedHashMap<String, String> data = new LinkedHashMap<>();
    data.put("Filament Code", calc.code());
    data.put("Weight", String.format("%.2f g", calc.weight()));
    data.put("Cost", String.format("€ %.2f", calc.cost()));

    String[][] tableData = new String[data.size()][2];
    int i = 0;
    for (var entry : data.entrySet()) {
      tableData[i++] = new String[] {entry.getKey(), entry.getValue()};
    }

    TableModel model = new ArrayTableModel(tableData);
    TableBuilder tableBuilder = new TableBuilder(model);
    return tableBuilder.addFullBorder(BorderStyle.fancy_light).build().render(50);
  }

  @ShellMethod(key="calculate", value="Calculates the costs for a print. Usage: calculate <code> <length in cm>")
  public String calculateCost(
    @ShellOption String code,
    @ShellOption double length) {
    return filamentService.calculateCost(code, length).fold(  
      error -> "Failed to calculate cost for filament with code " + code + ": " + error,
      this::formatCostCalculation
    );
  }
}