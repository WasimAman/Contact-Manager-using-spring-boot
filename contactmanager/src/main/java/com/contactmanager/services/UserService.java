package com.contactmanager.services;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contactmanager.entities.UserData;
import com.contactmanager.repositories.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserData saveUser(UserData user) {
        Optional<UserData> presentUser = userRepository.findByEmail(user.getEmail());
        if (presentUser.isPresent()) {
            return null;
        }
        return userRepository.save(user);
    }

    public UserData getUser(String username) {
        return userRepository.findByEmail(username).get();
    }

    public UserData getUserById(int userId){
        return userRepository.findById(userId).get();
    }

    public void updateUser(UserData user){
        userRepository.save(user);
    }
}