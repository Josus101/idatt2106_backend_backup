package org.ntnu.idatt2106.backend.model.map;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * This class represents a map marker.
 * It is currently empty and serves as a placeholder for future implementation.
 */
@Entity
@Table(name = "map_marker")
@Getter
@Setter
public class MapMarker {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "map_shared_id_seq")
  @SequenceGenerator(name = "map_shared_id_seq", sequenceName = "map_shared_id_sequence", allocationSize = 1)
  private Long id;

  /**
   * Blank constructor for the MapMarker model
   */
  public MapMarker() {}
}
