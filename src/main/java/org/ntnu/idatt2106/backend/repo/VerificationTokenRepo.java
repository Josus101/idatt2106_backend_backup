package org.ntnu.idatt2106.backend.repo;

import java.util.Optional;
import org.ntnu.idatt2106.backend.model.VerificationToken;
import org.ntnu.idatt2106.backend.model.User;
import org.ntnu.idatt2106.backend.model.VerificationTokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

/**
 * VerificationTokenRepo is a repository interface for managing VerificationToken entities.
 *
 * @Author Konrad Seime
 * @since 0.2
 */
public interface VerificationTokenRepo extends JpaRepository<VerificationToken, Long> {
  /**
   * Finds an VerificationToken by its token.
   *
   * @param token the token of the VerificationToken
   * @return an Optional containing the VerificationToken if found, or empty if not
   */
  Optional<VerificationToken> findByToken(String token);
  /**
   * Finds an VerificationToken by its user.
   *
   * @param email the email of the VerificationToken
   * @return an Optional containing the VerificationToken if found, or empty if not
   */
  Optional<VerificationToken> findByEmail(String email);

  /**
   * Deletes all VerificationTokens associated with a user ID.
   *
   * @param email the email of the user
   */
  @Transactional
  @Modifying
  void deleteAllByEmail(String email);

  /**
   * Deletes all VerificationTokens associated with a user ID and a specific type.
   *
   * @param email the email of the user
   * @param verificationTokenType the type of the VerificationToken
   */
  @Transactional
  @Modifying
  void deleteAllByEmailAndType(String email, VerificationTokenType verificationTokenType);
}
