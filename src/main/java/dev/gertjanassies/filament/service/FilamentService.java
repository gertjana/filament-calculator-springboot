package dev.gertjanassies.filament.service;

import java.util.List;

import org.springframework.stereotype.Service;

import dev.gertjanassies.filament.domain.CostCalculation;
import dev.gertjanassies.filament.domain.Filament;
import dev.gertjanassies.filament.repository.FilamentRepository;
import dev.gertjanassies.filament.repository.FilamentTypeRepository;
import dev.gertjanassies.filament.util.Result;

@Service
public class FilamentService {
    private final FilamentRepository repository;
    private final FilamentTypeRepository typeRepository;
    
    public FilamentService(FilamentRepository repository, FilamentTypeRepository typeRepository) {
        this.repository = repository;
        this.typeRepository = typeRepository;
    }
    
    public Result<List<Filament>, String> getAllFilaments() {
        return repository.findAll();
    }
    
    public Result<Filament, String> getFilamentByCode(String code)  {
        return repository.findByCode(code);
    }
    
    public Result<Filament, String> addFilament(Filament filament) {
        return repository.add(filament);
    }
    
    public Result<Filament, String> updateFilament(Filament filament) {
        return repository.update(filament);
    }
    
    public Result<Void, String> deleteFilament(String code) {
        return repository.deleteByCode(code);
    }
    
    public Result<List<Filament>, String> findByManufacturer(String manufacturer) {
        return getAllFilaments().map(filaments ->
            filaments.stream().filter(f -> f.manufacturer().equalsIgnoreCase(manufacturer))
            .toList());
    }
    
    public Result<List<Filament>, String> findByType(String type) {
        return getAllFilaments().map(filaments ->
            filaments.stream().filter(f -> f.type().equalsIgnoreCase(type))
            .toList());
    }

    private Result<Double, String> getDensity(String type) {
        return typeRepository.findByType(type)
            .map(ft -> ft.density());
    }

    /**
     * Calculates the cost of a given length of filament based on its diameter, density, and price per spool.
     * @param code of the filament
     * @param length in cm
     * @return cost in the same currency as the filament price
     */
    public Result<CostCalculation, String> calculateCost(String code, double length) {
        return getFilamentByCode(code).flatMap(f -> 
                    getDensity(f.type()).map(density -> {
                        double radiusCm = (f.size() / 2) / 10;
                        double volumeCm3 = Math.PI * Math.pow(radiusCm, 2) * length;
                        double weightGrams = volumeCm3 * density;
                        double cost = (weightGrams / f.weight()) * f.price().doubleValue();
                        return new CostCalculation(code, cost, weightGrams);
                    })
                );
    }
    /*. Scala ZIO does it like this: (which is syntactic sugar for nested flatMaps and maps)
        for {
            filament <- getFilamentByCode(code)
            density <- getDensity(filament.`type`)
            radiusCm = (filament.size / 2) / 10
            volumeCm3 = Math.PI * Math.pow(radiusCm, 2) * length
            weightGrams = volumeCm3 * density
            cost = (weightGrams / filament.weight) * filament.price.doubleValue()
        } yield CostCalculation(code, cost, weightGrams)    
    */
}
