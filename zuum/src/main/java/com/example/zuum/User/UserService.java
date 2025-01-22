package com.example.zuum.User;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.zuum.Common.Exception.NotFoundException;
import com.example.zuum.User.Dto.UpdateUserDataDTO;
import com.example.zuum.User.Exception.EmailAlreadyInUseException;

@Service
public class UserService {
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public UserModel updateInformations(Integer id, UpdateUserDataDTO dto) {
        Optional<UserModel> user = repository.findByEmail(dto.getEmail());

        if (user.isPresent() && user.get().getId() != id) {
            throw new EmailAlreadyInUseException();
        }

        user.get().setName(dto.getName());
        user.get().setEmail(dto.getEmail());
        user.get().setCellphone(dto.getCellphone());
        user.get().setBirthday(dto.getBirthday());

        repository.save(user.get());
        
        return user.get();
    }

}
