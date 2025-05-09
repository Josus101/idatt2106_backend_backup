package org.ntnu.idatt2106.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ntnu.idatt2106.backend.model.map.Coordinate;
import org.ntnu.idatt2106.backend.model.map.MapEntity;
import org.ntnu.idatt2106.backend.model.map.MapEntityType;
import org.ntnu.idatt2106.backend.model.map.MapMarkerType;
import org.ntnu.idatt2106.backend.repo.map.MapEntityRepo;
import org.ntnu.idatt2106.backend.repo.map.MapEntityTypeRepo;
import org.ntnu.idatt2106.backend.repo.map.MapMarkerTypeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

/**
 * Service class for importing bunker data from a JSON file.
 *
 * @author  Erlend Eide Zindel
 * @since 1.0
 */
@Service
public class BunkerImportService {
    private final MapEntityRepo mapEntityRepo;
    private final MapMarkerTypeRepo mapMarkerTypeRepo;
    private final ObjectMapper objectMapper;
    private final MapEntityTypeRepo mapEntityTypeRepo;

    /**
     * Constructor for BunkerImportService.
     *
     * @param mapEntityRepo The repository for MapEntity entities.
     * @param mapMarkerTypeRepo The repository for MapMarkerType entities.
     * @param objectMapper The ObjectMapper for parsing JSON data.
     */
    @Autowired
    public BunkerImportService(MapEntityRepo mapEntityRepo,
                               MapMarkerTypeRepo mapMarkerTypeRepo,
                               ObjectMapper objectMapper,
                               MapEntityTypeRepo mapEntityTypeRepo) {
        this.mapEntityRepo = mapEntityRepo;
        this.mapMarkerTypeRepo = mapMarkerTypeRepo;
        this.objectMapper = objectMapper;
        this.mapEntityTypeRepo = mapEntityTypeRepo;
    }

    /**
     * Imports bunker data from a JSON file and saves it to the database.
     *
     * @param fileName The name of the JSON file to import.
     * @throws IOException If an error occurs while reading the file.
     */
    public void importBunkerDataFromJson(String fileName) throws IOException {
        JsonNode rootNode = readJsonFromFile(fileName);

        MapMarkerType bunkerMapMarkerType = mapMarkerTypeRepo.findByName("Bunker").orElseGet(() -> {
            MapMarkerType newMapMarkerType = new MapMarkerType("Bunker");
            return mapMarkerTypeRepo.save(newMapMarkerType);
        });

        MapEntityType entityType = mapEntityTypeRepo.findByName("marker").orElseGet(() -> {
            MapEntityType newMapEntityType = new MapEntityType("marker");
            return mapEntityTypeRepo.save(newMapEntityType);
        });

        for (JsonNode feature : rootNode.get("features")) {
            JsonNode properties = feature.get("properties");
            JsonNode geometry = feature.get("geometry");

            String localID = properties.get("lokalId").asText();
            String address = properties.get("adresse").asText();
            String capacity = properties.get("plasser").asText();

            double utmx = geometry.get("coordinates").get(0).asDouble();
            double utmy = geometry.get("coordinates").get(1).asDouble();

            // Convert UTM coordinates to latitude and longitude (UTM zone 33 for Norway).
            double[] latlon = UTMConverterService.utmToLatLon(utmx, utmy, 33);
            double latitude = latlon[0];
            double longitude = latlon[1];

            if (mapEntityRepo.findByLocalID(localID) == null) {
                MapEntity service = new MapEntity();
                service.setName("Bunker " + localID);
                service.setDescription("Bunker with capacity: " + capacity);
                service.setAddress(address);
                service.setMapEntityType(entityType);
                service.setMapMarkerType(bunkerMapMarkerType);
                service.setCoordinatePoint(new Coordinate(latitude, longitude));
                service.setLocalID(localID);

                mapEntityRepo.save(service);
            }
        }
    }

    /**
     * Helper method to read JSON from a file.
     * Can be mocked in tests for easier testing.
     */
    protected JsonNode readJsonFromFile(String fileName) throws IOException {
        ClassPathResource resource = new ClassPathResource(fileName);
        try (InputStream inputStream = resource.getInputStream()) {
            return objectMapper.readTree(inputStream);
        }
    }
}
