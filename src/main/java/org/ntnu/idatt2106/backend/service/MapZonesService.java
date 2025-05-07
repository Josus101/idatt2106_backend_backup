package org.ntnu.idatt2106.backend.service;

import org.ntnu.idatt2106.backend.dto.map.CoordinatesDTO;
import org.ntnu.idatt2106.backend.dto.map.zones.MapZoneCreateDTO;
import org.ntnu.idatt2106.backend.dto.map.zones.MapZoneDescDTO;
import org.ntnu.idatt2106.backend.dto.map.zones.MapZoneFullDTO;
import org.ntnu.idatt2106.backend.model.map.Coordinate;
import org.ntnu.idatt2106.backend.model.map.CoordinatePolygon;
import org.ntnu.idatt2106.backend.model.map.CoordinateRing;
import org.ntnu.idatt2106.backend.model.map.MapZone;
import org.ntnu.idatt2106.backend.repo.map.MapZoneRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Service class for managing map zones.
 * This class provides methods to create, update, delete, and retrieve map zones.
 * It interacts with the MapZone repository to perform database operations.
 *
 * @author André Merkesdal
 * @since 0.1
 */
@Service
public class MapZonesService {

  // Repositories
  @Autowired
  private MapZoneRepo mapZoneRepo;

  /**
   * Retrieves all emergency zones from the database.
   *
   * @return a list of MapZoneFullDTO objects representing all emergency zones
   */
  public List<MapZoneFullDTO> getAllEmergencyZones() {
    return mapZoneRepo.findAll()
        .stream()
        .map(this::mapToMapZoneFullDTO)
        .toList();
  }

  /**
   * Retrieves all emergency zones within a specified area defined by a list of coordinates.
   *
   * @param coordinates a list of CoordinatesDTO objects representing the area
   * @param zoneIds     an array of zone IDs to exclude from the result
   * @return a list of MapZoneFullDTO objects representing the emergency zones within the specified area
   */
  public List<MapZoneFullDTO> getEmergencyZonesInMapArea(List<CoordinatesDTO> coordinates, Long[] zoneIds) {
    double minLat = coordinates.stream().mapToDouble(CoordinatesDTO::getLatitude).min().orElseThrow(NumberFormatException::new);
    double maxLat = coordinates.stream().mapToDouble(CoordinatesDTO::getLatitude).max().orElseThrow(NumberFormatException::new);
    double minLng = coordinates.stream().mapToDouble(CoordinatesDTO::getLongitude).min().orElseThrow(NumberFormatException::new);
    double maxLng = coordinates.stream().mapToDouble(CoordinatesDTO::getLongitude).max().orElseThrow(NumberFormatException::new);

    Set<Long> excludedIds = new HashSet<>(Arrays.asList(zoneIds));

    return mapZoneRepo.findAll()
      .stream()
      .filter(zone -> !excludedIds.contains(zone.getId()))
      .filter(zone -> {
        double lat = zone.getCoordinatePoint().getLatitude();
        double lng = zone.getCoordinatePoint().getLongitude();
        return lat >= minLat && lat <= maxLat && lng >= minLng && lng <= maxLng;
      })
      .map(this::mapToMapZoneFullDTO)
      .toList();
  }

  /**
   * Retrieves a specific emergency zone by its ID.
   *
   * @param id the ID of the emergency zone
   * @return a MapZoneFullDTO object representing the emergency zone with the specified ID
   */
  public MapZoneFullDTO getEmergencyZoneById(Long id) {
    return mapZoneRepo.findById(id)
        .map(this::mapToMapZoneFullDTO)
        .orElse(null);
  }

  /**
   * Retrieves a specific emergency zones description by its ID.
   *
   * @param id the ID of the emergency zone
   * @return a MapZoneDescDTO object representing the emergency zone with the specified ID
   */
  public MapZoneDescDTO getEmergencyZoneDescById(Long id) {
    return mapZoneRepo.findById(id)
      .map(zone -> new MapZoneDescDTO(
        zone.getName(),
        zone.getDescription(),
        zone.getAddress()))
      .orElse(null);
  }

  /**
   * Creates a new emergency zone in the database.
   *
   * @param mapZoneCreateDTO the MapZoneCreateDTO object containing the details of the new emergency zone
   * @return the ID of the newly created emergency zone
   */
  public Long createZone(MapZoneCreateDTO mapZoneCreateDTO) {
    MapZone mapZone = new MapZone(
      mapZoneCreateDTO.getName(),
      mapZoneCreateDTO.getDescription(),
      mapZoneCreateDTO.getAddress(),
      new Coordinate(
        mapZoneCreateDTO.getCoordinates().getLatitude(),
        mapZoneCreateDTO.getCoordinates().getLongitude()),
      mapZoneCreateDTO.getType(),
      mapZoneCreateDTO.getSeverityLevel());

    List<CoordinatePolygon> polygons = mapToCoordinatePolygons(mapZoneCreateDTO.getPolygonCoordinates(), mapZone);
    mapZone.setPolygons(polygons);

    return mapZoneRepo.save(mapZone).getId();
  }

