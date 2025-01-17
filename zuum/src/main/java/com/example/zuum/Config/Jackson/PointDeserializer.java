package com.example.zuum.Config.Jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import java.io.IOException;

public class PointDeserializer extends JsonDeserializer<Point> {

    private final GeometryFactory geometryFactory = new GeometryFactory();

    @Override
    public Point deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        double x = node.get("x").asDouble();
        double y = node.get("y").asDouble();
        return geometryFactory.createPoint(new Coordinate(x, y));
    }
}
