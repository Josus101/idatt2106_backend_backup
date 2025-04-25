package org.ntnu.idatt2106.backend.repo;
import org.ntnu.idatt2106.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * UserRepo is a repository interface for managing User entities.
 * It extends JpaRepository to provide CRUD operations and custom query methods.
 * @Author Konrad Seime
 * @since 0.1
 */
public interface UserRepo extends JpaRepository<User, Integer> {

  /**
   * Finds a User by their email.
   *
   * @param email the email of the User
   * @return an Optional containing the User if found, or empty if not
   */
  Optional<User> findByEmail(String email);
  /**
   * Finds a User by their id.
   *
   * @param id the id of the User
   * @return an Optional containing the User if found, or empty if not
   */
  Optional<User> findById(int id);
  /**
   * Finds a User by their phoneNumber.
   *
   * @param phoneNumber the phoneNumber of the User
   * @return an Optional containing the User if found, or empty if not
   */
  Optional<User> findByPhoneNumber(String phoneNumber);
}

