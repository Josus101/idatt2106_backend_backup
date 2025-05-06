package org.ntnu.idatt2106.backend.service;

import org.ntnu.idatt2106.backend.dto.map.CoordinatesDTO;
import org.ntnu.idatt2106.backend.dto.map.zones.EmergencyZoneCreateDTO;
import org.ntnu.idatt2106.backend.dto.map.zones.EmergencyZoneDescDTO;
import org.ntnu.idatt2106.backend.dto.map.zones.EmergencyZoneFullDTO;
import org.ntnu.idatt2106.backend.model.map.Coordinate;
import org.ntnu.idatt2106.backend.model.map.CoordinatePolygon;
import org.ntnu.idatt2106.backend.model.map.CoordinateRing;
import org.ntnu.idatt2106.backend.model.map.MapZone;
import org.ntnu.idatt2106.backend.repo.map.MapZoneRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class MapZonesService {

  // Repositories
  @Autowired
  private MapZoneRepo mapZoneRepo;

  public List<EmergencyZoneFullDTO> getAllEmergencyZones() {
    return mapZoneRepo.findAll()
      .stream()
      .map(zone -> new EmergencyZoneFullDTO(
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

            polygonRings.addFirst(outerRing);
            return polygonRings;
          })
          .toList()))
      .toList();
  }

  public List<EmergencyZoneFullDTO> getEmergencyZonesInMapArea(List<CoordinatesDTO> coordinates, Long[] zoneIds) {
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
      .map(zone -> new EmergencyZoneFullDTO(
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

            polygonRings.addFirst(outerRing);
            return polygonRings;
          })
          .toList()))
      .toList();
  }

  public EmergencyZoneFullDTO getEmergencyZoneById(Long id) {
    return mapZoneRepo.findById(id)
      .map(zone -> new EmergencyZoneFullDTO(
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

            polygonRings.addFirst(outerRing);
            return polygonRings;
          })
          .toList()))
      .orElse(null);
  }

  public EmergencyZoneDescDTO getEmergencyZoneDescById(Long id) {
    return mapZoneRepo.findById(id)
      .map(zone -> new EmergencyZoneDescDTO(
        zone.getName(),
        zone.getDescription(),
        zone.getAddress()))
      .orElse(null);
  }

  public Long createZone(EmergencyZoneCreateDTO emergencyZoneCreateDTO) {
    // Creating a zone
    MapZone mapZone = new MapZone(
      emergencyZoneCreateDTO.getName(),
      emergencyZoneCreateDTO.getDescription(),
      emergencyZoneCreateDTO.getAddress(),
      new Coordinate(
        emergencyZoneCreateDTO.getCoordinates().getLatitude(),
        emergencyZoneCreateDTO.getCoordinates().getLongitude()),
      emergencyZoneCreateDTO.getType(),
      emergencyZoneCreateDTO.getSeverityLevel());

    // Creating polygons
    List<CoordinatePolygon> polygons = emergencyZoneCreateDTO.getPolygonCoordinates().stream()
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

    // Linking polygons to the zone
    polygons.forEach(polygon -> polygon.setMapZone(mapZone));
    mapZone.setPolygons(polygons);

    // Saving the zone to the database and returning the ID
    return mapZoneRepo.save(mapZone).getId();
  }

  public void updateZone(Long zoneId, EmergencyZoneCreateDTO emergencyZoneCreateDTO) {
    // Find existing zone
    MapZone mapZone = mapZoneRepo.findById(zoneId)
      .orElseThrow(() -> new IllegalArgumentException("Zone ("+ zoneId +") not found"));

    // Update zone properties
    mapZone.setName(emergencyZoneCreateDTO.getName());
    mapZone.setDescription(emergencyZoneCreateDTO.getDescription());
    mapZone.setAddress(emergencyZoneCreateDTO.getAddress());
    mapZone.setType(emergencyZoneCreateDTO.getType());
    mapZone.setSeverityLevel(emergencyZoneCreateDTO.getSeverityLevel());
    mapZone.setCoordinatePoint(new Coordinate(
      emergencyZoneCreateDTO.getCoordinates().getLatitude(),
      emergencyZoneCreateDTO.getCoordinates().getLongitude()));

    // Update polygons
    mapZone.getPolygons().clear();
    List<CoordinatePolygon> polygons = emergencyZoneCreateDTO.getPolygonCoordinates().stream()
        .map(polygon -> {
          CoordinateRing outerRing = new CoordinateRing(
              polygon.get(0).stream()
                  .map(coord -> new Coordinate(coord.getLatitude(), coord.getLongitude()))
                  .toList()
          );
          List<CoordinateRing> innerRings = polygon.stream()
              .skip(1)
              .map(ring -> new CoordinateRing(
                  ring.stream()
                      .map(coord -> new Coordinate(coord.getLatitude(), coord.getLongitude()))
                      .toList()
              ))
              .toList();
          return new CoordinatePolygon(outerRing, innerRings);
        })
        .toList();

    // Link polygons to the zone
    polygons.forEach(polygon -> polygon.setMapZone(mapZone));
    mapZone.setPolygons(polygons);

    // Save the updated zone to the database
    mapZoneRepo.save(mapZone);
  }

  public void deleteZone(Long id) {
    MapZone mapZone = mapZoneRepo.findById(id)
      .orElseThrow(() -> new IllegalArgumentException("Zone ("+ id +") not found"));

    mapZoneRepo.delete(mapZone);
  }

}
