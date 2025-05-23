package org.ntnu.idatt2106.backend.service;

import org.ntnu.idatt2106.backend.dto.map.CoordinatesDTO;
import org.ntnu.idatt2106.backend.dto.map.markers.MarkerCreateDTO;
import org.ntnu.idatt2106.backend.dto.map.markers.MarkerFullDTO;
import org.ntnu.idatt2106.backend.dto.map.types.TypeFullDTO;
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

/**
 * Service class for managing map entities such as zones and markers.
 *
 * @author André Merkesdal
 * @version 0.2
 * @since 0.2
 */
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
    return mapEntityRepo.findAllByMapEntityType_Name("zone")
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

    return mapEntityRepo.findAllByMapEntityType_Name("zone")
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
        .orElseThrow(() -> new NoSuchElementException("Zone with ID " + id + " not found"));
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
        .orElseGet(() -> {
          MapZoneType newZoneType = new MapZoneType();
          newZoneType.setName(zoneCreateDTO.getType());
          return mapZoneTypeRepo.save(newZoneType);
        });

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
        .orElseGet(() -> {
          MapZoneType newZoneType = new MapZoneType();
          newZoneType.setName(zoneCreateDTO.getType());
          return mapZoneTypeRepo.save(newZoneType);
        });

    zone.setName(zoneCreateDTO.getName());
    zone.setDescription(zoneCreateDTO.getDescription());
    zone.setAddress(zoneCreateDTO.getAddress());
    zone.setSeverityLevel(zoneCreateDTO.getSeverityLevel());
    zone.setMapZoneType(zoneType);

    Coordinate coord = zone.getCoordinatePoint();
    if (coord == null) {
      coord = new Coordinate(
          zoneCreateDTO.getCoordinates().getLatitude(),
          zoneCreateDTO.getCoordinates().getLongitude());
      zone.setCoordinatePoint(coord);
    } else {
      coord.setLatitude(zoneCreateDTO.getCoordinates().getLatitude());
      coord.setLongitude(zoneCreateDTO.getCoordinates().getLongitude());
    }

    zone.setPolygonCoordinateList(zoneCreateDTO.getPolygonCoordinateList());

    mapEntityRepo.save(zone);
  }

  /**
   * Retrieves all markers from the database.
   *
   * @return a list of MarkerFullDTO objects representing all markers
   */
  public List<MarkerFullDTO> getAllMapMarkers() {
    return mapEntityRepo.findAllByMapEntityType_Name("marker")
        .stream()
        .map(this::mapToMapMarkerFullDTO)
        .toList();
  }

  /**
   * Retrieves all markers within a specified area defined by a list of coordinates.
   *
   * @param coordinates a list of CoordinatesDTO objects representing the area
   * @param markerIds   an array of marker IDs to exclude from the result
   * @return a list of MarkerFullDTO objects representing the markers within the specified area
   */
  public List<MarkerFullDTO> getMapMarkersInMapArea(List<CoordinatesDTO> coordinates, Long[] markerIds) {
    double minLat = coordinates.stream().mapToDouble(CoordinatesDTO::getLatitude).min().orElseThrow(NumberFormatException::new);
    double maxLat = coordinates.stream().mapToDouble(CoordinatesDTO::getLatitude).max().orElseThrow(NumberFormatException::new);
    double minLng = coordinates.stream().mapToDouble(CoordinatesDTO::getLongitude).min().orElseThrow(NumberFormatException::new);
    double maxLng = coordinates.stream().mapToDouble(CoordinatesDTO::getLongitude).max().orElseThrow(NumberFormatException::new);

    Set<Long> excludedIds = new HashSet<>(Arrays.asList(markerIds));

    return mapEntityRepo.findAllByMapEntityType_Name("marker")
        .stream()
        .filter(marker -> !excludedIds.contains(marker.getId()))
        .filter(marker -> {
          double lat = marker.getCoordinatePoint().getLatitude();
          double lng = marker.getCoordinatePoint().getLongitude();
          return lat >= minLat && lat <= maxLat && lng >= minLng && lng <= maxLng;
        })
        .map(this::mapToMapMarkerFullDTO)
        .toList();
  }

  /**
   * Retrieves a specific marker by its ID.
   *
   * @param id the ID of the marker
   * @return a ZoneFullDTO object representing the marker with the specified ID
   */
  public MarkerFullDTO getMapMarkerById(Long id) {
    return mapEntityRepo.findById(id)
        .map(this::mapToMapMarkerFullDTO)
        .orElse(null);
  }

  /**
   * Creates a new marker in the database.
   *
   * @param markerCreateDTO the MarkerCreateDTO object containing the details of the new marker
   * @return the ID of the newly created marker
   */
  public Long createMarker(MarkerCreateDTO markerCreateDTO) {
    MapEntityType entityType = mapEntityTypeRepo.findByName("marker")
        .orElseThrow(() -> new IllegalArgumentException("Marker entity type not found"));
    MapMarkerType markerType = mapMarkerTypeRepo.findByName(markerCreateDTO.getType())
        .orElseGet(() -> {
          MapMarkerType newMarkerType = new MapMarkerType();
          newMarkerType.setName(markerCreateDTO.getType());
          return mapMarkerTypeRepo.save(newMarkerType);
        });

    MapEntity marker = new MapEntity(
        markerCreateDTO.getName(),
        markerCreateDTO.getDescription(),
        markerCreateDTO.getAddress(),
        entityType,
        markerType,
        new Coordinate(
            markerCreateDTO.getCoordinates().getLatitude(),
            markerCreateDTO.getCoordinates().getLongitude())
    );
    return mapEntityRepo.save(marker).getId();
  }

  /**
   * Updates an existing emergency marker in the database.
   *
   * @param markerId        the ID of the emergency marker to update
   * @param markerCreateDTO the MarkerCreateDTO object containing the updated details of the emergency marker
   */
  public void updateMarker(Long markerId, MarkerCreateDTO markerCreateDTO) {
    MapEntity marker = mapEntityRepo.findById(markerId)
        .orElseThrow(() -> new IllegalArgumentException("Marker (" + markerId + ") not found"));
    MapMarkerType markerType = mapMarkerTypeRepo.findByName(markerCreateDTO.getType())
        .orElseGet(() -> {
          MapMarkerType newMarkerType = new MapMarkerType();
          newMarkerType.setName(markerCreateDTO.getType());
          return mapMarkerTypeRepo.save(newMarkerType);
        });

    marker.setName(markerCreateDTO.getName());
    marker.setDescription(markerCreateDTO.getDescription());
    marker.setAddress(markerCreateDTO.getAddress());
    marker.setMapMarkerType(markerType);

    Coordinate coord = marker.getCoordinatePoint();
    if (coord == null) {
      coord = new Coordinate(
          markerCreateDTO.getCoordinates().getLatitude(),
          markerCreateDTO.getCoordinates().getLongitude());
      marker.setCoordinatePoint(coord);
    } else {
      coord.setLatitude(markerCreateDTO.getCoordinates().getLatitude());
      coord.setLongitude(markerCreateDTO.getCoordinates().getLongitude());
    }

    mapEntityRepo.save(marker);
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
   * Retrieves a specific emergency zones description by its ID.
   *
   * @param id the ID of the emergency zone
   * @return a MapEntityDescDTO object representing the emergency zone with the specified ID
   */
  public MapEntityDescDTO getMapEntityDescById(Long id) {
    return mapEntityRepo.findById(id)
        .map(entity -> new MapEntityDescDTO(
            entity.getName(),
            entity.getDescription(),
            entity.getAddress()))
        .orElse(null);
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
   * Retrieves all map zone types from the database.
   *
   * @return a list of TypeFullDTO objects representing all map zone types
   */
  public List<TypeFullDTO> getZoneTypes() {
    return mapZoneTypeRepo.findAll()
        .stream()
        .map(zoneType -> new TypeFullDTO(
            zoneType.getId(),
            zoneType.getName()))
        .toList();
  }

  /**
   * Retrieves all marker types from the database.
   *
   * @return a list of TypeFullDTO objects representing all marker types
   */
  public List<TypeFullDTO> getMarkerTypes() {
    return mapMarkerTypeRepo.findAll()
        .stream()
        .map(markerType -> new TypeFullDTO(
            markerType.getId(),
            markerType.getName()))
        .toList();
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

  /**
   * Helper method to map a MapEntity object to a MarkerFullDTO object.
   *
   * @param marker the MapEntity object to map
   * @return a MarkerFullDTO object representing the mapped marker
   */
  private MarkerFullDTO mapToMapMarkerFullDTO(MapEntity marker) {
    return new MarkerFullDTO(
        marker.getId(),
        marker.getName(),
        marker.getDescription(),
        marker.getAddress(),
        marker.getMapMarkerType().getName(),
        new CoordinatesDTO(
            marker.getCoordinatePoint().getLatitude(),
            marker.getCoordinatePoint().getLongitude())
    );
  }
}
