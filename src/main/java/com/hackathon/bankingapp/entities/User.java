package com.hackathon.bankingapp.entities;

import com.hackathon.bankingapp.utils.UUIDUtil;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @Column(name = "account_number", columnDefinition = "BINARY(16)")
    private byte[] accountNumber;

    @NotBlank
    private String name; // Full name of the user

    @NotBlank
    @Email // Asegura que el email tenga un formato válido
    private String email;

    @NotBlank
    @Size(min = 8) // La contraseña debe tener al menos 8 caracteres
    private String password; // Almacenar la contraseña en formato hash

    @NotBlank
    private String address;

    @NotBlank
    private String phoneNumber;

    // Constructor que acepta un UUID
    public User(UUID accountNumber) {
        this.accountNumber = UUIDUtil.toBytes(accountNumber); // Convertir UUID a byte[]
    }

    // Método para obtener el UUID del número de cuenta
    public UUID getAccountNumberAsUUID() {
        return UUIDUtil.fromBytes(accountNumber); // Convertir byte[] a UUID
    }

    // toString, equals y hashCode si es necesario

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(accountNumber, user.accountNumber) &&
                Objects.equals(name, user.name) &&
                Objects.equals(email, user.email) &&
                Objects.equals(address, user.address) &&
                Objects.equals(phoneNumber, user.phoneNumber);

    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber, name, email, address, phoneNumber);
    }

    @Override
    public String toString() {
        return "User{" +
                "accountNumber=" + accountNumber +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }

    public double getBalance() {
        // Aquí deberías tener un método que obtenga el saldo del usuario
        return 0.0; // Por ahora retornamos 0.0
    }
}
