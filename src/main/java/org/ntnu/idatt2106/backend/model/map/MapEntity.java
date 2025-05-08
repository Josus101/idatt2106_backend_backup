package org.ntnu.idatt2106.backend.model.map;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Model class for map entities like markers and zones.
 * This class is used to represent entities in the map.
 *
 * @author André Merkesdal
 * @since 0.1
 */
@Entity
@Table(name = "map_entity")
@Getter
@Setter
public class MapEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String description;

  @Column(nullable = false)
  private String address;

  @Column(unique = true)
  private String localID;

  @Column
  private int severityLevel;

  @ManyToOne
  @JoinColumn(name = "mapEntityType", nullable = false)
  private MapEntityType mapEntityType;

  @ManyToOne
  @JoinColumn(name = "mapZoneType")
  private MapZoneType mapZoneType;

  @ManyToOne
  @JoinColumn(name = "mapMarkerType")
  private MapMarkerType mapMarkerType;

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "coordinate_id", referencedColumnName = "id")
  private Coordinate coordinatePoint;

  @Column
  private String polygonCoordinateList;

  /**
   * Blank constructor for the MapEntity model
   */
  public MapEntity() {}

  /**
   * Constructor for the MapEntity model for creating a zone
   */
  public MapEntity(String name,
                   String description,
                   String address,
                   int severityLevel,
                   MapEntityType mapEntityType,
                   MapZoneType mapZoneType,
                   Coordinate coordinatePoint,
                   String polygonCoordinateList) {
    this.name = name;
    this.description = description;
    this.address = address;
    this.severityLevel = severityLevel;
    this.mapEntityType = mapEntityType;
    this.mapZoneType = mapZoneType;
    this.mapMarkerType = null;
    this.coordinatePoint = coordinatePoint;
    this.polygonCoordinateList = polygonCoordinateList;
    this.localID = null;
  }

  /**
   * Constructor for the MapEntity model for creating a marker
   */
  public MapEntity(String name,
                   String description,
                   String address,
                   MapEntityType mapEntityType,
                   MapMarkerType mapMarkerType,
                   Coordinate coordinatePoint) {
    this.name = name;
    this.description = description;
    this.address = address;
    this.mapEntityType = mapEntityType;
    this.mapZoneType = null;
    this.mapMarkerType = mapMarkerType;
    this.coordinatePoint = coordinatePoint;
    this.severityLevel = 0;
    this.polygonCoordinateList = null;
    this.localID = null;
  }

  /**
   * Constructor for the MapEntity model for creating a marker with a localID
   */
  public MapEntity(String name,
                   String description,
                   String address,
                   MapEntityType mapEntityType,
                   MapMarkerType mapMarkerType,
                   Coordinate coordinatePoint,
                   String localID) {
    this.name = name;
    this.description = description;
    this.address = address;
    this.mapEntityType = mapEntityType;
    this.mapZoneType = null;
    this.mapMarkerType = mapMarkerType;
    this.coordinatePoint = coordinatePoint;
    this.severityLevel = 0;
    this.polygonCoordinateList = null;
    this.localID = localID;
  }
}
