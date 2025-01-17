package com.example.zuum.Trip;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.zuum.Trip.Dto.NewTripDTO;
import com.example.zuum.Trip.Dto.ResponseTripDTO;

@RestController()
@RequestMapping("/trips")
public record TripController(TripService service) {
    @PostMapping("/request")
    public ResponseEntity<ResponseTripDTO> requestRun(@RequestBody NewTripDTO dto) {
        var newTrip = this.service.requestTrip(dto);
        return ResponseEntity.ok(ResponseTripDTO.createResponseTripDTO(newTrip));
    }
}
