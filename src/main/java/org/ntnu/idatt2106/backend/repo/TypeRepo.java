package org.ntnu.idatt2106.backend.repo;

import org.ntnu.idatt2106.backend.model.Type;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TypeRepo extends JpaRepository<Type, Integer> {
    Optional<Type> findByName(String name);
}
