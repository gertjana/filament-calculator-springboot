package dev.gertjanassies.filament.repository;

import java.io.IOException;
import java.util.Optional;

import dev.gertjanassies.filament.domain.FilamentType;

public interface FilamentTypeRepository {
    Optional<FilamentType> findByType(String type) throws IOException;
}
