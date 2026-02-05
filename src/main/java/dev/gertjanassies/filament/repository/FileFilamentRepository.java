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
    public List<Filament> findAll() throws IOException {
        if (!Files.exists(filePath)) {
            return List.of();
        }
        
        return objectMapper.readValue(
            filePath.toFile(), 
            new TypeReference<List<Filament>>() {}
        );
    }
    
    @Override
    public Optional<Filament> findByCode(String code) throws IOException {
        return findAll().stream()
            .filter(f -> f.code().equals(code))
            .findFirst();
    }
    
    @Override
    public void save(List<Filament> filaments) throws IOException {
        objectMapper.writerWithDefaultPrettyPrinter()
            .writeValue(filePath.toFile(), filaments);
    }
    
    @Override
    public void add(Filament filament) throws IOException {
        List<Filament> filaments = new ArrayList<>(findAll());
        filaments.add(filament);
        save(filaments);
    }
    
    @Override
    public void update(Filament filament) throws IOException {
        List<Filament> filaments = findAll().stream()
            .map(f -> f.code().equals(filament.code()) ? filament : f)
            .toList();
        save(filaments);
    }
    
    @Override
    public void deleteByCode(String code) throws IOException {
        List<Filament> filaments = findAll().stream()
            .filter(f -> !f.code().equals(code))
            .toList();
        save(filaments);
    }
}
