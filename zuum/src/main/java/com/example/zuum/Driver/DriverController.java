package com.example.zuum.Driver;

import java.net.URI;

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

}
