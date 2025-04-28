package org.ntnu.idatt2106.backend.controller;

import org.ntnu.idatt2106.backend.service.BunkerImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bunker")
public class BunkerImportController {

    @Autowired
    private BunkerImportService bunkerImportService;

    /**
     * Endpoint to import bunker data from a JSON file
     */
    @PostMapping("/import")
    public ResponseEntity<String> importBunkerData() {
        try {
            bunkerImportService.importBunkerDataFromJson("Samfunnssikkerhet_0000_Norge_25833_TilfluktsromOffentlige_GeoJSON.json");
            return ResponseEntity.ok("Bunker data imported successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Import failed: " + e.getMessage());
        }
    }
}
