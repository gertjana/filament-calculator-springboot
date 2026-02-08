package dev.gertjanassies.filament.repository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
    
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.gertjanassies.filament.domain.FilamentType;
import dev.gertjanassies.filament.util.Result;

@Repository
public class FileFilamentTypeRepository implements FilamentTypeRepository {
    private final ObjectMapper objectMapper;
    private final Path filePath;
    
    public FileFilamentTypeRepository(
            ObjectMapper objectMapper,
            @Value("${filament.types.path:.filament.types.json}") String configPath) {
        this.objectMapper = objectMapper;
        this.filePath = Path.of(configPath);
    }
    
    private Result<List<FilamentType>, String> loadAll() {
        if (!Files.exists(filePath)) {
            return new Result.Failure<>("File not found: " + filePath);
        }
        
        return Result.of(
            () -> objectMapper.readValue(
                filePath.toFile(), 
                new TypeReference<List<FilamentType>>() {}
            ),
            e -> "Failed to read filament types from " + filePath + ": " + e.getMessage()
        );
   }
    
    @Override
    public Result<FilamentType, String> findByType(String type) {
        return loadAll().flatMap(filamentTypes -> {
            return filamentTypes.stream()
                .filter(ft -> ft.type().equalsIgnoreCase(type))
                .findFirst()
                .<Result<FilamentType, String>>map(Result.Success::new)
                .orElse(new Result.Failure<>("Filament type not found: " + type));
        });
    }
}
