package dev.gertjanassies.filament.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import dev.gertjanassies.filament.domain.CostCalculation;
import dev.gertjanassies.filament.domain.Filament;
import dev.gertjanassies.filament.repository.FilamentRepository;
import dev.gertjanassies.filament.repository.FilamentTypeRepository;

@Service
public class FilamentService {
    private final FilamentRepository repository;
    private final FilamentTypeRepository typeRepository;
    
    public FilamentService(FilamentRepository repository, FilamentTypeRepository typeRepository) {
        this.repository = repository;
        this.typeRepository = typeRepository;
    }
    
    public List<Filament> getAllFilaments() throws IOException {
        return repository.findAll();
    }
    
    public Optional<Filament> getFilamentByCode(String code)  {
        return repository.findByCode(code);
    }
    
    public Optional<Filament> addFilament(Filament filament) {
        return repository.add(filament);
    }
    
    public Optional<Filament> updateFilament(Filament filament) {
        return repository.update(filament);
    }
    
    public boolean deleteFilament(String code) throws IOException {
        return repository.deleteByCode(code);
    }
    
    public List<Filament> findByManufacturer(String manufacturer) throws IOException {
        return getAllFilaments().stream()
            .filter(f -> f.manufacturer().equalsIgnoreCase(manufacturer))
            .toList();
    }
    
    public List<Filament> findByType(String type) throws IOException {
        return getAllFilaments().stream()
            .filter(f -> f.type().equalsIgnoreCase(type))
            .toList();
    }

    private double getDensity(String type) throws IOException {
        return typeRepository.findByType(type)
            .map(ft -> ft.density())
            .orElseThrow(() -> new IllegalArgumentException("Unknown filament type: " + type));
    }

    /**
     * Calculates the cost of a given length of filament based on its diameter, density, and price per spool.
     * @param code of the filament
     * @param length in cm
     * @return cost in the same currency as the filament price
     * @throws IOException
     * @throws IllegalArgumentException if the filament code is not found or if the filament type is unknown
     */
    public CostCalculation calculateCost(String code, double length) throws IOException, IllegalArgumentException {
        var f = getFilamentByCode(code).orElseThrow(() -> new IllegalArgumentException("Filament with code " + code + " not found."));

        double radiusCm = (f.size() / 2) / 10;
        double volumeCm3 = Math.PI * Math.pow(radiusCm, 2) * (length);
        double weightGrams = volumeCm3 * getDensity(f.type());
        double cost = (weightGrams / f.weight()) * f.price().doubleValue();
        
        return new CostCalculation(code, cost, weightGrams);
    }
}
