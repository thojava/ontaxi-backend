package vn.ontaxi.service;

import com.google.maps.GeoApiContext;
import com.google.maps.RoadsApi;
import com.google.maps.model.LatLng;
import com.google.maps.model.SnappedPoint;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import vn.ontaxi.model.Location;

import java.util.ArrayList;
import java.util.List;

public class SnapToRoadService {
    private static final int PAGE_SIZE_LIMIT = 100;
    private static final int PAGINATION_OVERLAP = 5;

    public static List<Location> getDistance(final List<Location> locations, double accuracy_limit) throws Exception {
        List<LatLng> mCapturedLocations = new ArrayList<>();
        for (Location point : locations) {
            if (point.getAccuracy() < accuracy_limit) {
                mCapturedLocations.add(new LatLng(point.getLatitude(), point.getLongitude()));
            }
        }
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey("AIzaSyA_MkOpSmDjqMi63g-_Wm5ktcLxdMGeQhw")
                .build();
        return snapToRoads(context, mCapturedLocations);
    }

    public static List<Location> parseRoutes(String routes) {
        if (StringUtils.isEmpty(routes)) {
            return new ArrayList<>();
        }
        String paths = routes.replaceAll("\n", "");
        String[] points = paths.split(";");
        List<Location> locations = new ArrayList<>();
        for (String point : points) {
            if(StringUtils.isEmpty(point) || point.contains("null")) continue;
            String[] geo = point.split(",");
            double lat = Double.parseDouble(geo[0]);
            double lng = Double.parseDouble(geo[1]);
            double accuracy = Double.parseDouble(geo[2]);
            Location location = new Location(lat, lng, accuracy);
            locations.add(location);
        }

        return locations;
    }

