package vn.ontaxi.service;

import com.google.gson.Gson;
import org.apache.commons.lang3.CharEncoding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import vn.ontaxi.component.ConfigurationComponent;
import vn.ontaxi.json.model.google.dm.DistanceMatrix;
import vn.ontaxi.model.Location;
import vn.ontaxi.model.direction.GoogleDirections;
import vn.ontaxi.utils.HttpUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

@Service
public class DistanceMatrixService {
    private static final String DISTANCE_MATRIX_BASE_URL = "https://maps.googleapis.com/maps/api/distancematrix/%s?units=%s&origins=%s&destinations=%s&key=%s";
    private static final String DIRECTION_API_BASE_URL = "https://maps.googleapis.com/maps/api/directions/%s?units=%s&origin=%s&destination=%s&key=%s";

    private static final String OUTPUT_FORMAT = "json";
    private static final String UNIT = "metric";
    private final ConfigurationComponent configurationComponent;
    private final Environment environment;

    @Autowired
    public DistanceMatrixService(ConfigurationComponent configurationComponent, Environment environment) {
        this.configurationComponent = configurationComponent;
        this.environment = environment;
    }

    /* Return distance by m */
    public static double getDistance(final String origins, final String destinations) {
        String encodedOrigin, encodedDest;
        try {
            encodedOrigin = URLEncoder.encode(origins, CharEncoding.UTF_8);
            encodedDest = URLEncoder.encode(destinations, CharEncoding.UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        String directionAPIUrl = String.format(DISTANCE_MATRIX_BASE_URL, OUTPUT_FORMAT, UNIT, encodedOrigin, encodedDest, "AIzaSyC983JhXlUWUomd07jsfBkPjv7VpHA_eWQ");
        DistanceMatrix distanceMatrix = new Gson().fromJson(HttpUtils.readTextFromURL(directionAPIUrl), DistanceMatrix.class);
        if (distanceMatrix.getStatus().equalsIgnoreCase("OK")) {
            try {
                return (double) distanceMatrix.getRows().get(0).getElements().get(0).getDistance().getValue();
            } catch (NullPointerException exp) {
                exp.printStackTrace();
                return -1;
            }
        }

        return -1;
    }

    public static GoogleDirections getGoogleDirection(final String origins, final String destinations, final String apiKey) {
        String encodedOrigin, encodedDest;
        try {
            encodedOrigin = URLEncoder.encode(origins, CharEncoding.UTF_8);
            encodedDest = URLEncoder.encode(destinations, CharEncoding.UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        String directionAPIUrl = String.format(DIRECTION_API_BASE_URL, OUTPUT_FORMAT, UNIT, encodedOrigin, encodedDest, apiKey);
        return new Gson().fromJson(HttpUtils.readTextFromURL(directionAPIUrl), GoogleDirections.class);
    }

    public double getDistanceFromRoutes(String routes, boolean with_snap) throws Exception {
        List<Location> locations = SnapToRoadService.parseRoutes(routes);
        if(with_snap) {
            locations = SnapToRoadService.getDistance(locations, configurationComponent.getAccuracy_limit());
        }
        double previousLat = 0;
        double previousLng = 0;
        double totalDistance = 0;
        for (Location snappedPoint : locations) {
            double latitude = snappedPoint.getLatitude();
            double longitude = snappedPoint.getLongitude();
            if(previousLat > 0 && previousLng > 0) {
                float[] results = new float[1];
                computeDistanceAndBearing(previousLat, previousLng, latitude, longitude, results);
                double distance = results[0];
                if(distance > 2000) {
                    distance = getDistance(previousLat + "," + previousLng, latitude + "," + longitude);
                }
                totalDistance += distance;
            }
            previousLat = latitude;
            previousLng = longitude;
        }
        return totalDistance;
    }

//    public static void main(String[] args) throws Exception {
//        String routes = "21.0511977,105.7648838;21.0512269,105.764815;21.0503686,105.7636067;21.0479977,105.7634051;21.0465221,105.7633\n" +
//                "248;21.0457178,105.762565;21.0442621,105.7617722;21.0434768,105.7617659;21.042531,105.759533;21.0420876,105.7604\n" +
//                "133;21.041176,105.762511;21.0403611,105.7650213;21.0394199,105.7668325;21.0388981,105.7697264;21.0381658,105.773\n" +
//                "1888;21.0369629,105.7766957;21.0364153,105.7789607;21.0345619,105.7799672;21.0318379,105.7797173;21.0266923,105.\n" +
//                "7788489;21.0211725,105.7794663;21.0158918,105.7835306;21.0093241,105.788911;21.0038574,105.793476;20.9997279,105\n" +
//                ".796603;20.9972807,105.798772;20.9931707,105.8020332;20.9853439,105.8078067;20.9805115,105.8113132;20.9766748,10\n" +
//                "5.8141368;20.9756972,105.814885;20.9745968,105.815659;20.9740734,105.8163478;20.9735457,105.8171233;20.9741744,1\n" +
//                "05.8162459;20.9738839,105.8166733;20.972054,105.8203328;20.9712109,105.8234394;20.969764,105.8276162;20.9670235,\n" +
//                "105.8326462;20.9660207,105.8377204;20.9660207,105.8377204;20.9664385,105.8419409;20.9634048,105.8471792;20.95961\n" +
//                "16,105.8493161;20.9589672,105.8480855;20.9521909,105.8533288;20.9478775,105.8563617;20.9384176,105.8591781;20.93\n" +
//                "15647,105.859126;20.924904,105.8598288;20.9207359,105.8598294;20.9203724,105.8598098;20.8561834,105.8820099;20.8\n" +
//                "504678,105.8841771;20.8446057,105.8858682;20.8375791,105.8868534;20.5960759,105.8871228";
//        String[] locations = routes.replaceAll("\n", "").split(";");
//        double currentLat = 0, currentLon = 0;
//        double totalDistance = 0;
//        for (String location : locations) {
//            double lat = Double.parseDouble(location.split(",")[0]);
//            double lon = Double.parseDouble(location.split(",")[1]);
//            if(currentLat > 0 && currentLon > 0) {
//                float[] results = new float[1];
//                computeDistanceAndBearing(currentLat, currentLon, lat, lon, results);
//                double distance = results[0];
//                if(distance > 2000) {
//                    distance = getDistance(currentLat + "," + currentLon, lat + "," + lon);
//                }
//                totalDistance += distance;
//            }
//            currentLat = lat;
//            currentLon = lon;
//        }
//        System.err.println("Total " + totalDistance);
//
//        totalDistance = getDistanceFromRoutes(routes);
//        System.err.println("Total " + totalDistance);
//    }

    public static void computeDistanceAndBearing(double lat1, double lon1,
                                                  double lat2, double lon2, float[] results) {
        // Based on http://www.ngs.noaa.gov/PUBS_LIB/inverse.pdf
        // using the "Inverse Formula" (section 4)

        int MAXITERS = 20;
        // Convert lat/long to radians
        lat1 *= Math.PI / 180.0;
        lat2 *= Math.PI / 180.0;
        lon1 *= Math.PI / 180.0;
        lon2 *= Math.PI / 180.0;

        double a = 6378137.0; // WGS84 major axis
        double b = 6356752.3142; // WGS84 semi-major axis
        double f = (a - b) / a;
        double aSqMinusBSqOverBSq = (a * a - b * b) / (b * b);

        double L = lon2 - lon1;
        double A = 0.0;
        double U1 = Math.atan((1.0 - f) * Math.tan(lat1));
        double U2 = Math.atan((1.0 - f) * Math.tan(lat2));

        double cosU1 = Math.cos(U1);
        double cosU2 = Math.cos(U2);
        double sinU1 = Math.sin(U1);
        double sinU2 = Math.sin(U2);
        double cosU1cosU2 = cosU1 * cosU2;
        double sinU1sinU2 = sinU1 * sinU2;

        double sigma = 0.0;
        double deltaSigma = 0.0;
        double cosSqAlpha = 0.0;
        double cos2SM = 0.0;
        double cosSigma = 0.0;
        double sinSigma = 0.0;
        double cosLambda = 0.0;
        double sinLambda = 0.0;

        double lambda = L; // initial guess
        for (int iter = 0; iter < MAXITERS; iter++) {
            double lambdaOrig = lambda;
            cosLambda = Math.cos(lambda);
            sinLambda = Math.sin(lambda);
            double t1 = cosU2 * sinLambda;
            double t2 = cosU1 * sinU2 - sinU1 * cosU2 * cosLambda;
            double sinSqSigma = t1 * t1 + t2 * t2; // (14)
            sinSigma = Math.sqrt(sinSqSigma);
            cosSigma = sinU1sinU2 + cosU1cosU2 * cosLambda; // (15)
            sigma = Math.atan2(sinSigma, cosSigma); // (16)
            double sinAlpha = (sinSigma == 0) ? 0.0 :
                    cosU1cosU2 * sinLambda / sinSigma; // (17)
            cosSqAlpha = 1.0 - sinAlpha * sinAlpha;
            cos2SM = (cosSqAlpha == 0) ? 0.0 :
                    cosSigma - 2.0 * sinU1sinU2 / cosSqAlpha; // (18)

            double uSquared = cosSqAlpha * aSqMinusBSqOverBSq; // defn
            A = 1 + (uSquared / 16384.0) * // (3)
                    (4096.0 + uSquared *
                            (-768 + uSquared * (320.0 - 175.0 * uSquared)));
            double B = (uSquared / 1024.0) * // (4)
                    (256.0 + uSquared *
                            (-128.0 + uSquared * (74.0 - 47.0 * uSquared)));
            double C = (f / 16.0) *
                    cosSqAlpha *
                    (4.0 + f * (4.0 - 3.0 * cosSqAlpha)); // (10)
            double cos2SMSq = cos2SM * cos2SM;
            deltaSigma = B * sinSigma * // (6)
                    (cos2SM + (B / 4.0) *
                            (cosSigma * (-1.0 + 2.0 * cos2SMSq) -
                                    (B / 6.0) * cos2SM *
                                            (-3.0 + 4.0 * sinSigma * sinSigma) *
                                            (-3.0 + 4.0 * cos2SMSq)));

            lambda = L +
                    (1.0 - C) * f * sinAlpha *
                            (sigma + C * sinSigma *
                                    (cos2SM + C * cosSigma *
                                            (-1.0 + 2.0 * cos2SM * cos2SM))); // (11)

            double delta = (lambda - lambdaOrig) / lambda;
            if (Math.abs(delta) < 1.0e-12) {
                break;
            }
        }

        float distance = (float) (b * A * (sigma - deltaSigma));
        results[0] = distance;
        if (results.length > 1) {
            float initialBearing = (float) Math.atan2(cosU2 * sinLambda,
                    cosU1 * sinU2 - sinU1 * cosU2 * cosLambda);
            initialBearing *= 180.0 / Math.PI;
            results[1] = initialBearing;
            if (results.length > 2) {
                float finalBearing = (float) Math.atan2(cosU1 * sinLambda,
                        -sinU1 * cosU2 + cosU1 * sinU2 * cosLambda);
                finalBearing *= 180.0 / Math.PI;
                results[2] = finalBearing;
            }
        }
    }
}
