package com.example.zuum.Driver;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.zuum.Driver.Dto.DriverResponseDTO;
import com.example.zuum.Driver.Dto.NewDriverDTO;
import com.example.zuum.Driver.Dto.UpdateDriverDataDTO;
import com.example.zuum.Ride.RideModel;
import com.example.zuum.Ride.Dto.RideResponseDTO;

@RestController
@RequestMapping("/drivers")
public record DriverController(DriverService service) {
    @PostMapping
    public ResponseEntity<DriverResponseDTO> register(@RequestBody @Validated NewDriverDTO dto) {
        DriverModel newDriver = service.createDriver(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").buildAndExpand(newDriver.getId()).toUri();
        
        return ResponseEntity.created(location).body(DriverResponseDTO.create(newDriver));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DriverResponseDTO> updateInformations(@PathVariable Integer id, @RequestBody @Validated UpdateDriverDataDTO dto) {
        DriverModel driver = service.updateInformations(id, dto);

        return ResponseEntity.ok(DriverResponseDTO.create(driver));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverResponseDTO> getInformations(@PathVariable Integer id) {
        DriverModel driver = service.getInformations(id);

        return ResponseEntity.ok(DriverResponseDTO.create(driver));
    }

    @GetMapping("/{id}/rides")
    public ResponseEntity<Page<RideResponseDTO>> getDriverRides(
        @PathVariable("id") Integer id, @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<RideModel> rides = service.getRides(id, pageable);

        return ResponseEntity.ok(rides.map(RideResponseDTO::create));
    }

}
