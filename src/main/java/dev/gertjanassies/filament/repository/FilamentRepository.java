package dev.gertjanassies.filament.repository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import dev.gertjanassies.filament.domain.Filament;

public interface FilamentRepository {
    List<Filament> findAll() throws IOException;
    Optional<Filament> findByCode(String code) throws IOException;
    void save(List<Filament> filaments) throws IOException;
    void add(Filament filament) throws IOException;
    void update(Filament filament) throws IOException;
    void deleteByCode(String code) throws IOException;
}
