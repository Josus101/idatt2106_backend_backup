package org.ntnu.idatt2106.backend.model.map;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


/**
 * The model that represents a type of emergency service
 *
 * @Author André Merkesdal
 * @version 0.2
 * @since 0.1
 */
@Entity
@Table(name = "map_entity_type")
@Getter
@Setter
public class MapEntityType {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(nullable = false, unique = true)
  private String name;

  @OneToMany(mappedBy = "mapEntityType")
  private List<MapEntity> entities;

  /**
   * Blank constructor for the MapEntityType model
   */
  public MapEntityType() {}

  /**
   * Constructor for the MapEntityType model
   * @param id of the type
   * @param name of the type
   * @param entities the services that are of this type
   */
  public MapEntityType(int id, String name, List<MapEntity> entities) {
    this.id = id;
    this.name = name;
    this.entities = entities;
  }

  /**
   * Constructor for the MapEntityType model
   * @param id of the type
   * @param name of the type
   */
  public MapEntityType(int id, String name) {
    this(id, name, null);
  }

  /**
   * Constructor for the MapEntityType model
   * @param name of the type
   */
  public MapEntityType(String name) {
    this(0, name, null);
  }

}
