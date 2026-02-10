package dev.gertjanassies.filament.service;

import java.util.List;

import org.springframework.stereotype.Service;

import dev.gertjanassies.filament.domain.CostCalculation;
import dev.gertjanassies.filament.domain.Filament;
import dev.gertjanassies.filament.domain.FilamentType;
import dev.gertjanassies.filament.repository.FilamentRepository;
import dev.gertjanassies.filament.repository.FilamentTypeRepository;
import dev.gertjanassies.filament.util.Result;

@Service
public class FilamentService {
    private final FilamentRepository filamentRepository;
    private final FilamentTypeRepository typeRepository;
    
    public FilamentService(FilamentRepository filamentRepository, FilamentTypeRepository typeRepository) {
        this.filamentRepository = filamentRepository;
        this.typeRepository = typeRepository;
    }
    
    public Result<List<Filament>, String> getAllFilaments() {
        return filamentRepository.findAll();
    }
    
    public Result<Filament, String> getFilamentById(int id)  {
        return filamentRepository.findById(id);
    }
    
    public Result<FilamentType, String> getFilamentTypeById(int id) {
        return typeRepository.findById(id);
    }
    
    public Result<Filament, String> addFilament(Filament filament) {
        return filamentRepository.add(filament);
    }
    
    public Result<Filament, String> updateFilament(Filament filament) {
        return filamentRepository.update(filament);
    }
    
    public Result<Void, String> deleteFilament(int id) {
        return filamentRepository.deleteById(id);
    }

    /**
     * Calculates the cost of a given length of filament based on its diameter, density, and price per spool.
     * @param id of the filament
     * @param length in cm
     * @return cost in the same currency as the filament price
     */
    public Result<CostCalculation, String> calculateCost(int id, double length) {
        return getFilamentById(id).flatMap(f -> {
            if (f.weight() <= 0) {
                return new Result.Failure<>("Cannot calculate cost: filament weight must be greater than 0");
            }
            return getFilamentTypeById(f.filamentTypeId()).map(ft -> {
                double radiusCm = (ft.diameter() / 2) / 10;
                double volumeCm3 = Math.PI * Math.pow(radiusCm, 2) * length;
                double weightGrams = volumeCm3 * ft.density();
                double cost = (weightGrams / f.weight()) * f.price().doubleValue();
                return new CostCalculation(id, cost, weightGrams);
            });
        });
    }
}
