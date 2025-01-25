package com.example.zuum.Ride;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.zuum.Ride.Dto.NewRideDTO;
import com.example.zuum.Ride.Dto.RideAcceptedDTO;
import com.example.zuum.Ride.Dto.RideResponseDTO;
import com.example.zuum.Ride.Dto.RideRequestNotificationDTO;

@RestController
@RequestMapping("/rides")
public record RideController(RideService service) {
    @PostMapping("/request")
    public ResponseEntity<RideResponseDTO> requestTrip(@RequestBody NewRideDTO dto) {
        var newTrip = this.service.requestRide(dto);

        return ResponseEntity.ok(RideResponseDTO.create(newTrip));
    }

    @GetMapping("/recent")
    public ResponseEntity<List<RideRequestNotificationDTO>> getRecentPendingRideRequests(
            @RequestParam("driverId") Integer driverId, @RequestParam("lat") double lat,
            @RequestParam("longt") double longt,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        Page<RideModel> rides = service.getRecentPendingRidesByLocation(driverId, lat, longt, pageable);
        List<RideRequestNotificationDTO> responseDTO = rides.stream().map(RideRequestNotificationDTO::create)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/{rideId}/accept")
    public ResponseEntity<RideAcceptedDTO> acceptRide(@PathVariable Integer rideId, @RequestParam Integer driverId) {
        RideModel ride = service.acceptRide(rideId, driverId);

        return ResponseEntity.ok(RideAcceptedDTO.create(ride));
    }

}
