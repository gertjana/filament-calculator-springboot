package dev.gertjanassies.filament.repository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.gertjanassies.filament.domain.Filament;
import dev.gertjanassies.filament.util.Result;

@Repository
public class FileFilamentRepository implements FilamentRepository {
    private final ObjectMapper objectMapper;
    private final Path filePath;
    
    public FileFilamentRepository(
            ObjectMapper objectMapper,
            @Value("${filament.config.path:.filament.json}") String configPath) {
        this.objectMapper = objectMapper;
        this.filePath = Path.of(configPath);
    }

    @Override
    public Result<List<Filament>, String> findAll() {
        if (!Files.exists(filePath)) {
            return new Result.Failure<>("File not found: " + filePath);
        }
        
        return Result.of(
            () -> objectMapper.readValue(
                filePath.toFile(), 
                new TypeReference<List<Filament>>() {}
            ),
            e -> "Failed to read filaments from: " + filePath + ": " + e.getMessage()       
            
        );
    }
    
    @Override
    public Result<Filament, String> findById(int id) {
        return findAll()
            .flatMap(filaments -> filaments.stream()
                .filter(f -> f.id() == id)
                .findFirst()
                .<Result<Filament, String>>map(Result.Success::new)
                .orElse(new Result.Failure<>("Filament not found: " + id))
            );
}
    
    @Override
    public Result<Void, String> save(List<Filament> filaments)  {
        return Result.of(
            () -> {
                objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(filePath.toFile(), filaments);
                return null; // Return type is Void, so we return null on success
            },
            e -> "Failed to save filaments to " + filePath + ": " + e.getMessage()
        );
    }
    
    @Override
    public Result<Filament, String> add(Filament filament) {
        return findAll()
            .flatMap(filaments -> {
                // Generate next ID
                int nextId = filaments.stream()
                    .mapToInt(Filament::id)
                    .max()
                    .orElse(0) + 1;
                
                // Create new filament with generated ID
                Filament newFilament = new Filament(
                    nextId,
                    filament.color(),
                    filament.filamentTypeId(),
                    filament.price(),
                    filament.weight()
                );
                
                List<Filament> updated = new ArrayList<>(filaments);
                updated.add(newFilament);
                
                return save(updated).map(v -> newFilament);
            });
    }
    
    @Override
    public Result<Filament, String> update(Filament filament) {
        return findAll()
            .map(filaments -> {
                List<Filament> updated = new ArrayList<>(filaments);
                for (int i = 0; i < updated.size(); i++) {
                    if (updated.get(i).id() == filament.id()) {
                        updated.set(i, filament);
                        break;
                    }
                }
                return updated;
            })
            .flatMap(this::save)
            .map(v -> filament);  // Transform Void to the updated Filament
    }
    
    @Override
    public Result<Void, String> deleteById(int id) {
        return findAll().map(filaments -> filaments.stream()
                .filter(f -> f.id() != id)
                .toList())
            .flatMap(this::save);
    }
}
