package org.ntnu.idatt2106.backend.service;

import org.ntnu.idatt2106.backend.dto.map.CoordinatesDTO;
import org.ntnu.idatt2106.backend.dto.map.zones.ZoneCreateDTO;
import org.ntnu.idatt2106.backend.dto.map.MapEntityDescDTO;
import org.ntnu.idatt2106.backend.dto.map.zones.ZoneFullDTO;
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
   * @return a list of ZoneFullDTO objects representing all emergency zones
   */
  public List<ZoneFullDTO> getAllMapZones() {
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
   * @return a list of ZoneFullDTO objects representing the emergency zones within the specified area
   */
  public List<ZoneFullDTO> getMapZonesInMapArea(List<CoordinatesDTO> coordinates, Long[] zoneIds) {
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
   * @return a ZoneFullDTO object representing the emergency zone with the specified ID
   */
  public ZoneFullDTO getMapZoneById(Long id) {
    return mapEntityRepo.findById(id)
        .map(this::mapToMapZoneFullDTO)
        .orElse(null);
  }
  /**
   * Retrieves a specific emergency zones description by its ID.
   *
   * @param id the ID of the emergency zone
   * @return a MapEntityDescDTO object representing the emergency zone with the specified ID
   */
  public MapEntityDescDTO getMapZoneDescById(Long id) {
    return mapEntityRepo.findById(id)
        .map(zone -> new MapEntityDescDTO(
            zone.getName(),
            zone.getDescription(),
            zone.getAddress()))
        .orElse(null);
  }

  /**
   * Creates a new emergency zone in the database.
   *
   * @param zoneCreateDTO the ZoneCreateDTO object containing the details of the new emergency zone
   * @return the ID of the newly created emergency zone
   */
  public Long createZone(ZoneCreateDTO zoneCreateDTO) {
    MapEntityType entityType = mapEntityTypeRepo.findByName("zone")
        .orElseThrow(() -> new IllegalArgumentException("Zone entity type not found"));
    MapZoneType zoneType = mapZoneTypeRepo.findByName(zoneCreateDTO.getType())
        .orElseThrow(() -> new IllegalArgumentException("Zone type not found"));

    MapEntity zone = new MapEntity(
        zoneCreateDTO.getName(),
        zoneCreateDTO.getDescription(),
        zoneCreateDTO.getAddress(),
        zoneCreateDTO.getSeverityLevel(),
        entityType,
        zoneType,
        new Coordinate(
            zoneCreateDTO.getCoordinates().getLatitude(),
            zoneCreateDTO.getCoordinates().getLongitude()),
        zoneCreateDTO.getPolygonCoordinateList());

    return mapEntityRepo.save(zone).getId();
  }

  /**
   * Updates an existing emergency zone in the database.
   *
   * @param zoneId          the ID of the emergency zone to update
   * @param zoneCreateDTO the ZoneCreateDTO object containing the updated details of the emergency zone
   */
  public void updateZone(Long zoneId, ZoneCreateDTO zoneCreateDTO) {
    MapEntity zone = mapEntityRepo.findById(zoneId)
        .orElseThrow(() -> new IllegalArgumentException("Zone (" + zoneId + ") not found"));

    MapZoneType zoneType = mapZoneTypeRepo.findByName(zoneCreateDTO.getType())
        .orElseThrow(() -> new IllegalArgumentException("Zone type not found"));

    zone.setName(zoneCreateDTO.getName());
    zone.setDescription(zoneCreateDTO.getDescription());
    zone.setAddress(zoneCreateDTO.getAddress());
    zone.setSeverityLevel(zoneCreateDTO.getSeverityLevel());
    zone.setMapZoneType(zoneType);
    zone.setCoordinatePoint(new Coordinate(
        zoneCreateDTO.getCoordinates().getLatitude(),
        zoneCreateDTO.getCoordinates().getLongitude()));

    mapEntityRepo.save(zone);
  }

  /**
   * Retrieves the coordinates of a specific map entity by its ID.
   *
   * @param id the ID of the map entity
   * @return a CoordinatesDTO object representing the coordinates of the map entity
   * @throws IllegalArgumentException if the entity with the specified ID is not found
   */
  public CoordinatesDTO getMapEntityCoordinates(Long id) {
    MapEntity entity = mapEntityRepo.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Entity (" + id + ") not found"));

    return new CoordinatesDTO(
        entity.getCoordinatePoint().getLatitude(),
        entity.getCoordinatePoint().getLongitude());
  }

  /**
   * Deletes a map entity from the database.
   *
   * @param id the ID of the entity to delete
   * @throws IllegalArgumentException if the entity with the specified ID is not found
   */
  public void deleteMapEntity(Long id) {
    MapEntity zone = mapEntityRepo.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Entity (" + id + ") not found"));

    mapEntityRepo.delete(zone);
  }

  /**
   * Helper method to map a MapEntity object to a ZoneFullDTO object.
   *
   * @param zone the MapEntity object to map
   * @return a ZoneFullDTO object representing the mapped zone
   */
  private ZoneFullDTO mapToMapZoneFullDTO(MapEntity zone) {
    return new ZoneFullDTO(
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
