package org.ntnu.idatt2106.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.ntnu.idatt2106.backend.dto.admin.AdminLoginRegisterRequest;
import org.ntnu.idatt2106.backend.exceptions.UnauthorizedException;
import org.ntnu.idatt2106.backend.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class is used to handle all the requests related to admin users.
 * It contains endpoints for creating, deleting, and elevating admin users.
 *
 * @Author Konrad Seime
 * @since 0.1
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

  @Autowired
  private AdminService adminService;

  @PostMapping("/createAdmin")
  @Operation(
      summary = "Create a new admin user",
      description = "Post request to create a new admin user. "
          + "Requires superuser privileges. "
          + "Returns true if the admin was created successfully, false otherwise."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Admin user created successfully",
          content = @Content(
              schema = @Schema(implementation = Boolean.class)
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Invalid request"
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Unauthorized"
      )
  })
  public ResponseEntity<Boolean> addAdminUser(
      @Parameter(
          name = "Authorization",
          description = "Bearer token in the format `Bearer <JWT>`",
          required = true,
          example = "Bearer eyJhbGciOiJIUzI1N.iIsInR5cCI6IkpXVCJ9..."
      )
      @RequestBody AdminLoginRegisterRequest admin,
      @RequestHeader("Authorization") String authorizationHeader) {

    try {
      adminService.register(admin.getUsername(), admin.getPassword(), authorizationHeader);
    } catch (UnauthorizedException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    return ResponseEntity.ok(true);
  }

  @PostMapping("/elevate/{id}")
  @Operation(
      summary = "Elevates an admin user to a superuseer",
      description = "Post request to elevate an admin user to a superuser. "
          + "Requires superuser privileges. "
          + "Returns true if the admin was elevated successfully, false otherwise."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Admin user elevated successfully",
          content = @Content(
              schema = @Schema(implementation = Boolean.class)
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Invalid request"
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Unauthorized"
      )
  })
  public ResponseEntity<Boolean> elevateAdminUser(
      @Parameter(
          name = "Authorization",
          description = "Bearer token in the format `Bearer <JWT>`",
          required = true,
          example = "Bearer eyJhbGciOiJIUzI1N.iIsInR5cCI6IkpXVCJ9..."
      )
      @PathVariable String id,
      @RequestHeader("Authorization") String authorizationHeader) {
    try {
      adminService.elevateAdmin(id, authorizationHeader);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    } catch (UnauthorizedException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    return ResponseEntity.ok(true);
  }

  @PostMapping("/login")
  @Operation(
      summary = "Admin login",
      description = "Validates admin credentials and returns a JWT token on success"
  )
  @ApiResponses (value = {
    @ApiResponse(
        responseCode = "200",
        description = "JWT token returned",
        content = @Content(
            schema = @Schema(implementation = String.class)
        )
    ),
    @ApiResponse(
        responseCode = "404",
        description = "No admin found with given username and password",
        content = @Content(
            schema = @Schema(implementation = String.class)
        )
    ),
    @ApiResponse(
        responseCode = "400",
        description = "Invalid admin data",
        content = @Content(
            schema = @Schema(implementation = String.class)
        )
    )
  })
  public ResponseEntity<?> login(
      @RequestBody AdminLoginRegisterRequest adminLogin) {
    try {
      String token = adminService.authenticate(adminLogin.getUsername(), adminLogin.getPassword());
      return ResponseEntity.ok(token);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid admin data");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @PostMapping("/delete/{id}")
  @Operation(
      summary = "Delete an admin user",
      description = "Post request to delete an admin user. "
          + "Requires superuser privileges. "
          + "Returns true if the admin was deleted successfully, false otherwise."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Admin user deleted successfully",
          content = @Content(
              schema = @Schema(implementation = Boolean.class)
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Invalid request"
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Unauthorized"
      )
  })
  public ResponseEntity<?> deleteAdminUser(
      @Parameter(
          name = "Authorization",
          description = "Bearer token in the format `Bearer <JWT>`",
          required = true,
          example = "Bearer eyJhbGciOiJIUzI1N.iIsInR5cCI6IkpXVCJ9..."
      )
      @PathVariable String id,
      @RequestHeader("Authorization") String authorizationHeader) {
    try {
      adminService.exterminateAdmin(id, authorizationHeader);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    } catch (UnauthorizedException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    return ResponseEntity.ok(true);
  }

}
