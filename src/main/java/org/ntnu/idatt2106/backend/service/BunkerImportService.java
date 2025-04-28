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

@Service
public class BunkerImportService {
    private final EmergencyServiceRepo emergencyServiceRepo;
    private final TypeRepo typeRepo;
    private final ObjectMapper objectMapper;

    @Autowired
    public BunkerImportService(EmergencyServiceRepo emergencyServiceRepo, TypeRepo typeRepo, ObjectMapper objectMapper) {
        this.emergencyServiceRepo = emergencyServiceRepo;
        this.typeRepo = typeRepo;
        this.objectMapper = objectMapper;
    }

    public void importBunkerDataFromJson(String fileName) throws IOException {
        ClassPathResource resource = new ClassPathResource(fileName);
        JsonNode rootNode = objectMapper.readTree(resource.getInputStream());

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

            //Norge er utm-sone 33
            double [] latlon = UTMConverterService.utmToLatLon(utmx, utmy, 33);
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
}
