package dev.gertjanassies.filament.repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.gertjanassies.filament.domain.FilamentType;

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
    
    private List<FilamentType> loadAll() throws IOException {
        if (!Files.exists(filePath)) {
            return List.of();
        }
        
        return objectMapper.readValue(
            filePath.toFile(), 
            new TypeReference<List<FilamentType>>() {}
        );
    }
    
    @Override
    public Optional<FilamentType> findByType(String type) throws IOException {
        return loadAll().stream()
            .filter(ft -> ft.type().equalsIgnoreCase(type))
            .findFirst();
    }
}