    private static List<Location> snapToRoads(GeoApiContext context, List<LatLng> mCapturedLocations) throws Exception {
        List<Location> snappedPoints = new ArrayList<>();

        int offset = 0;
        while (offset < mCapturedLocations.size()) {
            // Calculate which points to include in this request. We can't exceed the API's
            // maximum and we want to ensure some overlap so the API can infer a good location for
            // the first few points in each request.
            if (offset > 0) {
                offset -= PAGINATION_OVERLAP;   // Rewind to include some previous points.
            }
            int lowerBound = offset;
            int upperBound = Math.min(offset + PAGE_SIZE_LIMIT, mCapturedLocations.size());

            // Get the data we need for this page.
            LatLng[] page = mCapturedLocations
                    .subList(lowerBound, upperBound)
                    .toArray(new LatLng[upperBound - lowerBound]);

            // Perform the request. Because we have interpolate=true, we will get extra data points
            // between our originally requested path. To ensure we can concatenate these points, we
            // only start adding once we've hit the first new point (that is, skip the overlap).
            SnappedPoint[] points = ObjectUtils.defaultIfNull(RoadsApi.snapToRoads(context, true, page).await(), new SnappedPoint[0]);
            boolean passedOverlap = false;
            for (SnappedPoint point : points) {
                if (offset == 0 || point.originalIndex >= PAGINATION_OVERLAP - 1) {
                    passedOverlap = true;
                }
                if (passedOverlap) {
                    snappedPoints.add(new Location(point.location.lat, point.location.lng, 0));
                }
            }

            offset = upperBound;
        }

        return snappedPoints;
    }

//    public static void main(String[] args) throws Exception {
//        List<SnappedPoint> snappedPoints = getDistance("21.0030275,105.8099614;21.0026083,105.8095512;21.0024776,105.8095507;21.002331,105.8096175;21.0022411,105.8095\n" +
//                "713;21.0021854,105.8094119;21.0021863,105.8092595;21.0021734,105.8090136;21.0021552,105.808825;21.0021318,105.80\n" +
//                "87001;21.0021003,105.8086403;21.0020527,105.8085633;21.0020197,105.8083923;21.0020342,105.8082679;21.0020553,105\n" +
//                ".8081591;21.0020641,105.8080932;21.0020641,105.8080932;21.0020422,105.8080391;21.0020114,105.8077915;21.0019519,\n" +
//                "105.8074517;21.0018638,105.807197;21.0018912,105.8069917;21.0019641,105.8068274;21.0020179,105.8067531;21.002178\n" +
//                "6,105.8067938;21.0022407,105.8067927;21.0024764,105.8068647;21.0027255,105.80693;21.0028985,105.8070405;21.00303\n" +
//                "61,105.8070066;21.0032858,105.8069908;21.0036099,105.8070589;21.0039411,105.8069542;21.0041244,105.8066626;21.00\n" +
//                "43156,105.8063435;21.0045061,105.8060469;21.0047362,105.8056574;21.0049335,105.8052861;21.0050984,105.8050589;21\n" +
//                ".0052034,105.8049021;21.0052033,105.8049083;21.0052033,105.8049083;21.0052033,105.8049083;21.0052033,105.8049083\n" +
//                ";21.0052033,105.8049083;21.0052033,105.8049083;21.0052033,105.8049083;21.0052033,105.8049083;21.0052033,105.8049\n" +
//                "083;21.0052033,105.8049083;21.0052033,105.8049083;21.0052033,105.8049083;21.0052033,105.8049083;21.0052033,105.8\n" +
//                "049083;21.0052033,105.8049083;21.0052033,105.8049083;21.0052033,105.8049083;21.0052033,105.8049083;21.0052033,10\n" +
//                "5.8049083;21.0052072,105.8049068;21.0052732,105.8048225;21.0053069,105.8046691;21.0052578,105.8044207;21.0049861\n" +
//                ",105.8040876;21.0047098,105.8036808;21.0043785,105.803239;21.0040785,105.8028554;21.0038426,105.8024478;21.00355\n" +
//                "69,105.8020777;21.0033194,105.8016648;21.003012,105.8013604;21.0027284,105.8009129;21.0024907,105.8005864;21.002\n" +
//                "3927,105.8002707;21.0021239,105.7998767;21.0018839,105.7994648;21.0016231,105.799263;21.0013644,105.7990714;21.0\n" +
//                "011802,105.7989114;21.0009241,105.798718;21.000666,105.7984493;21.0004081,105.7981438;21.0001698,105.7978373;20.\n" +
//                "9999755,105.7975748;20.999846,105.7973425;20.9997435,105.7971782;20.9997279,105.7971591;20.999728,105.7971591;20\n" +
//                ".999728,105.7971591;20.999728,105.7971591;20.999728,105.7971591;20.999728,105.7971591;20.999728,105.7971591;20.9\n" +
//                "99728,105.7971591;20.999728,105.7971591;20.999728,105.7971591;20.999728,105.7971591;20.999728,105.7971591;20.999\n" +
//                "728,105.7971591;20.999728,105.7971591;20.9997162,105.7971527;20.9996535,105.7971006;20.9994829,105.7968677;20.99\n" +
//                "9252,105.7965091;20.9990768,105.7961955;20.998893,105.7959014;20.9986825,105.7956006;20.9984406,105.7953112;20.9\n" +
//                "982156,105.7949454;20.9979382,105.7945856;20.9976756,105.7941815;20.9973529,105.7937972;20.9970167,105.7932825;2\n" +
//                "0.9967154,105.7928946;20.9964107,105.792424;20.9960787,105.7919517;20.9956906,105.7913917;20.9953975,105.7909689\n" +
//                ";20.9951481,105.7906323;20.9949453,105.7903651;20.9948599,105.790296;20.9948596,105.7902954;20.9948596,105.79029\n" +
//                "54;20.9948596,105.7902954;20.9948596,105.7902954;20.9948596,105.7902954;20.9948596,105.7902954;20.9948596,105.79\n" +
//                "02954;20.9948016,105.7902948;20.9946197,105.7904341;20.994487,105.7906504;20.9942953,105.7909719;20.9940768,105.\n" +
//                "7913513;20.9938929,105.7917112;20.9937145,105.7920727;20.9935935,105.7923521;20.9935076,105.7926663;20.9931974,1\n" +
//                "05.7929878;20.9931011,105.7932318;20.9934021,105.7934265;20.9936189,105.7937047;20.9938041,105.7939771;20.994006\n" +
//                "3,105.7942675;20.9940502,105.7944729;20.9938106,105.7946191;20.9939194,105.7946733;20.9941733,105.7944641;20.994\n" +
//                "4854,105.7942239;20.9948116,105.7939817;20.9950023,105.7938585;20.9950082,105.7938547");
//        System.out.println(snappedPoints);
//    }
}
