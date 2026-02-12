package dev.gertjanassies.filament.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.command.CommandExceptionResolver;
import org.springframework.shell.command.CommandHandlingResult;
import org.springframework.shell.CommandNotFound;

@Configuration
public class CustomExceptionResolver {

    @Bean
    CommandExceptionResolver commandExceptionResolver() {
        return exception -> {
            if (exception instanceof CommandNotFound) {
                return CommandHandlingResult.of("Error: " + exception.getMessage() + "\n", 1);
            }
            // Handle other exceptions with a generic message
            if (exception.getMessage() != null && !exception.getMessage().isEmpty()) {
                return CommandHandlingResult.of("Error: " + exception.getMessage() + "\n", 1);
            }
            return CommandHandlingResult.of("Error: " + exception.getClass().getSimpleName() + "\n", 1);
        };
    }
}