  /**
   * Updates an existing emergency zone in the database.
   *
   * @param zoneId          the ID of the emergency zone to update
   * @param mapZoneCreateDTO the MapZoneCreateDTO object containing the updated details of the emergency zone
   */
  public void updateZone(Long zoneId, MapZoneCreateDTO mapZoneCreateDTO) {
    MapZone mapZone = mapZoneRepo.findById(zoneId)
        .orElseThrow(() -> new IllegalArgumentException("Zone (" + zoneId + ") not found"));

    mapZone.setName(mapZoneCreateDTO.getName());
    mapZone.setDescription(mapZoneCreateDTO.getDescription());
    mapZone.setAddress(mapZoneCreateDTO.getAddress());
    mapZone.setType(mapZoneCreateDTO.getType());
    mapZone.setSeverityLevel(mapZoneCreateDTO.getSeverityLevel());
    mapZone.setCoordinatePoint(new Coordinate(
      mapZoneCreateDTO.getCoordinates().getLatitude(),
      mapZoneCreateDTO.getCoordinates().getLongitude()));

    List<CoordinatePolygon> polygons = mapToCoordinatePolygons(mapZoneCreateDTO.getPolygonCoordinates(), mapZone);
    mapZone.setPolygons(polygons);

    mapZoneRepo.save(mapZone);
  }

  /**
   * Deletes an emergency zone from the database.
   *
   * @param id the ID of the emergency zone to delete
   */
  public void deleteZone(Long id) {
    MapZone mapZone = mapZoneRepo.findById(id)
      .orElseThrow(() -> new IllegalArgumentException("Zone ("+ id +") not found"));

    mapZoneRepo.delete(mapZone);
  }

  /**
   * Helper method to map a MapZone object to a MapZoneFullDTO object.
   *
   * @param zone the MapZone object to map
   * @return a MapZoneFullDTO object representing the mapped zone
   */
  private MapZoneFullDTO mapToMapZoneFullDTO(MapZone zone) {
    return new MapZoneFullDTO(
      zone.getId(),
      zone.getName(),
      zone.getDescription(),
      zone.getAddress(),
      new CoordinatesDTO(
        zone.getCoordinatePoint().getLatitude(),
        zone.getCoordinatePoint().getLongitude()),
      zone.getType(),
      zone.getSeverityLevel(),
      zone.getPolygons().stream()
        .map(polygon -> {
          List<CoordinatesDTO> outerRing = polygon.getOuterRing().getCoordinates().stream()
            .map(coordinate -> new CoordinatesDTO(
              coordinate.getLatitude(),
              coordinate.getLongitude()))
            .toList();

          List<List<CoordinatesDTO>> polygonRings = polygon.getInnerRings().stream()
            .map(innerRing -> innerRing.getCoordinates().stream()
              .map(coordinate -> new CoordinatesDTO(
                coordinate.getLatitude(),
                coordinate.getLongitude()))
              .toList())
            .toList();

          List<List<CoordinatesDTO>> modifiablePolygonRings = new ArrayList<>(polygonRings);
          modifiablePolygonRings.addFirst(outerRing);
          return modifiablePolygonRings;
        })
        .toList()
    );
  }

  /**
   * Helper method to map a list of polygon coordinates to a list of CoordinatePolygon objects.
   *
   * @param polygonCoordinates CoordinatesDTO objects representing the coordinates of the polygons
   * @param mapZone           the MapZone object to associate with the polygons
   * @return a list of CoordinatePolygon objects representing the mapped polygons
   */
  private List<CoordinatePolygon> mapToCoordinatePolygons(List<List<List<CoordinatesDTO>>> polygonCoordinates, MapZone mapZone) {
    List<CoordinatePolygon> polygons = polygonCoordinates.stream()
      .map(polygon -> {
        CoordinateRing outerRing = new CoordinateRing(
          polygon.getFirst().stream()
            .map(coordinate -> new Coordinate(
              coordinate.getLatitude(),
              coordinate.getLongitude()))
            .toList());
        List<CoordinateRing> innerRings = polygon.stream().skip(1)
          .map(innerRing -> new CoordinateRing(
            innerRing.stream()
              .map(coordinate -> new Coordinate(
                coordinate.getLatitude(),
                coordinate.getLongitude()))
              .toList()))
          .toList();
        return new CoordinatePolygon(outerRing, innerRings);
      }).toList();

    polygons.forEach(polygon -> polygon.setMapZone(mapZone));
    return polygons;
  }
}
