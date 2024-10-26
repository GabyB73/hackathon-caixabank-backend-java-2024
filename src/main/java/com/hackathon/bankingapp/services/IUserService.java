package com.hackathon.bankingapp.services;

import com.hackathon.bankingapp.DTO.AccountInfoResponseDTO;
import com.hackathon.bankingapp.DTO.UserInfoResponseDTO;
import com.hackathon.bankingapp.DTO.UserRegistrationDTO;
import com.hackathon.bankingapp.entities.User;

import java.util.Optional;
import java.util.UUID;

public interface IUserService {

    User registerUser(UserRegistrationDTO userRegistrationDTO);
    Optional<User> loginUser(String identifier, String password);
    UserInfoResponseDTO getUserInfo(UUID accountNumber);
    AccountInfoResponseDTO getAccountInfo(UUID accountNumber);
    void logoutUser(String token);
}
