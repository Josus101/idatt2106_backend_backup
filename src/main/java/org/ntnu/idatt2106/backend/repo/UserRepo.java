package org.ntnu.idatt2106.backend.repo;
import org.ntnu.idatt2106.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Integer> {

  Optional<User> findByEmail(String email);
  Optional<User> findById(int id);
  Optional<User> findByPhoneNumber(String phoneNumber);
}

