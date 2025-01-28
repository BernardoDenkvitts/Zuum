package com.example.zuum.Distance;

import org.springframework.stereotype.Component;

@Component
public class HaversineDistance implements IDistanceCalculator {
    private static final double EARTH_RADIUS_KM = 6371.0;

    // Calculate the distance between two points on Earth using Haversine formula
    @Override
    public double calculate(double long1, double lat1, double long2, double lat2) {
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(long1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(long2);

        double dLat = lat2Rad - lat1Rad;
        double dLon = lon2Rad - lon1Rad;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad) * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

}
