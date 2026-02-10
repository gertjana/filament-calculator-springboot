package dev.gertjanassies.filament.repository;

import java.util.List;

import dev.gertjanassies.filament.domain.FilamentType;
import dev.gertjanassies.filament.util.Result;

/**
 * Repository interface for managing FilamentType entities.
 */
public interface FilamentTypeRepository {

    /**
     * Find all filament types.
     * @return A Result containing a list of filament types or an error message.
     */
    Result<List<FilamentType>, String> findAll();

    /**
     * Find a filament type by its id.
     * @param id The id of the filament type to find.
     * @return A Result containing the found filament type or an error message.
     */
    Result<FilamentType, String> findById(int id);

    /**
     * Save a list of filament types to the repository.
     * @param types The list of filament types to save.
     * @return A Result indicating success or failure of the save operation.
     */
    Result<Void, String> save(List<FilamentType> types);

    /**
     * Add a new filament type to the repository.
     * @param type The filament type to add.
     * @return A Result containing the added filament type or an error message.
     */
    Result<FilamentType, String> add(FilamentType type);

    /**
     * Update an existing filament type in the repository.
     * @param type The filament type with updated information.
     * @return A Result containing the updated filament type or an error message.
     */
    Result<FilamentType, String> update(FilamentType type);

    /**
     * Delete a filament type by its id.
     * @param id The id of the filament type to delete.
     * @return A Result indicating success or failure of the delete operation.
     */
    Result<Void, String> deleteById(int id);
}
