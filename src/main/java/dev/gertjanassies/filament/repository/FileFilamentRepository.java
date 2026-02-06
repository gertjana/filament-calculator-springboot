package dev.gertjanassies.filament.repository;

import dev.gertjanassies.filament.domain.Filament;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public List<Filament> findAll() {
        if (!Files.exists(filePath)) {
            return List.of();
        }
        
        try {
            return objectMapper.readValue(
                filePath.toFile(), 
                new TypeReference<List<Filament>>() {}
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to read filaments from: " + filePath, e);
        }
    }
    
    @Override
    public Optional<Filament> findByCode(String code) {
        return findAll().stream()
            .filter(f -> f.code().equals(code))
            .findFirst();
    }
    
    @Override
    public void save(List<Filament> filaments)  {
        try {
            objectMapper.writerWithDefaultPrettyPrinter()
            .writeValue(filePath.toFile(), filaments);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save filaments to: " + filePath, e);
        }
    }
    
    @Override
    public Optional<Filament> add(Filament filament) {
        List<Filament> filaments = new ArrayList<>(findAll());
        filaments.add(filament);
        save(filaments);
        return Optional.of(filament);
    }
    
    @Override
    public Optional<Filament> update(Filament filament) {
        List<Filament> filaments = findAll().stream()
            .map(f -> f.code().equals(filament.code()) ? filament : f)
            .toList();
        save(filaments);
        return Optional.of(filament);
    }
    
    @Override
    public boolean deleteByCode(String code) {
        List<Filament> filaments = findAll().stream()
            .filter(f -> !f.code().equals(code))
            .toList();
        save(filaments);
        return true;
    }
}
