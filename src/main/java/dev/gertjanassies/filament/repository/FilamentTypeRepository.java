package dev.gertjanassies.filament.repository;

import dev.gertjanassies.filament.domain.FilamentType;
import dev.gertjanassies.filament.util.Result;

public interface FilamentTypeRepository {
    Result<FilamentType, String> findByType(String type);
}
