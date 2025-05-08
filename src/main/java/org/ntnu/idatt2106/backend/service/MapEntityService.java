package org.ntnu.idatt2106.backend.service;

import org.ntnu.idatt2106.backend.dto.map.CoordinatesDTO;
import org.ntnu.idatt2106.backend.dto.map.zones.MapZoneCreateDTO;
import org.ntnu.idatt2106.backend.dto.map.zones.MapZoneDescDTO;
import org.ntnu.idatt2106.backend.dto.map.zones.MapZoneFullDTO;
import org.ntnu.idatt2106.backend.model.map.*;
import org.ntnu.idatt2106.backend.repo.map.MapEntityRepo;
import org.ntnu.idatt2106.backend.repo.map.MapEntityTypeRepo;
import org.ntnu.idatt2106.backend.repo.map.MapMarkerTypeRepo;
import org.ntnu.idatt2106.backend.repo.map.MapZoneTypeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MapEntityService {

  // Repositories
  @Autowired
  private MapEntityRepo mapEntityRepo;
  @Autowired
  private MapEntityTypeRepo mapEntityTypeRepo;
  @Autowired
  private MapZoneTypeRepo mapZoneTypeRepo;
  @Autowired
  private MapMarkerTypeRepo mapMarkerTypeRepo;

  /**
   * Retrieves all emergency zones from the database.
   *
   * @return a list of MapZoneFullDTO objects representing all emergency zones
   */
  public List<MapZoneFullDTO> getAllMapZones() {
    return mapEntityRepo.findAllByMapEntityTypeName("zone")
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
  public List<MapZoneFullDTO> getMapZonesInMapArea(List<CoordinatesDTO> coordinates, Long[] zoneIds) {
    double minLat = coordinates.stream().mapToDouble(CoordinatesDTO::getLatitude).min().orElseThrow(NumberFormatException::new);
    double maxLat = coordinates.stream().mapToDouble(CoordinatesDTO::getLatitude).max().orElseThrow(NumberFormatException::new);
    double minLng = coordinates.stream().mapToDouble(CoordinatesDTO::getLongitude).min().orElseThrow(NumberFormatException::new);
    double maxLng = coordinates.stream().mapToDouble(CoordinatesDTO::getLongitude).max().orElseThrow(NumberFormatException::new);

    Set<Long> excludedIds = new HashSet<>(Arrays.asList(zoneIds));

    return mapEntityRepo.findAllByMapEntityTypeName("zone")
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
  public MapZoneFullDTO getMapZoneById(Long id) {
    return mapEntityRepo.findById(id)
        .map(this::mapToMapZoneFullDTO)
        .orElse(null);
  }
  /**
   * Retrieves a specific emergency zones description by its ID.
   *
   * @param id the ID of the emergency zone
   * @return a MapZoneDescDTO object representing the emergency zone with the specified ID
   */
  public MapZoneDescDTO getMapZoneDescById(Long id) {
    return mapEntityRepo.findById(id)
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
    MapEntityType entityType = mapEntityTypeRepo.findByName("zone")
        .orElseThrow(() -> new IllegalArgumentException("Zone entity type not found"));
    MapZoneType zoneType = mapZoneTypeRepo.findByName(mapZoneCreateDTO.getType())
        .orElseThrow(() -> new IllegalArgumentException("Zone type not found"));

    MapEntity zone = new MapEntity(
        mapZoneCreateDTO.getName(),
        mapZoneCreateDTO.getDescription(),
        mapZoneCreateDTO.getAddress(),
        mapZoneCreateDTO.getSeverityLevel(),
        entityType,
        zoneType,
        new Coordinate(
            mapZoneCreateDTO.getCoordinates().getLatitude(),
            mapZoneCreateDTO.getCoordinates().getLongitude()),
        mapZoneCreateDTO.getPolygonCoordinateList());

    return mapEntityRepo.save(zone).getId();
  }

  /**
   * Updates an existing emergency zone in the database.
   *
   * @param zoneId          the ID of the emergency zone to update
   * @param mapZoneCreateDTO the MapZoneCreateDTO object containing the updated details of the emergency zone
   */
  public void updateZone(Long zoneId, MapZoneCreateDTO mapZoneCreateDTO) {
    MapEntity zone = mapEntityRepo.findById(zoneId)
        .orElseThrow(() -> new IllegalArgumentException("Zone (" + zoneId + ") not found"));

    MapZoneType zoneType = mapZoneTypeRepo.findByName(mapZoneCreateDTO.getType())
        .orElseThrow(() -> new IllegalArgumentException("Zone type not found"));

    zone.setName(mapZoneCreateDTO.getName());
    zone.setDescription(mapZoneCreateDTO.getDescription());
    zone.setAddress(mapZoneCreateDTO.getAddress());
    zone.setSeverityLevel(mapZoneCreateDTO.getSeverityLevel());
    zone.setMapZoneType(zoneType);
    zone.setCoordinatePoint(new Coordinate(
        mapZoneCreateDTO.getCoordinates().getLatitude(),
        mapZoneCreateDTO.getCoordinates().getLongitude()));

    mapEntityRepo.save(zone);
  }

  /**
   * Deletes an emergency zone from the database.
   *
   * @param id the ID of the emergency zone to delete
   * @throws IllegalArgumentException if the zone id is not found
   */
  public void deleteZone(Long id) {
    MapEntity zone = mapEntityRepo.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Zone (" + id + ") not found"));

    mapEntityRepo.delete(zone);
  }

  /**
   * Helper method to map a MapEntity object to a MapZoneFullDTO object.
   *
   * @param zone the MapEntity object to map
   * @return a MapZoneFullDTO object representing the mapped zone
   */
  private MapZoneFullDTO mapToMapZoneFullDTO(MapEntity zone) {
    return new MapZoneFullDTO(
      zone.getId(),
      zone.getName(),
      zone.getDescription(),
      zone.getAddress(),
      zone.getSeverityLevel(),
      zone.getMapZoneType().getName(),
      new CoordinatesDTO(
          zone.getCoordinatePoint().getLatitude(),
          zone.getCoordinatePoint().getLongitude()),
      zone.getPolygonCoordinateList()
    );
  }
}
