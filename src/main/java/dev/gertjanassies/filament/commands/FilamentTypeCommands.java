package dev.gertjanassies.filament.commands;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import dev.gertjanassies.filament.domain.FilamentType;
import dev.gertjanassies.filament.service.FilamentTypeService;
import dev.gertjanassies.filament.util.InputHelper;
import dev.gertjanassies.filament.util.OutputFormat;
import dev.gertjanassies.filament.util.OutputFormatter;

@ShellComponent
public class FilamentTypeCommands {

    private final FilamentTypeService filamentTypeService;
    private final InputHelper inputHelper;

    FilamentTypeCommands(FilamentTypeService filamentTypeService, InputHelper inputHelper) {
        this.filamentTypeService = filamentTypeService;
        this.inputHelper = inputHelper;
    }

    private String formatFilamentTypes(List<FilamentType> types, OutputFormat format) {
        if (types.isEmpty()) {
            return "No filament types found.";
        }

        String[] headers = {"ID", "Name", "Manufacturer", "Description", "Type", "Diameter", "Nozzle Temp", "Bed Temp", "Density"};
        
        Function<FilamentType, String[]> rowMapper = ft -> new String[] {
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

        return switch (format) {
            case JSON -> OutputFormatter.formatJson(types);
            case CSV -> OutputFormatter.formatCsv(types, headers, rowMapper);
            case TABLE -> OutputFormatter.formatTable(types, headers, rowMapper);
        };
    }

    private String formatFilamentType(FilamentType ft, OutputFormat format) {
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

        return switch (format) {
            case JSON -> OutputFormatter.formatJson(ft);
            case CSV -> OutputFormatter.formatCsv(data);
            case TABLE -> OutputFormatter.formatTable(data);
        };
    }

    @ShellMethod(key = "type-list", value = "Lists all filament types")
    public String listTypes(
        @ShellOption(value = {"-o", "--output"}, defaultValue = "TABLE", help = "Output format: table, json, or csv") OutputFormat format) {
        return filamentTypeService.getAllFilamentTypes().fold(
            error -> "Failed to retrieve filament types: " + error,
            types -> formatFilamentTypes(types, format)
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
            name = inputHelper.readString("Name: ");
        }
        if (manufacturer == null) {
            manufacturer = inputHelper.readString("Manufacturer: ");
        }
        if (description == null) {
            description = inputHelper.readString("Description: ");
        }
        if (type == null) {
            type = inputHelper.readString("Type (PLA/PETG/ABS/etc): ");
        }
        if (diameter == null) {
            diameter = inputHelper.readDouble("Diameter (mm): ");
        }
        if (nozzleTemp == null) {
            nozzleTemp = inputHelper.readString("Nozzle Temperature (e.g., 190-220): ");
        }
        if (bedTemp == null) {
            bedTemp = inputHelper.readString("Bed Temperature (e.g., 50-60): ");
        }
        if (density == null) {
            density = inputHelper.readDouble("Density (g/cm³): ");
        }

        var filamentType = new FilamentType(0, name, manufacturer, description, type, diameter, nozzleTemp, bedTemp, density);
        return filamentTypeService.addFilamentType(filamentType).fold(
            error -> "Failed to add filament type: " + error,
            value -> "Filament type added successfully:\n" + formatFilamentType(value, OutputFormat.TABLE)
        );
    }

    @ShellMethod(key = "type-get", value = "Gets a filament type by its id. Usage: type-get <id>")
    public String getType(
        @ShellOption int id,
        @ShellOption(value = {"-o", "--output"}, defaultValue = "TABLE", help = "Output format: table, json, or csv") OutputFormat format) {
        return filamentTypeService.getFilamentTypeById(id).fold(
            error -> "Failed to get filament type with id " + id + ": " + error,
            filamentType -> formatFilamentType(filamentType, format)
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
