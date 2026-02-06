package dev.gertjanassies.filament;

import dev.gertjanassies.filament.config.FilamentRuntimeHints;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.shell.command.annotation.CommandScan;

@SpringBootApplication
@CommandScan
@ImportRuntimeHints(FilamentRuntimeHints.class)
public class FilamentApplication {
	public static void main(String[] args) {
		SpringApplication.run(FilamentApplication.class, args);
	}

}
