package com.example.zuum.Config.Swagger;

import org.locationtech.jts.geom.Point;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Point", description = "Geographic location")
public class PointSchema {
    
    @Schema(description = "X (longitude)", example = "-52.83628526180858")
    private double x;

    @Schema(description = "Y (latitude)", example = "-28.73641961515877")
    private double y;

    public PointSchema(Point point) {
        this.x = point.getX();
        this.y = point.getY();
    }

    public double getX() { return x; }
    public double getY() { return y; }

}
