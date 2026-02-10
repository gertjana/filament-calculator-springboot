package dev.gertjanassies.filament.repository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
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
            @Value("${filament.type.config.path:.filament/filament-types.json}") String configPath) {
        this.objectMapper = objectMapper;
        this.filePath = Path.of(System.getProperty("user.home"), configPath);
    }

    @Override
    public Result<List<FilamentType>, String> findAll() {
        if (!Files.exists(filePath)) {
            return new Result.Success<>(List.of());
        }
        
        return Result.of(
            () -> objectMapper.readValue(
                filePath.toFile(), 
                new TypeReference<List<FilamentType>>() {}
            ),
            e -> "Failed to read filament types from: " + filePath + ": " + e.getMessage()
        );
    }
    
    @Override
    public Result<FilamentType, String> findById(int id) {
        return findAll()
            .flatMap(types -> types.stream()
                .filter(t -> t.id() == id)
                .findFirst()
                .<Result<FilamentType, String>>map(Result.Success::new)
                .orElse(new Result.Failure<>("Filament type not found: " + id))
            );
    }
    
    @Override
    public Result<Void, String> save(List<FilamentType> types) {
        return Result.of(
            () -> {
                Files.createDirectories(filePath.getParent());
                objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(filePath.toFile(), types);
                return null;
            },
            e -> "Failed to save filament types to " + filePath + ": " + e.getMessage()
        );
    }
    
    @Override
    public Result<FilamentType, String> add(FilamentType type) {
        return findAll()
            .flatMap(types -> {
                // Generate next ID
                int nextId = types.stream()
                    .mapToInt(FilamentType::id)
                    .max()
                    .orElse(0) + 1;
                
                // Create new filament type with generated ID
                FilamentType newType = new FilamentType(
                    nextId,
                    type.name(),
                    type.manufacturer(),
                    type.description(),
                    type.type(),
                    type.diameter(),
                    type.nozzleTemp(),
                    type.bedTemp(),
                    type.density()
                );
                
                List<FilamentType> updated = new ArrayList<>(types);
                updated.add(newType);
                
                // Save and return the new filament type directly
                return save(updated).map(v -> newType);
            });
    }
    
    @Override
    public Result<FilamentType, String> update(FilamentType type) {
        return findAll()
            .map(types -> {
                List<FilamentType> updated = new ArrayList<>(types);
                for (int i = 0; i < updated.size(); i++) {
                    if (updated.get(i).id() == type.id()) {
                        updated.set(i, type);
                        break;
                    }
                }
                return updated;
            })
            .flatMap(this::save)
            .map(v -> type);
    }
    
    @Override
    public Result<Void, String> deleteById(int id) {
        return findAll()
            .map(types -> types.stream()
                .filter(t -> t.id() != id)
                .toList())
            .flatMap(this::save);
    }
}
