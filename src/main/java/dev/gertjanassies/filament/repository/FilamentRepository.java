package dev.gertjanassies.filament.repository;

import java.util.List;

import dev.gertjanassies.filament.domain.Filament;
import dev.gertjanassies.filament.util.Result;

/**
 * Repository interface for managing Filament entities.
 */
public interface FilamentRepository {

    /**
     * Find all filaments.
     * @return  A Result containing a list of filaments or an error message.
     */
    Result<List<Filament>, String> findAll();

    /**
     * Find a filament by its id.
     * @param id  The id of the filament to find.
     * @return  A Result containing the found filament or an error message.
     */

    Result<Filament, String> findById(int id);
    /**
     * Save a list of filaments to the repository.
     * @param filaments The list of filaments to save.
     * @return A Result indicating success or failure of the save operation.
     */

    Result<Void, String> save(List<Filament> filaments);
    /**
     * Add a new filament to the repository.
     * @param filament
     * @return A Result containing the added filament or an error message.
     */

    Result<Filament, String> add(Filament filament);
    /**
     * Update an existing filament in the repository.
     * @param filament The filament with updated information.
     * @return A Result containing the updated filament or an error message.
     */

    Result<Filament, String> update(Filament filament);

    /**
     * Delete a filament by its id.
     * @param id The id of the filament to delete.
     * @return A Result indicating success or failure of the delete operation.
     */
    Result<Void, String> deleteById(int id);
}
