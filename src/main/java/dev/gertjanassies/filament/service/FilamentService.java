package dev.gertjanassies.filament.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import dev.gertjanassies.filament.domain.CostCalculation;
import dev.gertjanassies.filament.domain.Filament;
import dev.gertjanassies.filament.repository.FilamentRepository;

@Service
public class FilamentService {
    private final FilamentRepository repository;
    
    public FilamentService(FilamentRepository repository) {
        this.repository = repository;
    }
    
    public List<Filament> getAllFilaments() throws IOException {
        return repository.findAll();
    }
    
    public Filament getFilamentByCode(String code) throws IOException {
        return repository.findByCode(code)
            .orElseThrow(() -> new IllegalArgumentException("Filament not found: " + code));
    }
    
    public void addFilament(Filament filament) throws IOException {
        repository.add(filament);
    }
    
    public void updateFilament(Filament filament) throws IOException {
        repository.update(filament);
    }
    
    public void deleteFilament(String code) throws IOException {
        repository.deleteByCode(code);
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

    private double getDensity(String type) {
        // Assuming PLA density of 1.24 g/cm³ and ABS density of 1.04 g/cm³
        return switch (type.toLowerCase()) {
            case "pla" -> 1.24;
            case "abs" -> 1.04;
            case "petg" -> 1.27;
            case "ngen" -> 1.28;
            case "nylon" -> 1.15;
            case "tpu" -> 1.20;
            default -> throw new IllegalArgumentException("Unknown filament type: " + type);
        };
    }

    /**
     * Calculates the cost of a given length of filament based on its diameter, density, and price per spool.
     * @param code of the filament
     * @param length in cm
     * @return cost in the same currency as the filament price
     * @throws IOException
     */
    public CostCalculation calculateCost(String code, double length) throws IOException {
        var filament = getFilamentByCode(code);

        double radiusCm = (filament.size() / 2) / 10;
        double volumeCm3 = Math.PI * Math.pow(radiusCm, 2) * (length);
        double weightGrams = volumeCm3 * getDensity(filament.type());
        double cost = (weightGrams / filament.weight()) * filament.price().doubleValue();
        
        return new CostCalculation(code, cost, weightGrams);
    }
}
