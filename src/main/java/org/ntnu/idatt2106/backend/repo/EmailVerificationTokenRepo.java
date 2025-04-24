package org.ntnu.idatt2106.backend.repo;

import java.util.Optional;
import org.ntnu.idatt2106.backend.models.EmailVerifyToken;
import org.ntnu.idatt2106.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailVerificationTokenRepo extends JpaRepository<EmailVerifyToken, Long> {
  Optional<EmailVerifyToken> findByToken(String token);
  Optional<EmailVerifyToken> findByUser(User user);
}
