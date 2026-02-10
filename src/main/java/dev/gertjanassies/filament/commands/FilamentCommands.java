package dev.gertjanassies.filament.commands;

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
import dev.gertjanassies.filament.domain.FilamentType;
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

        String[][] data = new String[filaments.size() + 1][11];
        data[0] = new String[] {"ID", "Name", "Manufacturer", "Type", "Diameter", "Nozzle Temp", "Bed Temp", "Density", "Color", "Price", "Weight"};

        for (int i = 0; i < filaments.size(); i++) {
            Filament f = filaments.get(i);
            var typeResult = filamentService.getFilamentTypeById(f.filamentTypeId());
            
            if (typeResult instanceof dev.gertjanassies.filament.util.Result.Success<?, ?> success) {
                FilamentType ft = (FilamentType) success.value();
                data[i + 1] = new String[] {
                    String.valueOf(f.id()),
                    ft.name(),
                    ft.manufacturer(),
                    ft.type(),
                    String.format("%.2f mm", ft.diameter()),
                    ft.nozzleTemp() + "°C",
                    ft.bedTemp() + "°C",
                    String.format("%.2f", ft.density()),
                    f.color(),
                    String.format("€%.2f", f.price()),
                    f.weight() + "g"
                };
            } else {
                data[i + 1] = new String[] {
                    String.valueOf(f.id()),
                    "?",
                    "?",
                    "?",
                    "?",
                    "?",
                    "?",
                    "?",
                    f.color(),
                    String.format("€%.2f", f.price()),
                    f.weight() + "g"
                };
            }
        }

        TableModel model = new ArrayTableModel(data);
        TableBuilder tableBuilder = new TableBuilder(model);
        return tableBuilder.addFullBorder(BorderStyle.fancy_light).build().render(140);
    }

    private String formatFilamentTable(Filament f) {
        LinkedHashMap<String, String> data = new LinkedHashMap<>();
        data.put("ID", String.valueOf(f.id()));
        data.put("Color", f.color());
        data.put("Price", String.format("€%.2f", f.price()));
        data.put("Weight", f.weight() + "g");
        data.put("Filament Type ID", String.valueOf(f.filamentTypeId()));
        
        var typeResult = filamentService.getFilamentTypeById(f.filamentTypeId());
        if (typeResult instanceof dev.gertjanassies.filament.util.Result.Success<FilamentType, String> success) {
            FilamentType ft = success.value();
            data.put("Type Name", ft.name());
            data.put("Manufacturer", ft.manufacturer());
            data.put("Description", ft.description());
            data.put("Type", ft.type());
            data.put("Diameter", String.format("%.2f mm", ft.diameter()));
            data.put("Nozzle Temp", ft.nozzleTemp() + "°C");
            data.put("Bed Temp", ft.bedTemp() + "°C");
            data.put("Density", String.format("%.2f g/cm³", ft.density()));
        }

        String[][] tableData = new String[data.size()][2];
        int i = 0;
        for (var entry : data.entrySet()) {
            tableData[i++] = new String[] {entry.getKey(), entry.getValue()};
        }

        TableModel model = new ArrayTableModel(tableData);
        TableBuilder tableBuilder = new TableBuilder(model);
        return tableBuilder.addFullBorder(BorderStyle.fancy_light).build().render(60);
    }

    @ShellMethod(key = "list", value = "Lists all filaments in the collection")
    public String listAll() {
        return filamentService.getAllFilaments().fold(
            error -> "Failed to retrieve filaments: " + error,
            this::formatFilamentsTable
        );
    }

    @ShellMethod(key = "add", value = "Adds a new filament to the collection. Usage: add <color> <filamentTypeId> <price> <weight>")
    public String addFilament(
        @ShellOption String color,
        @ShellOption int filamentTypeId,
        @ShellOption double price,
        @ShellOption int weight) {

        var filament = new Filament(0, color, filamentTypeId, java.math.BigDecimal.valueOf(price), weight);
        return filamentService.addFilament(filament).fold(
            error -> "Failed to add filament: " + error,
            value -> "Filament added successfully:\n" + formatFilamentTable(value)
        );
    }

    @ShellMethod(key = "get", value = "Gets a filament by its id. Usage: get <id>")
    public String getFilament(@ShellOption int id) {
        return filamentService.getFilamentById(id).fold(
            error -> "Failed to get filament with id " + id + ": " + error,
            this::formatFilamentTable
        );
    }

    @ShellMethod(key = "delete", value = "Deletes a filament by its id. Usage: delete <id>")
    public String deleteFilament(@ShellOption int id) {
        return filamentService.deleteFilament(id).fold(
            error -> error,
            value -> "Filament deleted successfully: " + id
        );
    }
}