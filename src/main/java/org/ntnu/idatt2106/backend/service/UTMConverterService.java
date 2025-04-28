package org.ntnu.idatt2106.backend.service;

/**
 * Utility service for converting UTM (Universal Transverse Mercator) coordinates to latitude and longitude.
 *
 * @author Erlend Eide Zindel
 * @since 1.0
 */
public class UTMConverterService {

    private static final double WGS84_A = 6378137.0; // radius
    private static final double WGS84_ECCSQ = 0.00669438; // eccentricity squared

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private UTMConverterService() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Converts UTM coordinates to latitude and longitude.
     *
     * @param easting The easting value of the UTM coordinate.
     * @param northing The northing value of the UTM coordinate.
     * @param zone The UTM zone number.
     * @return A double array where the first element is latitude and the second is longitude.
     */
    public static double[] utmToLatLon(double easting, double northing, int zone) {
        double e1sq = WGS84_ECCSQ / (1 - WGS84_ECCSQ);
        double n = WGS84_A / Math.sqrt(1 - WGS84_ECCSQ * Math.pow(Math.sin(0), 2));
        double r = n * (1 - WGS84_ECCSQ) / (1 - WGS84_ECCSQ * Math.pow(Math.sin(0), 2));

        double k0 = 0.9996;
        double e = Math.sqrt(WGS84_ECCSQ);

        double arc = northing / k0;
        double mu = arc / (WGS84_A * (1 - Math.pow(e, 2) / 4.0 - 3 * Math.pow(e, 4) / 64.0 - 5 * Math.pow(e, 6) / 256.0));

        double ei = (1 - Math.pow((1 - e * e), 0.5)) / (1 + Math.pow((1 - e * e), 0.5));

        double ca = 3 * ei / 2 - 27 * Math.pow(ei, 3) / 32.0;
        double cb = 21 * Math.pow(ei, 2) / 16 - 55 * Math.pow(ei, 4) / 32;
        double cc = 151 * Math.pow(ei, 3) / 96;
        double cd = 1097 * Math.pow(ei, 4) / 512;
        double phi1 = mu + ca * Math.sin(2 * mu) + cb * Math.sin(4 * mu) + cc * Math.sin(6 * mu) + cd * Math.sin(8 * mu);

        double n0 = WGS84_A / Math.sqrt(1 - Math.pow(e * Math.sin(phi1), 2));
        double r0 = WGS84_A * (1 - e * e) / Math.pow(1 - Math.pow(e * Math.sin(phi1), 2), 1.5);
        double fact1 = n0 * Math.tan(phi1) / r0;

        double _a1 = 500000 - easting;
        double dd0 = _a1 / (n0 * k0);
        double fact2 = dd0 * dd0 / 2;

        double t0 = Math.pow(Math.tan(phi1), 2);
        double Q0 = e1sq * Math.pow(Math.cos(phi1), 2);
        double fact3 = (5 + 3 * t0 + 10 * Q0 - 4 * Q0 * Q0 - 9 * e1sq) * Math.pow(dd0, 4) / 24;

        double fact4 = (61 + 90 * t0 + 298 * Q0 + 45 * Math.pow(t0, 2) - 252 * e1sq - 3 * Math.pow(Q0, 2)) * Math.pow(dd0, 6) / 720;

        double lof1 = dd0;
        double lof2 = (1 + 2 * t0 + Q0) * Math.pow(dd0, 3) / 6.0;
        double lof3 = (5 - 2 * Q0 + 28 * t0 - 3 * Math.pow(Q0, 2) + 8 * e1sq + 24 * Math.pow(t0, 2)) * Math.pow(dd0, 5) / 120;
        double _a2 = (lof1 - lof2 + lof3) / Math.cos(phi1);
        double _a3 = _a2 * 180 / Math.PI;

        double latitude = 180 * (phi1 - fact1 * (fact2 + fact3 + fact4)) / Math.PI;

        double longitude = ((zone > 0) ? 6 * zone - 183 : 3) - _a3;

        return new double[]{latitude, longitude};
    }
}
