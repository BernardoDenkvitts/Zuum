package com.example.zuum.Ride;

import java.math.BigDecimal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.zuum.Ride.Dto.NewRideDTO;
import com.example.zuum.Ride.Dto.NewRideResponseDTO;
import com.example.zuum.Ride.Dto.RidePriceResponseDTO;

@RestController()
@RequestMapping("/trips")
public record RideController(RideService service) {
    @PostMapping("/request")
    public ResponseEntity<NewRideResponseDTO> requestTrip(@RequestBody NewRideDTO dto) {
        var newTrip = this.service.requestRide(dto);
        return ResponseEntity.ok(NewRideResponseDTO.create(newTrip));
    }

    // TODO incluir Drools

    @GetMapping("/price")
    public ResponseEntity<RidePriceResponseDTO> returnTripPrice(@RequestParam("id") Integer userId,
            @RequestParam("orgLat") double orgLat, @RequestParam("orgLng") double orgLng,
            @RequestParam("desLat") double desLat, @RequestParam("desLng") double desLng) {
        return ResponseEntity.ok(new RidePriceResponseDTO(BigDecimal.valueOf(15.50)));
    };
}
