package org.ntnu.idatt2106.backend.repo;

import org.ntnu.idatt2106.backend.models.EmailVerifyToken;
import org.ntnu.idatt2106.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailVerificationTokenRepo extends JpaRepository<EmailVerifyToken, Long> {
  EmailVerifyToken findByToken(String token);
  EmailVerifyToken findByUser(User user);
}
