package org.ntnu.idatt2106.backend.repo;

import java.util.Optional;
import org.ntnu.idatt2106.backend.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * AdminRepo is a repository interface for managing Admin entities.
 * It extends JpaRepository to provide CRUD operations and custom query methods.
 * @Author Konrad Seime
 * @since 0.1
 */
public interface AdminRepo extends JpaRepository<Admin, Integer> {
  /**
   * Finds an Admin by their username.
   *
   * @param username the username of the Admin
   * @return an Optional containing the Admin if found, or empty if not
   */
  Optional<Admin> findByUsername(String username);
  /**
   * Finds out if an Admin exists by their username.
   * @param username the username of the Admin
   * @return true if the Admin exists, false otherwise
  */
  boolean existsByUsername(String username);
  /**
   * Finds an Admin by their id.
   *
   * @param id the id of the Admin
   * @return an Optional containing the Admin if found, or empty if not
   */
  Optional<Admin> findById(int id);
  /**
   * Finds out if an Admin exists by their id.
   * @param id the id of the Admin
   * @return true if the Admin exists, false otherwise
  */
  boolean existsById(int id);

}
