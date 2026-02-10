package dev.gertjanassies.filament.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import dev.gertjanassies.filament.domain.FilamentType;
import dev.gertjanassies.filament.repository.FilamentTypeRepository;
import dev.gertjanassies.filament.util.Result;

@Service
public class FilamentTypeService {
    private final FilamentTypeRepository repository;
    
    public FilamentTypeService(FilamentTypeRepository repository) {
        this.repository = repository;
    }
    
    public Result<List<FilamentType>, String> getAllFilamentTypes() {
        return repository.findAll()
            .map(types -> types.stream()
                .sorted(Comparator.comparing(FilamentType::manufacturer)
                    .thenComparing(FilamentType::name))
                .toList());
    }
    
    public Result<FilamentType, String> getFilamentTypeById(int id) {
        return repository.findById(id);
    }
    
    public Result<FilamentType, String> addFilamentType(FilamentType type) {
        return repository.add(type);
    }
    
    public Result<FilamentType, String> updateFilamentType(FilamentType type) {
        return repository.update(type);
    }
    
    public Result<Void, String> deleteFilamentType(int id) {
        return repository.deleteById(id);
    }
}
