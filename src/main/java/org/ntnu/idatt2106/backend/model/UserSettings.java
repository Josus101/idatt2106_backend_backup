package org.ntnu.idatt2106.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity class representing user settings in the database.
 * This class is used to store user-specific settings.
 *
 * @Author Jonas Reiher
 * @since 0.1
 */
@Entity
@Table(name = "user_settings")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserSettings {

  @Id
  private int userId;

  @Column(nullable = false)
  private String settings;
}

