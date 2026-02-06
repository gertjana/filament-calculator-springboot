package dev.gertjanassies.filament.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class DirectoryInitializer implements CommandLineRunner {
    
    @Value("${filament.config.path}")
    private String configPath;
    
    @Value("${filament.types.path}")
    private String typesPath;
    
    @Override
    public void run(String... args) {
        ensureParentDirectoryExists(configPath);
        ensureParentDirectoryExists(typesPath);
    }
    
    private void ensureParentDirectoryExists(String filePath) {
        try {
            Path path = Path.of(filePath);
            Path parentDir = path.getParent();
            
            if (parentDir != null && !Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create directory for: " + filePath, e);
        }
    }
}
