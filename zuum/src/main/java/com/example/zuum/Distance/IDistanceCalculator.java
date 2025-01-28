package com.example.zuum.Distance;

import org.springframework.stereotype.Component;

@Component
public interface IDistanceCalculator {
    double calculate(double long1, double lat1, double long2, double lat2);
}
