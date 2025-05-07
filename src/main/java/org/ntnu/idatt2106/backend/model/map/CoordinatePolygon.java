package org.ntnu.idatt2106.backend.model.map;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a polygon defined by a list of coordinates.
 * It is used to represent the shape of a zone on the map.
 *
 * @author André Merkesdal
 * @since 0.1
 */
@Entity
@Table(name = "coordinate_polygon")
@Getter
@Setter
public class CoordinatePolygon {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "map_zone_id")
  private MapZone mapZone;

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  private CoordinateRing outerRing;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderColumn(name = "coordinate_seq_index")
  private List<CoordinateRing> innerRings = new ArrayList<>();

  /**
   * Blank constructor for the CoordinatePolygon model
   */
  public CoordinatePolygon() {}

  /**
   * Constructor for the CoordinatePolygon model
   *
   * @param outerRing  of the polygon
   * @param innerRings of the polygon
   */
  public CoordinatePolygon(CoordinateRing outerRing, List<CoordinateRing> innerRings) {
    this.outerRing = outerRing;
    this.innerRings = innerRings;
  }
}
