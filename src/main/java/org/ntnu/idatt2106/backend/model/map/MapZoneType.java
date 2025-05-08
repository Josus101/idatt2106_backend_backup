package org.ntnu.idatt2106.backend.model.map;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


/**
 * The model that represents a type of emergency service
 *
 * @Author Jonas Reiher
 * @since 0.1
 */
@Entity
@Table(name = "map_zone_type")
@Getter
@Setter
public class MapZoneType {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(nullable = false, unique = true)
  private String name;

  @OneToMany(mappedBy = "mapZoneType")
  private List<MapEntity> entities;

  /**
   * Blank constructor for the MapZoneType model
   */
  public MapZoneType() {}

  /**
   * Constructor for the MapZoneType model
   * @param id of the type
   * @param name of the type
   * @param entities the services that are of this type
   */
  public MapZoneType(int id, String name, List<MapEntity> entities) {
    this.id = id;
    this.name = name;
    this.entities = entities;
  }

  /**
   * Constructor for the MapZoneType model
   * @param id of the type
   * @param name of the type
   */
  public MapZoneType(int id, String name) {
    this(id, name, null);
  }

  /**
   * Constructor for the MapZoneType model
   * @param name of the type
   */
  public MapZoneType(String name) {
    this(0, name, null);
  }

}
