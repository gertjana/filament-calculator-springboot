package dev.gertjanassies.filament.commands;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import dev.gertjanassies.filament.domain.Filament;
import dev.gertjanassies.filament.domain.FilamentType;
import dev.gertjanassies.filament.dto.FilamentListWithType;
import dev.gertjanassies.filament.dto.FilamentWithType;
import dev.gertjanassies.filament.service.FilamentService;
import dev.gertjanassies.filament.util.InputHelper;
import dev.gertjanassies.filament.util.OutputFormat;
import dev.gertjanassies.filament.util.OutputFormatter;

@ShellComponent
public class FilamentCommands {

    private final FilamentService filamentService;
    private final InputHelper inputHelper;

    FilamentCommands(FilamentService filamentService, InputHelper inputHelper) {
        this.filamentService = filamentService;
        this.inputHelper = inputHelper;
    }

    private String formatFilaments(List<Filament> filaments, OutputFormat format) {
        if (filaments.isEmpty()) {
            return "No filaments found.";
        }

        // Fetch all filament types once and build a map for efficient lookup
        Map<Integer, FilamentType> typeMap = filamentService.getAllFilamentTypes()
            .map(types -> types.stream()
                .collect(Collectors.toMap(FilamentType::id, type -> type)))
            .fold(
                error -> Map.<Integer, FilamentType>of(),
                types -> types
            );

        if (format == OutputFormat.JSON) {
            // For JSON, create DTOs with nested FilamentType
            List<FilamentWithType> filamentsWithType = filaments.stream()
                .map(f -> {
                    FilamentType ft = typeMap.get(f.filamentTypeId());
                    return new FilamentWithType(f.id(), f.color(), f.price(), f.weight(), ft);
                })
                .toList();
            return OutputFormatter.formatJson(filamentsWithType);
        }

        String[] headers = {"ID", "Name", "Manufacturer", "Type", "Diameter", "Nozzle Temp", "Bed Temp", "Density", "Color", "Price", "Weight"};

        Function<Filament, String[]> rowMapper = f -> {
            FilamentType ft = typeMap.get(f.filamentTypeId());
            if (ft != null) {
                return new String[] {
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
                return new String[] {
                    String.valueOf(f.id()),
                    "?", "?", "?", "?", "?", "?", "?",
                    f.color(),
                    String.format("€%.2f", f.price()),
                    f.weight() + "g"
                };
            }
        };

        return switch (format) {
            case JSON -> throw new IllegalStateException("JSON handled above");
            case CSV -> OutputFormatter.formatCsv(filaments, headers, rowMapper);
            case TABLE -> OutputFormatter.formatTable(filaments, headers, rowMapper);
        };
    }

    private String formatFilament(Filament f, OutputFormat format) {
        var typeResult = filamentService.getFilamentTypeById(f.filamentTypeId());
        
        if (format == OutputFormat.JSON) {
            // For JSON, create DTO with nested FilamentType
            FilamentType ft = typeResult instanceof dev.gertjanassies.filament.util.Result.Success<FilamentType, String> success
                ? success.value()
                : null;
            FilamentWithType dto = new FilamentWithType(f.id(), f.color(), f.price(), f.weight(), ft);
            return OutputFormatter.formatJson(dto);
        }
        
        if (format == OutputFormat.CSV) {
            // For CSV, use same format as list (single row)
            String[] headers = {"ID", "Name", "Manufacturer", "Type", "Diameter", "Nozzle Temp", "Bed Temp", "Density", "Color", "Price", "Weight"};
            
            String[] row;
            if (typeResult instanceof dev.gertjanassies.filament.util.Result.Success<FilamentType, String> success) {
                FilamentType ft = success.value();
                row = new String[] {
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
                row = new String[] {
                    String.valueOf(f.id()),
                    "?", "?", "?", "?", "?", "?", "?",
                    f.color(),
                    String.format("€%.2f", f.price()),
                    f.weight() + "g"
                };
            }
            
            // Create CSV with header and single row
            StringBuilder csv = new StringBuilder();
            csv.append(String.join(",", headers)).append("\n");
            csv.append(String.join(",", OutputFormatter.escapeCsvValues(row)));
            return csv.toString();
        }
        
        // For TABLE format, use key-value layout
        LinkedHashMap<String, String> data = new LinkedHashMap<>();
        data.put("ID", String.valueOf(f.id()));
        data.put("Color", f.color());
        data.put("Price", String.format("€%.2f", f.price()));
        data.put("Weight", f.weight() + "g");
        data.put("Filament Type ID", String.valueOf(f.filamentTypeId()));
        
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

        return OutputFormatter.formatTable(data);
    }

    @ShellMethod(key = "list", value = "Lists all filaments in the collection")
    public String listAll(
        @ShellOption(value = {"-o", "--output"}, defaultValue = "TABLE", help = "Output format: table, json, or csv") OutputFormat format) {
        return filamentService.getAllFilaments().fold(
            error -> "Failed to retrieve filaments: " + error,
            filaments -> formatFilaments(filaments, format)
        );
    }

    @ShellMethod(key = "add", value = "Adds a new filament to the collection. Usage: add [<color> <filamentTypeId> <price> <weight>]")
    public String addFilament(
        @ShellOption(defaultValue = ShellOption.NULL) String color,
        @ShellOption(defaultValue = ShellOption.NULL) Integer filamentTypeId,
        @ShellOption(defaultValue = ShellOption.NULL) Double price,
        @ShellOption(defaultValue = ShellOption.NULL) Integer weight) {

        // Interactive prompts if arguments not provided
        if (color == null) {
            color = inputHelper.readString("Color: ", String::trim, s -> s.length() > 2, "Color needs to be larger than 2 characters. Please enter a valid color.");
        }
        if (filamentTypeId == null) {
            filamentTypeId = inputHelper.readInteger("Filament Type ID: ");
        }
        if (price == null) {
            price = inputHelper.readDouble("Price (€): ");
        }
        if (weight == null) {
            weight = inputHelper.readInteger("Weight (grams): ");
        }

        var filament = new Filament(0, color, filamentTypeId, java.math.BigDecimal.valueOf(price), weight);
        return filamentService.addFilament(filament).fold(
            error -> "Failed to add filament: " + error,
            value -> "Filament added successfully:\n" + formatFilament(value, OutputFormat.TABLE)
        );
    }

    @ShellMethod(key = "get", value = "Gets a filament by its id. Usage: get <id>")
    public String getFilament(
        @ShellOption int id,
        @ShellOption(value = {"-o", "--output"}, defaultValue = "TABLE", help = "Output format: table, json, or csv") OutputFormat format) {
        return filamentService.getFilamentById(id).fold(
            error -> "Failed to get filament with id " + id + ": " + error,
            filament -> formatFilament(filament, format)
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