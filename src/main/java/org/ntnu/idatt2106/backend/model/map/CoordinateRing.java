package org.ntnu.idatt2106.backend.model.map;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a ring of coordinates that can be used to define a polygon.
 * It is a part of the map model in the application.
 *
 * @author André Merkesdal
 * @since 0.1
 */
@Entity
@Table(name = "coordinate_ring")
@Getter
@Setter
public class CoordinateRing {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderColumn(name = "coordinate_seq_index")
  private List<Coordinate> coordinates = new ArrayList<>();

  @ManyToOne
  @JoinColumn(name = "polygon_id")
  private CoordinatePolygon polygon;

  /**
   * Blank constructor for the CoordinateRing model
   */
  public CoordinateRing() {}

  /**
   * Constructor for the CoordinateRing model
   *
   * @param coordinates of the ring
   */
  public CoordinateRing(List<Coordinate> coordinates) {
    this.coordinates = coordinates;
  }
}
