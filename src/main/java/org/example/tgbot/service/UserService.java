package org.example.tgbot.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.example.tgbot.model.UserModel;
import org.example.tgbot.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@Transactional
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public void addUser(Long id, double rent){
        UserModel userModel = new UserModel();
        userModel.setChatId(id);
        userModel.setRentUsd(rent);
        userRepository.save(userModel);
        log.info("user {} added successfully", id);
    }

    public boolean isUserExists(Long id){
        return userRepository.existsById(id);
    }

    public void updateUser(Long id, double rent){
        UserModel userModel = userRepository.findById(id).orElseThrow(() -> new RuntimeException("user not found"));
        userModel.setRentUsd(rent);
    }

    public double getPrice(Long id){
        UserModel userModel = userRepository.findById(id).orElseThrow(() -> new RuntimeException("user not found"));
        return userModel.getRentUsd();
    }
}
