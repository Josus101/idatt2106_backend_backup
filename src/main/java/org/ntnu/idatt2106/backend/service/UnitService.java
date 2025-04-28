package org.ntnu.idatt2106.backend.service;

import org.ntnu.idatt2106.backend.dto.unit.UnitGetResponse;
import org.ntnu.idatt2106.backend.repo.UnitRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for handling unit-related operations.
 * This class is responsible for the business logic related to units.
 * @author Jona Reiher
 * @version 0.1
 * @since 0.1
 */
@Service
public class UnitService {

  // Repositories
  @Autowired
  private UnitRepo unitRepo;

  /**
   * Retrieves a unit by its ID.
   *
   * @param id the ID of the unit
   * @return the unit with the given ID
   */
  public UnitGetResponse getUnitById(int id) {
    return unitRepo.findById(id)
      .map(unit -> new UnitGetResponse(unit.getId(), unit.getName()))
      .orElseThrow(() -> new IllegalArgumentException("Unit not found"));
  }

  /**
   * Retrieves all units.
   *
   * @return a list of all units
   */
  public List<UnitGetResponse> getAllUnits() {
    return unitRepo.findAll()
            .stream()
            .map(unit -> new UnitGetResponse(unit.getId(), unit.getName()))
            .toList();
  }

}
