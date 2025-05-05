package org.ntnu.idatt2106.backend.service;

import org.ntnu.idatt2106.backend.dto.map.CoordinatesDTO;
import org.ntnu.idatt2106.backend.dto.map.zones.EmergencyZoneCreateDTO;
import org.ntnu.idatt2106.backend.dto.map.zones.EmergencyZoneDescDTO;
import org.ntnu.idatt2106.backend.dto.map.zones.EmergencyZoneFullDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MapZonesService {

  public List<EmergencyZoneFullDTO> getAllEmergencyZones() {
    return null;
  }

  public List<EmergencyZoneFullDTO> getEmergencyZonesInMapArea(List<CoordinatesDTO> coordinates, Long[] zoneIds) {
    return null;
  }

  public EmergencyZoneFullDTO getEmergencyZoneById(Long id) {
    return null;
  }

  public EmergencyZoneDescDTO getEmergencyZoneDescById(Long id) {
    return null;
  }

  public void createZone(EmergencyZoneCreateDTO emergencyZoneCreateDTO) {

  }

  public void updateZone(Long zoneId, EmergencyZoneCreateDTO emergencyZoneCreateDTO) {

  }

  public void deleteZone(Long id) {

  }

}
