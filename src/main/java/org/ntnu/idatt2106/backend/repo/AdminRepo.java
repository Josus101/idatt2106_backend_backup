package org.ntnu.idatt2106.backend.repo;

import java.util.Optional;
import org.ntnu.idatt2106.backend.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepo extends JpaRepository<Admin, Integer> {
  Optional<Admin> findByUsername(String username);
  boolean existsByUsername(String username);
  Optional<Admin> findById(int id);
  boolean existsById(int id);

}
