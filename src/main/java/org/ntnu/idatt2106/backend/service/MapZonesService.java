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

@Service
public class MapZonesService {

  // Repositories
  @Autowired
  private MapZoneRepo mapZoneRepo;

  public List<MapZoneFullDTO> getAllEmergencyZones() {
    return mapZoneRepo.findAll()
        .stream()
        .map(zone -> new MapZoneFullDTO(
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
                  modifiablePolygonRings.add(0, outerRing);
                  return modifiablePolygonRings;
                })
                .toList()))
        .toList();
  }

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
      .map(zone -> new MapZoneFullDTO(
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

  public MapZoneFullDTO getEmergencyZoneById(Long id) {
    return mapZoneRepo.findById(id)
        .map(zone -> new MapZoneFullDTO(
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

                  // Convert to a modifiable list
                  List<List<CoordinatesDTO>> modifiablePolygonRings = new ArrayList<>(polygonRings);
                  modifiablePolygonRings.addFirst(outerRing); // Add the outer ring at the beginning
                  return modifiablePolygonRings;
                })
                .toList()))
        .orElse(null);
  }

  public MapZoneDescDTO getEmergencyZoneDescById(Long id) {
    return mapZoneRepo.findById(id)
      .map(zone -> new MapZoneDescDTO(
        zone.getName(),
        zone.getDescription(),
        zone.getAddress()))
      .orElse(null);
  }

  public Long createZone(MapZoneCreateDTO mapZoneCreateDTO) {
    // Creating a zone
    MapZone mapZone = new MapZone(
      mapZoneCreateDTO.getName(),
      mapZoneCreateDTO.getDescription(),
      mapZoneCreateDTO.getAddress(),
      new Coordinate(
        mapZoneCreateDTO.getCoordinates().getLatitude(),
        mapZoneCreateDTO.getCoordinates().getLongitude()),
      mapZoneCreateDTO.getType(),
      mapZoneCreateDTO.getSeverityLevel());

    // Creating polygons
    List<CoordinatePolygon> polygons = mapZoneCreateDTO.getPolygonCoordinates().stream()
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

    List<CoordinatePolygon> polygons = mapZoneCreateDTO.getPolygonCoordinates().stream()
        .map(polygon -> {
          CoordinateRing outerRing = new CoordinateRing(
              polygon.getFirst().stream()
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

    polygons.forEach(polygon -> polygon.setMapZone(mapZone));
    mapZone.setPolygons(polygons);

    mapZoneRepo.save(mapZone);
  }

  public void deleteZone(Long id) {
    MapZone mapZone = mapZoneRepo.findById(id)
      .orElseThrow(() -> new IllegalArgumentException("Zone ("+ id +") not found"));

    mapZoneRepo.delete(mapZone);
  }

}
