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
}
