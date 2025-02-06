package com.example.zuum.User;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.zuum.Ride.RideModel;
import com.example.zuum.Ride.Dto.RideResponseDTO;
import com.example.zuum.User.Dto.UpdateUserDataDTO;
import com.example.zuum.User.Dto.UserResponseDTO;

@RestController
@RequestMapping("/users")
public record UserController(UserService service) {
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getInformations(@PathVariable Integer id) {
        UserModel user = service.getInformations(id);

        return ResponseEntity.ok(UserResponseDTO.create(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateInformations(@PathVariable Integer id, @RequestBody @Validated UpdateUserDataDTO dto) {
        UserModel user = service.updateInformations(id, dto);
        return ResponseEntity.ok(UserResponseDTO.create(user));
    }

    @GetMapping("/{id}/rides")
    public ResponseEntity<Page<RideResponseDTO>> getRides(@PathVariable Integer id,
        @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<RideModel> rides = service.getRides(id, pageable);

        return ResponseEntity.ok(rides.map(RideResponseDTO::create));
    }
}
