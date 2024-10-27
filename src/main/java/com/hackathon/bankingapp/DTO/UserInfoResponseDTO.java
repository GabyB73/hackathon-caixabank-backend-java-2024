package com.hackathon.bankingapp.DTO;

import com.hackathon.bankingapp.entities.User;
import com.hackathon.bankingapp.utils.UUIDUtil;
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
// Método estático para convertir desde byte[] a UUID
    public static UserInfoResponseDTO fromUser(User user) {
        UUID accountNumber = UUIDUtil.fromBytes(user.getAccountNumber()); // Convierte byte[] a UUID
        return new UserInfoResponseDTO(
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getAddress(),
                accountNumber
        );
    }

}
