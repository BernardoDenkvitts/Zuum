package com.example.zuum.Ride;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.zuum.Ride.Dto.NewRideDTO;
import com.example.zuum.Ride.Dto.PriceRequestDTO;
import com.example.zuum.Ride.Dto.RidePriceResponseDTO;
import com.example.zuum.Ride.Dto.RideResponseDTO;
import com.example.zuum.Ride.Dto.RideRequestNotificationDTO;

@RestController
@RequestMapping("/rides")
public record RideController(RideService service) {

    @GetMapping("/price")
    public ResponseEntity<RidePriceResponseDTO> requestRidePrice(@RequestBody @Validated PriceRequestDTO dto) {
        return ResponseEntity.ok(
            new RidePriceResponseDTO(service.calculateRidePrice(dto).getPrice())
        );
    }

    @PostMapping("/request")
    public ResponseEntity<RideResponseDTO> requestRide(@RequestBody @Validated NewRideDTO dto) {
        var newTrip = this.service.requestRide(dto);

        return ResponseEntity.ok(RideResponseDTO.create(newTrip));
    }

    @GetMapping("/recent")
    public ResponseEntity<List<RideRequestNotificationDTO>> getRecentPendingRideRequests(
            @RequestParam("driverId") Integer driverId, @RequestParam("longt") double longt,
            @RequestParam("lat") double lat,
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        Page<RideModel> rides = service.getRecentPendingRidesByLocation(driverId, longt, lat, pageable);
        List<RideRequestNotificationDTO> responseDTO = rides.stream().map(RideRequestNotificationDTO::create)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDTO);
    }

    @PatchMapping("/{rideId}")
    public ResponseEntity<RideResponseDTO> updateStatus(@PathVariable Integer rideId, @RequestParam Integer driverId,
            @RequestParam("status") RideStatus newStatus) {
        RideModel ride = service.updateRideStatus(rideId, driverId, newStatus);

        return ResponseEntity.ok(RideResponseDTO.create(ride));
    }

    @GetMapping("/{rideId}")
    public ResponseEntity<RideResponseDTO> getRideInformation(@PathVariable Integer rideId, @RequestParam(required = false) Integer driverId, @RequestParam(required = false) Integer passangerId) {
        RideModel ride = service.getData(rideId, driverId, passangerId);

        return ResponseEntity.ok(RideResponseDTO.create(ride));
    }

}
