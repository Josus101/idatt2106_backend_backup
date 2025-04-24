package org.ntnu.idatt2106.backend.repo;

import org.ntnu.idatt2106.backend.models.ResetPasswordToken;
import org.ntnu.idatt2106.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResetPasswordTokenRepo extends JpaRepository<ResetPasswordToken, Long> {
  ResetPasswordToken findByToken(String token);
  ResetPasswordToken findByUser(User user);
}
