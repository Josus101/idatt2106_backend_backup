package org.ntnu.idatt2106.backend.repo;

import java.util.Optional;
import org.ntnu.idatt2106.backend.models.ResetPasswordToken;
import org.ntnu.idatt2106.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResetPasswordTokenRepo extends JpaRepository<ResetPasswordToken, Long> {
  Optional<ResetPasswordToken> findByToken(String token);
  Optional<ResetPasswordToken> findByUser(User user);
}
