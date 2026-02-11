package dev.gertjanassies.filament.util;

import java.util.function.Function;
import java.util.function.Predicate;

import org.jline.reader.LineReader;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Helper class for reading and validating user input with retry logic.
 */
@Component
public class InputHelper {

    private final LineReader lineReader;

    public InputHelper(@Lazy LineReader lineReader) {
        this.lineReader = lineReader;
    }

    /**
     * Generic method to read and validate user input with retry logic.
     * 
     * @param <T> The type of value to parse
     * @param prompt The prompt text to display to the user
     * @param parser Function to parse the input string to type T
     * @param validator Optional predicate to validate the parsed value
     * @param errorMessage Error message to display on validation failure
     * @param maxAttempts Maximum number of retry attempts (default 3)
     * @return The validated parsed value
     * @throws IllegalStateException if all attempts failed
     */
    public <T> T readInput(String prompt, Function<String, T> parser, Predicate<T> validator, String errorMessage, int maxAttempts) {
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            try {
                String input = lineReader.readLine(prompt);
                T value = parser.apply(input);
                if (validator == null || validator.test(value)) {
                    return value;
                }
                System.out.println(errorMessage);
            } catch (Exception e) {
                System.out.println(errorMessage);
            }
        }
        throw new IllegalStateException("Failed to get valid input after " + maxAttempts + " attempts.");
    }

    /**
     * Read an integer value with validation.
     * 
     * @param prompt The prompt text to display
     * @return The validated integer value
     */
    public Integer readInteger(String prompt) {
        return readInput(
            prompt,
            Integer::parseInt,
            null,
            "Invalid number format. Please enter a valid integer.",
            3
        );
    }

    /**
     * Read a double value with validation.
     * 
     * @param prompt The prompt text to display
     * @return The validated double value
     */
    public Double readDouble(String prompt) {
        return readInput(
            prompt,
            Double::parseDouble,
            null,
            "Invalid number format. Please enter a valid decimal number.",
            3
        );
    }

    /**
     * Read a string value with optional parsing and validation.
     * 
     * @param prompt The prompt text to display
     * @param parser Function to transform the input string (e.g., toLowerCase, toUpperCase, capitalize)
     * @param validator Optional predicate to validate the parsed value
     * @param errorMessage Error message to display on validation failure
     * @return The validated and transformed string value
     * 
     * @example
     * // Convert to uppercase and validate minimum length:
     * String color = readString(
     *     "Color: ",
     *     String::toUpperCase,
     *     s -> s.length() >= 2,
     *     "Color must be at least 2 characters."
     * );
     * 
     * // Capitalize first letter and validate non-empty:
     * String name = readString(
     *     "Name: ",
     *     s -> s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase(),
     *     s -> !s.isBlank(),
     *     "Name cannot be empty."
     * );
     */
    public String readString(String prompt, Function<String, String> parser, Predicate<String> validator, String errorMessage) {
        return readInput(
            prompt,
            parser,
            validator,
            errorMessage,
            3
        );
    }

    /**
     * Read a simple string value (no parsing or validation).
     * 
     * @param prompt The prompt text to display
     * @return The input string
     */
    public String readString(String prompt) {
        return readInput(
            prompt,
            Function.identity(),
            s -> !s.isEmpty(),
            "Input cannot be empty.",
            3
        );
    }

    /**
     * Display a numbered list of options and let the user select one.
     * 
     * @param <T> The type of items in the list
     * @param prompt The prompt text to display
     * @param options The list of options to choose from
     * @param displayFunction Function to convert each item to a display string
     * @return The selected item
     * @throws IllegalStateException if the list is empty or user fails to select after max attempts
     * 
     * @example
     * FilamentType selected = inputHelper.selectFromList(
     *     "Select filament type",
     *     filamentTypes,
     *     ft -> ft.manufacturer() + " - " + ft.name()
     * );
     */
    public <T> T selectFromList(String prompt, java.util.List<T> options, Function<T, String> displayFunction) {
        if (options.isEmpty()) {
            throw new IllegalStateException("Cannot select from an empty list.");
        }

        System.out.println("\n" + prompt + ":");
        for (int i = 0; i < options.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + displayFunction.apply(options.get(i)));
        }

        Integer selection = readInput(
            "\nEnter selection (1-" + options.size() + "): ",
            Integer::parseInt,
            s -> s >= 1 && s <= options.size(),
            "Invalid selection. Please enter a number between 1 and " + options.size() + ".",
            3
        );
        
        return options.get(selection - 1);
    }
    
    /**
     * Display a numbered list of options and let the user select one.
     * Uses toString() for display.
     * 
     * @param <T> The type of items in the list
     * @param prompt The prompt text to display
     * @param options The list of options to choose from
     * @return The selected item
     */
    public <T> T selectFromList(String prompt, java.util.List<T> options) {
        return selectFromList(prompt, options, Object::toString);
    }
}
