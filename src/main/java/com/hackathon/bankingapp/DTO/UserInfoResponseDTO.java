package com.hackathon.bankingapp.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponseDTO {

    private String name;
    private String email;
    private String phoneNumber;
    private String address;
    private UUID accountNumber;
    // No incluir el hashedPassword aquí


}
