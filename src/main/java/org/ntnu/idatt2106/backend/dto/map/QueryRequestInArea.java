package org.ntnu.idatt2106.backend.dto.map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Data transfer object for a query request in a specific area.
 * This class is used to transfer query request data between the client and server.
 *
 * @since 0.2
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QueryRequestInArea {

  @Schema(description = "List of coordinates representing the area",
      example = "[{\"latitude\": 60.123456, \"longitude\": 10.123456}, " +
          "{\"latitude\": 60.654321, \"longitude\": 10.654321}]")
  private List<CoordinatesDTO> mapArea;
  @Schema(description = "List of IDs to exclude from the query", example = "[1, 2, 3]")
  private List<Long> excludedIds = new ArrayList<>();

}
