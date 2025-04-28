package org.ntnu.idatt2106.backend.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

public class UTMConverterServiceTest {

    @Test
    @DisplayName("Should correctly convert UTM coordinates to latitude and longitude")
    void testUtmToLatLonConversion() {
        // Oslo sentrum omtrent (Easting, Northing, Zone 33)
        double easting = 597000;
        double northing = 6640000;
        int zone = 33;

        double[] latlon = UTMConverterService.utmToLatLon(easting, northing, zone);

        // Assert latitude and longitude are within reasonable range
        assertNotNull(latlon);
        assertEquals(2, latlon.length);

        double latitude = latlon[0];
        double longitude = latlon[1];

        assertTrue(latitude > 50 && latitude < 70, "Latitude should be in Norway range");
        assertTrue(longitude > 5 && longitude < 30, "Longitude should be in Norway range");
    }

    @Test
    @DisplayName("Should return latitude and longitude within valid earth bounds")
    void testLatLonWithinBounds() {
        double easting = 500000;
        double northing = 4649776.22482;
        int zone = 33;

        double[] latlon = UTMConverterService.utmToLatLon(easting, northing, zone);

        double latitude = latlon[0];
        double longitude = latlon[1];

        assertTrue(latitude >= -90 && latitude <= 90, "Latitude must be between -90 and 90");
        assertTrue(longitude >= -180 && longitude <= 180, "Longitude must be between -180 and 180");
    }

    @Test
    @DisplayName("Should handle UTM zone 0 gracefully (fallback)")
    void testZoneZeroFallback() {
        double easting = 500000;
        double northing = 4649776.22482;
        int zone = 0;

        double[] latlon = UTMConverterService.utmToLatLon(easting, northing, zone);

        double latitude = latlon[0];
        double longitude = latlon[1];

        assertTrue(latitude >= -90 && latitude <= 90, "Latitude must be valid even with zone 0");
        assertTrue(longitude >= -180 && longitude <= 180, "Longitude must be valid even with zone 0");
    }

    @Test
    @DisplayName("Should not allow instantiation of UTMConverterService")
    void testPrivateConstructor() throws Exception {
        var constructor = UTMConverterService.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        InvocationTargetException thrown = assertThrows(InvocationTargetException.class, () -> {
            constructor.newInstance();
        });

        assertTrue(thrown.getCause() instanceof UnsupportedOperationException);
    }

}
