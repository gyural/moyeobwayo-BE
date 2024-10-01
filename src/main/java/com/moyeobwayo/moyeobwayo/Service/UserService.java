package com.moyeobwayo.moyeobwayo.Service;

import com.moyeobwayo.moyeobwayo.Domain.UserEntity;
import org.springframework.stereotype.Service;
import com.moyeobwayo.moyeobwayo.Repository.UserEntityRepository;


@Service
public class UserService {

    private final UserEntityRepository userRepository;

    public UserService(UserEntityRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserEntity login(String userName, String password) {
        return userRepository.findByUserNameAndPassword(userName, password);
    }
}
