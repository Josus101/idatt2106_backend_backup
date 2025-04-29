package org.ntnu.idatt2106.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ntnu.idatt2106.backend.model.EmergencyService;
import org.ntnu.idatt2106.backend.model.Type;
import org.ntnu.idatt2106.backend.repo.EmergencyServiceRepo;
import org.ntnu.idatt2106.backend.repo.TypeRepo;
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
    private final EmergencyServiceRepo emergencyServiceRepo;
    private final TypeRepo typeRepo;
    private final ObjectMapper objectMapper;

    /**
     * Constructor for BunkerImportService.
     *
     * @param emergencyServiceRepo The repository for EmergencyService entities.
     * @param typeRepo The repository for Type entities.
     * @param objectMapper The ObjectMapper for parsing JSON data.
     */
    @Autowired
    public BunkerImportService(EmergencyServiceRepo emergencyServiceRepo, TypeRepo typeRepo, ObjectMapper objectMapper) {
        this.emergencyServiceRepo = emergencyServiceRepo;
        this.typeRepo = typeRepo;
        this.objectMapper = objectMapper;
    }

    /**
     * Imports bunker data from a JSON file and saves it to the database.
     *
     * @param fileName The name of the JSON file to import.
     * @throws IOException If an error occurs while reading the file.
     */
    public void importBunkerDataFromJson(String fileName) throws IOException {
        JsonNode rootNode = readJsonFromFile(fileName);

        Type bunkerType = typeRepo.findByName("Bunker").orElseGet(() -> {
            Type newType = new Type("Bunker");
            return typeRepo.save(newType);
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

            if (emergencyServiceRepo.findByLocalID(localID).isEmpty()) {
                EmergencyService service = new EmergencyService();
                service.setLocalID(localID);
                service.setName(address);
                service.setDescription(capacity);
                service.setLongitude(longitude);
                service.setLatitude(latitude);
                service.setType(bunkerType);

                emergencyServiceRepo.save(service);
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
