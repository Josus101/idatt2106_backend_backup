package org.ntnu.idatt2106.backend.repo;

import java.util.Optional;
import org.ntnu.idatt2106.backend.model.EmailVerifyToken;
import org.ntnu.idatt2106.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

/**
 * EmailVerificationTokenRepo is a repository interface for managing EmailVerifyToken entities.
 * It extends JpaRepository to provide CRUD operations and custom query methods.
 * @Author Konrad Seime
 * @since 0.1
 */
public interface EmailVerificationTokenRepo extends JpaRepository<EmailVerifyToken, Long> {
  /**
   * Finds an EmailVerifyToken by its token.
   *
   * @param token the token of the EmailVerifyToken
   * @return an Optional containing the EmailVerifyToken if found, or empty if not
   */
  Optional<EmailVerifyToken> findByToken(String token);
  /**
   * Finds an EmailVerifyToken by its user.
   *
   * @param user the user of the EmailVerifyToken
   * @return an Optional containing the EmailVerifyToken if found, or empty if not
   */
  Optional<EmailVerifyToken> findByUser(User user);

  /**
   * Deletes all EmailVerifyTokens associated with a user ID.
   *
   * @param id the ID of the user
   */
  @Transactional
  @Modifying
  void deleteAllByUserId(int id);
}
