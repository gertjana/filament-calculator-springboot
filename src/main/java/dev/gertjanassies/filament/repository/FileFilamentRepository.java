package dev.gertjanassies.filament.repository;

import java.io.IOException;
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
        
        try {
            return new Result.Success<>(objectMapper.readValue(
                filePath.toFile(), 
                new TypeReference<List<Filament>>() {}
            ));
        } catch (IOException e) {
            return new Result.Failure<>("Failed to read filaments from: " + filePath);
        }
    }
    
    @Override
    public Result<Filament, String> findByCode(String code) {
        return findAll()
            .flatMap(filaments -> filaments.stream()
                .filter(f -> f.code().equals(code))
                .findFirst()
                .<Result<Filament, String>>map(Result.Success::new)
                .orElse(new Result.Failure<>("Filament not found: " + code))
            );
}
    
    @Override
    public Result<Void, String> save(List<Filament> filaments)  {
        try {
            objectMapper.writerWithDefaultPrettyPrinter()
            .writeValue(filePath.toFile(), filaments);
            return new Result.Success<>(null);
        } catch (IOException e) {
            return new Result.Failure<>("Failed to save filaments to: " + filePath);
        }
    }
    
    @Override
    public Result<Filament, String> add(Filament filament) {
        return findAll()
            .map(filaments -> {
                List<Filament> updated = new ArrayList<>(filaments);
                updated.add(filament);
                return updated;
            })
            .flatMap(this::save)
            .map(v -> filament);  // Transform Void to the added Filament
    }
    
    @Override
    public Result<Filament, String> update(Filament filament) {
        return findAll()
            .map(filaments -> {
                List<Filament> updated = new ArrayList<>(filaments);
                for (int i = 0; i < updated.size(); i++) {
                    if (updated.get(i).code().equals(filament.code())) {
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
    public Result<Void, String> deleteByCode(String code) {
        return findAll().map(filaments -> filaments.stream()
                .filter(f -> !f.code().equals(code))
                .toList())
            .flatMap(this::save);
    }
}
