package dev.gertjanassies.filament.util;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;

import org.springframework.shell.table.ArrayTableModel;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.TableBuilder;
import org.springframework.shell.table.TableModel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utility for formatting output in different formats (TABLE, JSON, CSV).
 */
public class OutputFormatter {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Format a list of items as a table.
     * 
     * @param items The list of items to format
     * @param headers The column headers
     * @param rowMapper Function to map each item to a row of strings
     * @return Formatted table string
     */
    public static <T> String formatTable(List<T> items, String[] headers, Function<T, String[]> rowMapper) {
        if (items.isEmpty()) {
            return "No items found.";
        }

        String[][] data = new String[items.size() + 1][];
        data[0] = headers;

        for (int i = 0; i < items.size(); i++) {
            data[i + 1] = rowMapper.apply(items.get(i));
        }

        TableModel model = new ArrayTableModel(data);
        TableBuilder tableBuilder = new TableBuilder(model);
        return tableBuilder.addFullBorder(BorderStyle.fancy_light).build().render(140);
    }

    /**
     * Format a single item as a table (key-value pairs).
     * 
     * @param data The key-value pairs to display
     * @return Formatted table string
     */
    public static String formatTable(LinkedHashMap<String, String> data) {
        String[][] tableData = new String[data.size()][2];
        int i = 0;
        for (var entry : data.entrySet()) {
            tableData[i++] = new String[] {entry.getKey(), entry.getValue()};
        }

        TableModel model = new ArrayTableModel(tableData);
        TableBuilder tableBuilder = new TableBuilder(model);
        return tableBuilder.addFullBorder(BorderStyle.fancy_light).build().render(60);
    }

    /**
     * Format a list of items as JSON.
     * 
     * @param items The list of items to format
     * @return JSON string
     */
    public static <T> String formatJson(List<T> items) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(items);
        } catch (JsonProcessingException e) {
            return "Error formatting JSON: " + e.getMessage();
        }
    }

    /**
     * Format a single item as JSON.
     * 
     * @param item The item to format
     * @return JSON string
     */
    public static <T> String formatJson(T item) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(item);
        } catch (JsonProcessingException e) {
            return "Error formatting JSON: " + e.getMessage();
        }
    }

    /**
     * Format a list of items as CSV.
     * 
     * @param items The list of items to format
     * @param headers The column headers
     * @param rowMapper Function to map each item to a row of strings
     * @return CSV string
     */
    public static <T> String formatCsv(List<T> items, String[] headers, Function<T, String[]> rowMapper) {
        StringBuilder csv = new StringBuilder();
        
        // Add headers
        csv.append(String.join(",", escapeHeaders(headers))).append("\n");
        
        // Add rows
        for (T item : items) {
            String[] row = rowMapper.apply(item);
            csv.append(String.join(",", escapeCsvValues(row))).append("\n");
        }
        
        return csv.toString();
    }

    /**
     * Format a single item as CSV (key-value pairs as two columns).
     * 
     * @param data The key-value pairs
     * @return CSV string
     */
    public static String formatCsv(LinkedHashMap<String, String> data) {
        StringBuilder csv = new StringBuilder();
        csv.append("Field,Value\n");
        
        for (var entry : data.entrySet()) {
            csv.append(escapeCsvValue(entry.getKey()))
               .append(",")
               .append(escapeCsvValue(entry.getValue()))
               .append("\n");
        }
        
        return csv.toString();
    }

    /**
     * Escape CSV headers.
     */
    private static String[] escapeHeaders(String[] headers) {
        String[] escaped = new String[headers.length];
        for (int i = 0; i < headers.length; i++) {
            escaped[i] = escapeCsvValue(headers[i]);
        }
        return escaped;
    }

    /**
     * Escape CSV values.
     */
    public static String[] escapeCsvValues(String[] values) {
        String[] escaped = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            escaped[i] = escapeCsvValue(values[i]);
        }
        return escaped;
    }

    /**
     * Escape a single CSV value (quote if contains comma, quote, or newline).
     */
    private static String escapeCsvValue(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
