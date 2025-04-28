package org.ntnu.idatt2106.backend.repo;

import java.util.Optional;
import org.ntnu.idatt2106.backend.model.Type;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for type entity.
 */
public interface TypeRepo extends JpaRepository<Type, Integer> {
  /**
   * Finds a type by its id.
    * @param id the id of the type
   * @return the type with the given id
   */
  Optional<Type> findById(int id);

  /**
   * Finds a type by its name.
   * @param name the name of the type
   * @return the type with the given name
   */
  Optional<Type> findByName(String name);
}
