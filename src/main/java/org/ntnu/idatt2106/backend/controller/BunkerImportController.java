package org.ntnu.idatt2106.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.ntnu.idatt2106.backend.service.BunkerImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * This class is used to handle all requests related to importing bunker data.
 * It contains an endpoint for importing bunker data from a JSON file.
 *
 * @author Erlend Eide Zindel
 * @since 0.1
 */
@RestController
@RequestMapping("/api/bunker")
public class BunkerImportController {

    @Autowired
    private BunkerImportService bunkerImportService;

    /**
     * Endpoint to import bunker data from a JSON file.
     *
     * @return a response entity containing a success message if the import was successful,
     *         or an error message if the import failed.
     */
    @PostMapping("/import")
    @Operation(
            summary = "Import bunker data",
            description = "Imports bunker data from a JSON file."
    )
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "200",
                description = "Bunker data imported successfully",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(example = "Bunker data imported successfully"))
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Import failed",
                content = @Content(
                    mediaType = "application/json",
                        examples = {
                            @ExampleObject(
                                name = "Import failed IO exception",
                                summary = "Import failed due to IO error",
                                value = "Import failed due to IO error: <error message>"
                            ),
                            @ExampleObject(
                                name = "Import failed",
                                summary = "Import failed",
                                value = "Import failed: <error message>"
                            )
                      })
            )
    })
    public ResponseEntity<String> importBunkerData() {
        try {
            bunkerImportService.importBunkerDataFromJson("Samfunnssikkerhet_0000_Norge_25833_TilfluktsromOffentlige_GeoJSON.json");
            return ResponseEntity.ok("Bunker data imported successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Import failed due to IO error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Import failed: " + e.getMessage());
        }
    }
}
