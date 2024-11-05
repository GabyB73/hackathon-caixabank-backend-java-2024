package com.hackathon.bankingapp.controllers;

import com.hackathon.bankingapp.DTO.AccountInfoResponseDTO;
import com.hackathon.bankingapp.DTO.LoginRequestDTO;
import com.hackathon.bankingapp.DTO.UserInfoResponseDTO;
import com.hackathon.bankingapp.DTO.UserRegistrationDTO;
import com.hackathon.bankingapp.entities.User;
import com.hackathon.bankingapp.services.UserServiceImpl;
import com.hackathon.bankingapp.utils.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private TokenUtil tokenUtil;

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody UserRegistrationDTO userRegistrationDTO) {
        User newUser = userService.registerUser(userRegistrationDTO);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginRequestDTO loginRequest) {
        Optional<User> userOptional = userService.loginUser(loginRequest.getIdentifier(), loginRequest.getPassword());
        String token = tokenUtil.generateToken(userOptional.get()); // Asume que tienes un método para generar el JWT
        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    @GetMapping("/info")
    public ResponseEntity<UserInfoResponseDTO> getUserInfo(@RequestHeader("Authorization") String token) {
        UUID accountNumber = tokenUtil.extractAccountNumberFromToken(token); // Método que extrae el número de cuenta del token
        UserInfoResponseDTO userInfo = userService.getUserInfoFromToken(token);
        return new ResponseEntity<>(userInfo, HttpStatus.OK);
    }

    @GetMapping("/account-info")
    public ResponseEntity<AccountInfoResponseDTO> getAccountInfo(@RequestHeader("Authorization") String token) {
        UUID accountNumber = tokenUtil.extractAccountNumberFromToken(token); // Método que extrae el número de cuenta del token
        AccountInfoResponseDTO accountInfo = userService.getAccountInfo(accountNumber);
        return new ResponseEntity<>(accountInfo, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(@RequestHeader("Authorization") String token) {
        userService.logoutUser(token);
        return new ResponseEntity<>("User logged out successfully", HttpStatus.OK);
    }



}
