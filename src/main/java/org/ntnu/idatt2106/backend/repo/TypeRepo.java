package org.ntnu.idatt2106.backend.repo;

import org.ntnu.idatt2106.backend.model.Type;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for Type entity.
 */
public interface TypeRepo extends JpaRepository<Type, Integer> {
    /**
     * Finds a type by its name.
     * @param name the name of the type
     * @return the type with the given name
     */
    Optional<Type> findByName(String name);
}
