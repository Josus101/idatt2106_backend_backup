package org.ntnu.idatt2106.backend.repo;


import java.util.List;
import java.util.Optional;
import org.ntnu.idatt2106.backend.model.EmergencyService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.ntnu.idatt2106.backend.model.Type;

/**
 * Repository interface for EmergencyService entity.
 */
public interface EmergencyServiceRepo extends JpaRepository<EmergencyService, Integer> {


    /**
     * Finds an emergency service by its local ID.
     * @param localID the local ID of the emergency service
     * @return the emergency service with the given local ID
     */
    Optional<EmergencyService> findByLocalID(String localID);

  /**
   * Finds a service by its id.
   * @param id the id of the service
   * @return the service with the given id
   */
  Optional<EmergencyService> findById(int id);

  /**
   * Finds all services by their type id.
   * @param type the id of the type
   * @return a list of services with the given type id
   */
  List<EmergencyService> findAllByTypeId(Type type);

  /**
   * Finds all services by their type name.
   * @param name the name of the type
   * @return a list of services with the given type name
   */
  Optional<EmergencyService> findByName(String name);


}
