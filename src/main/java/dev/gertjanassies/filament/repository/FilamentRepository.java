package dev.gertjanassies.filament.repository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import dev.gertjanassies.filament.domain.Filament;

public interface FilamentRepository {
    List<Filament> findAll();
    Optional<Filament> findByCode(String code);
    void save(List<Filament> filaments) throws IOException;
    Optional<Filament> add(Filament filament);
    Optional<Filament> update(Filament filament);
    boolean deleteByCode(String code);
}
