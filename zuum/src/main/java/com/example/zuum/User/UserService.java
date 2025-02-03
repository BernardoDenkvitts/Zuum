package com.example.zuum.User;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.zuum.Common.Exception.NotFoundException;
import com.example.zuum.Driver.DriverRepository;
import com.example.zuum.Ride.RideModel;
import com.example.zuum.Ride.RideRepository;
import com.example.zuum.User.Dto.UpdateUserDataDTO;
import com.example.zuum.User.Exception.EmailAlreadyInUseException;
import com.example.zuum.User.Exception.MissingDriverProfileException;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final DriverRepository driverRepository;
    private final RideRepository rideRepository;

    public UserService(UserRepository repository, DriverRepository driverRepository, RideRepository rideRepository) {
        this.userRepository = repository;
        this.driverRepository = driverRepository;
        this.rideRepository = rideRepository;
    }

    @Transactional
    public UserModel updateInformations(Integer id, UpdateUserDataDTO dto) {
        UserModel user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User with id " + id));
        
        if (dto.getEmail() != user.getEmail()){
            Optional<UserModel> checkEmail = userRepository.findByEmail(dto.getEmail());
            if (checkEmail.isPresent() && checkEmail.get().getId() != id) {
                throw new EmailAlreadyInUseException();
            }
        }

        // If user wants to change his Status to DRIVER
        // First he must exists in driver repository
        if (dto.getUserType().equals(UserType.DRIVER)) {
            if (!driverRepository.findByUserId(id).isPresent()) {
                throw new MissingDriverProfileException();
            }
        }

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setUserType(dto.getUserType());
        user.setCellphone(dto.getCellphone());
        user.setBirthday(dto.getBirthday());

        userRepository.save(user);
        
        return user;
    }

    public Page<RideModel> getRides(Integer userId, Pageable pageable) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id " + userId));
  
        return rideRepository.findRides(userId, null, pageable);
    }

}
