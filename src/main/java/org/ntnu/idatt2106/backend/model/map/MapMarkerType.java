package org.ntnu.idatt2106.backend.model.map;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


/**
 * The model that represents a type for map markers
 *
 * @Author Jonas Reiher
 * @since 0.1
 */
@Entity
@Table(name = "map_marker_type")
@Getter
@Setter
public class MapMarkerType {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(nullable = false, unique = true)
  private String name;

  @OneToMany(mappedBy = "mapMarkerType")
  private List<MapEntity> entities;

  /**
   * Blank constructor for the MapMarkerType model
   */
  public MapMarkerType() {}

  /**
   * Constructor for the MapMarkerType model
   * @param id of the type
   * @param name of the type
   * @param entities the services that are of this type
   */
  public MapMarkerType(int id, String name, List<MapEntity> entities) {
    this.id = id;
    this.name = name;
    this.entities = entities;
  }

  /**
   * Constructor for the MapMarkerType model
   * @param id of the type
   * @param name of the type
   */
  public MapMarkerType(int id, String name) {
    this(id, name, null);
  }

  /**
   * Constructor for the MapMarkerType model
   * @param name of the type
   */
  public MapMarkerType(String name) {
    this(0, name, null);
  }

}
