package com.example.zuum;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.zuum.Distance.HaversineDistance;
import com.example.zuum.Distance.IDistanceCalculator;
import com.example.zuum.Ride.Dto.PriceRequestDTO;

@SpringBootTest
public class DistanceCalculatorTest {

  IDistanceCalculator distanceCalculator = new HaversineDistance();

  @Test
  public void testDistance_whenPointsAreValid_thenReturnCorrectDistance() {

    GeometryFactory geometryFactory = new GeometryFactory();

    Coordinate originCoordinate = new Coordinate(-52.0, -28.0);
    Point origin = geometryFactory.createPoint(originCoordinate);

    Coordinate destinyCoordinate = new Coordinate(-52.0, -27.946);
    Point destiny = geometryFactory.createPoint(destinyCoordinate);

    PriceRequestDTO requestDTO = new PriceRequestDTO(1, origin, destiny);

    double distance = distanceCalculator.calculate(requestDTO.getOrigin().getX(), requestDTO.getOrigin().getY(),
        requestDTO.getDestiny().getX(), requestDTO.getDestiny().getY());

    assertEquals(5.98422, distance, 0.5);

  }

}
