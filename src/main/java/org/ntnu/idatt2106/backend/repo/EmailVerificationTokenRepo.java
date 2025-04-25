package org.ntnu.idatt2106.backend.repo;

import java.util.Optional;
import org.ntnu.idatt2106.backend.model.EmailVerifyToken;
import org.ntnu.idatt2106.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailVerificationTokenRepo extends JpaRepository<EmailVerifyToken, Long> {
  Optional<EmailVerifyToken> findByToken(String token);
  Optional<EmailVerifyToken> findByUser(User user);
}
