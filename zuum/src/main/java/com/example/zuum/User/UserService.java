package com.example.zuum.User;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.zuum.Common.Exception.NotFoundException;
import com.example.zuum.Driver.DriverRepository;
import com.example.zuum.User.Dto.UpdateUserDataDTO;
import com.example.zuum.User.Exception.EmailAlreadyInUseException;
import com.example.zuum.User.Exception.MissingDriverProfileException;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final DriverRepository driverRepository;

    public UserService(UserRepository repository, DriverRepository driverRepository) {
        this.userRepository = repository;
        this.driverRepository = driverRepository;
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

}
