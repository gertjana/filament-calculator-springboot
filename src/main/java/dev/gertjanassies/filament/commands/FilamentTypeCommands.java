package dev.gertjanassies.filament.commands;

import java.util.LinkedHashMap;
import java.util.List;

import org.jline.reader.LineReader;
import org.springframework.context.annotation.Lazy;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.table.ArrayTableModel;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.TableBuilder;
import org.springframework.shell.table.TableModel;

import dev.gertjanassies.filament.domain.FilamentType;
import dev.gertjanassies.filament.service.FilamentTypeService;

@ShellComponent
public class FilamentTypeCommands {

    private final FilamentTypeService filamentTypeService;
    private final LineReader lineReader;

    FilamentTypeCommands(FilamentTypeService filamentTypeService, @Lazy LineReader lineReader) {
        this.filamentTypeService = filamentTypeService;
        this.lineReader = lineReader;
    }

    private String formatFilamentTypesTable(List<FilamentType> types) {
        if (types.isEmpty()) {
            return "No filament types found.";
        }

        String[][] data = new String[types.size() + 1][9];
        data[0] = new String[] {"ID", "Name", "Manufacturer", "Description", "Type", "Diameter", "Nozzle Temp", "Bed Temp", "Density"};
        
        for (int i = 0; i < types.size(); i++) {
            FilamentType ft = types.get(i);
            data[i + 1] = new String[] {
                String.valueOf(ft.id()),
                ft.name(),
                ft.manufacturer(),
                ft.description(),
                ft.type(),
                String.format("%.2f mm", ft.diameter()),
                ft.nozzleTemp() + "°C",
                ft.bedTemp() + "°C",
                String.format("%.2f g/cm³", ft.density())
            };
        }

        TableModel model = new ArrayTableModel(data);
        TableBuilder tableBuilder = new TableBuilder(model);
        return tableBuilder.addFullBorder(BorderStyle.fancy_light).build().render(140);
    }

    private String formatFilamentTypeTable(FilamentType ft) {
        LinkedHashMap<String, String> data = new LinkedHashMap<>();
        data.put("ID", String.valueOf(ft.id()));
        data.put("Name", ft.name());
        data.put("Manufacturer", ft.manufacturer());
        data.put("Description", ft.description());
        data.put("Type", ft.type());
        data.put("Diameter", String.format("%.2f mm", ft.diameter()));
        data.put("Nozzle Temp", ft.nozzleTemp() + "°C");
        data.put("Bed Temp", ft.bedTemp() + "°C");
        data.put("Density", String.format("%.2f g/cm³", ft.density()));

        String[][] tableData = new String[data.size()][2];
        int i = 0;
        for (var entry : data.entrySet()) {
            tableData[i++] = new String[] {entry.getKey(), entry.getValue()};
        }

        TableModel model = new ArrayTableModel(tableData);
        TableBuilder tableBuilder = new TableBuilder(model);
        return tableBuilder.addFullBorder(BorderStyle.fancy_light).build().render(60);
    }

    @ShellMethod(key = "type-list", value = "Lists all filament types")
    public String listTypes() {
        return filamentTypeService.getAllFilamentTypes().fold(
            error -> "Failed to retrieve filament types: " + error,
            this::formatFilamentTypesTable
        );
    }

    @ShellMethod(key = "type-add", value = "Adds a new filament type. Usage: type-add [<name> <manufacturer> <description> <type> <diameter> <nozzleTemp> <bedTemp> <density>]")
    public String addType(
        @ShellOption(defaultValue = ShellOption.NULL) String name,
        @ShellOption(defaultValue = ShellOption.NULL) String manufacturer,
        @ShellOption(defaultValue = ShellOption.NULL) String description,
        @ShellOption(defaultValue = ShellOption.NULL) String type,
        @ShellOption(defaultValue = ShellOption.NULL) Double diameter,
        @ShellOption(defaultValue = ShellOption.NULL) String nozzleTemp,
        @ShellOption(defaultValue = ShellOption.NULL) String bedTemp,
        @ShellOption(defaultValue = ShellOption.NULL) Double density) {

        // Interactive prompts if arguments not provided
        if (name == null) {
            name = lineReader.readLine("Name: ");
        }
        if (manufacturer == null) {
            manufacturer = lineReader.readLine("Manufacturer: ");
        }
        if (description == null) {
            description = lineReader.readLine("Description: ");
        }
        if (type == null) {
            type = lineReader.readLine("Type (PLA/PETG/ABS/etc): ");
        }
        if (diameter == null) {
            boolean validDiameter = false;
            while (!validDiameter) {
                try {
                    String input = lineReader.readLine("Diameter (mm): ");
                    diameter = Double.parseDouble(input);
                    validDiameter = true;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number format. Please enter a valid decimal number.");
                }
            }
        }
        if (nozzleTemp == null) {
            nozzleTemp = lineReader.readLine("Nozzle Temperature (e.g., 190-220): ");
        }
        if (bedTemp == null) {
            bedTemp = lineReader.readLine("Bed Temperature (e.g., 50-60): ");
        }
        if (density == null) {
            boolean validDensity = false;
            while (!validDensity) {
                try {
                    String input = lineReader.readLine("Density (g/cm³): ");
                    density = Double.parseDouble(input);
                    validDensity = true;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number format. Please enter a valid decimal number.");
                }
            }
        }

        var filamentType = new FilamentType(0, name, manufacturer, description, type, diameter, nozzleTemp, bedTemp, density);
        return filamentTypeService.addFilamentType(filamentType).fold(
            error -> "Failed to add filament type: " + error,
            value -> "Filament type added successfully:\n" + formatFilamentTypeTable(value)
        );
    }

    @ShellMethod(key = "type-get", value = "Gets a filament type by its id. Usage: type-get <id>")
    public String getType(@ShellOption int id) {
        return filamentTypeService.getFilamentTypeById(id).fold(
            error -> "Failed to get filament type with id " + id + ": " + error,
            this::formatFilamentTypeTable
        );
    }

    @ShellMethod(key = "type-delete", value = "Deletes a filament type by its id. Usage: type-delete <id>")
    public String deleteType(@ShellOption int id) {
        return filamentTypeService.deleteFilamentType(id).fold(
            error -> error,
            value -> "Filament type deleted successfully: " + id
        );
    }
}
