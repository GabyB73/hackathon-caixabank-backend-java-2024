package com.hackathon.bankingapp.services;

import com.hackathon.bankingapp.DTO.AccountInfoResponseDTO;
import com.hackathon.bankingapp.DTO.UserInfoResponseDTO;
import com.hackathon.bankingapp.DTO.UserRegistrationDTO;
import com.hackathon.bankingapp.entities.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements IUserService {


    @Override
    public User registerUser(UserRegistrationDTO userRegistrationDTO) {
        return null;
    }

    @Override
    public Optional<User> loginUser(String identifier, String password) {
        return Optional.empty();
    }

    @Override
    public UserInfoResponseDTO getUserInfoFromToken(String token) {
        return null;
    }

    @Override
    public AccountInfoResponseDTO getAccountInfo(String accountNumber) {
        return null;
    }

    @Override
    public void logoutUser(String token) {

    }
}
