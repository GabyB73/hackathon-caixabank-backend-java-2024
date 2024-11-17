package com.hackathon.bankingapp.DTO;

import com.hackathon.bankingapp.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponseDTO {

    private String name;
    private String email;
    private String phoneNumber;
    private String address;
    private String accountNumber;
    // No incluir el hashedPassword aquí

// Método estático para convertir desde User a UserInfoResponseDTO
    public static UserInfoResponseDTO fromUser(User user) {

        return new UserInfoResponseDTO(
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getAddress(),
                user.getAccountNumber()
        );
    }

}
