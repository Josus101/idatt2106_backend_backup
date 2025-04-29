package org.ntnu.idatt2106.backend.repo;

import java.util.Optional;
import org.ntnu.idatt2106.backend.model.ResetPasswordToken;
import org.ntnu.idatt2106.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

/**
 * ResetPasswordTokenRepo is a repository interface for managing ResetPasswordToken entities.
 * It extends JpaRepository to provide CRUD operations and custom query methods.
 * @Author Konrad Seime
 * @since 0.1
 */
public interface ResetPasswordTokenRepo extends JpaRepository<ResetPasswordToken, Long> {
  /**
   * Finds a ResetPasswordToken by its token.
   *
   * @param token the token of the ResetPasswordToken
   * @return an Optional containing the ResetPasswordToken if found, or empty if not
   */
  Optional<ResetPasswordToken> findByToken(String token);
  /**
   * Finds a ResetPasswordToken by its user.
   *
   * @param user the user of the ResetPasswordToken
   * @return an Optional containing the ResetPasswordToken if found, or empty if not
   */
  Optional<ResetPasswordToken> findByUser(User user);

  /**
   * Deletes a ResetPasswordToken by its user.
   *
   * @param userId the user of the ResetPasswordToken
   */
  @Modifying
  @Transactional
  void deleteAllByUserId(int userId);
}
