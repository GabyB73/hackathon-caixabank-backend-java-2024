package com.hackathon.bankingapp.services;

import com.hackathon.bankingapp.DTO.AccountInfoResponseDTO;
import com.hackathon.bankingapp.DTO.UserInfoResponseDTO;
import com.hackathon.bankingapp.DTO.UserRegistrationDTO;
import com.hackathon.bankingapp.entities.User;
import com.hackathon.bankingapp.repositories.UserRepository;
import com.hackathon.bankingapp.utils.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ITokenBlacklistService tokenBlacklistService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private byte[] toByteArray(UUID accountNumber) {
        return UUIDUtil.toBytes(accountNumber);
    }

    private UUID toUUID(byte[] accountNumberBytes) {
        return UUIDUtil.fromBytes(accountNumberBytes);
    }

    @Override
    public User registerUser(UserRegistrationDTO userRegistrationDTO) {

        // Verificar si el usuario ya existe en la base de datos
        if (userRepository.findByEmail(userRegistrationDTO.getEmail()).isPresent()) {
            throw new RuntimeException("User with email already exists: " + userRegistrationDTO.getEmail());
        }

        // Validar que los campos no estén vacíos
        if (userRegistrationDTO.getName().isEmpty() ||
                userRegistrationDTO.getEmail().isEmpty() ||
                userRegistrationDTO.getPassword().isEmpty() ||
                userRegistrationDTO.getAddress().isEmpty() ||
                userRegistrationDTO.getPhoneNumber().isEmpty()) {
            throw new RuntimeException("Fields cannot be empty");
        }

        // Validar el formato del correo electrónico
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        if (!userRegistrationDTO.getEmail().matches(emailRegex)) {
            throw new RuntimeException("Invalid email format: " + userRegistrationDTO.getEmail());
        }

        // Encriptar la contraseña
        String hashedPassword = passwordEncoder.encode(userRegistrationDTO.getPassword());

        // Crear un nuevo usuario y guardarlo en la base de datos
        User user = new User();
        // Generar un nuevo UUID para el número de cuenta
        UUID accountNumber = UUID.randomUUID();
        user.setAccountNumber(toByteArray(accountNumber)); // Convertir UUID a byte[]
        user.setName(userRegistrationDTO.getName());
        user.setPassword(hashedPassword);
        user.setEmail(userRegistrationDTO.getEmail());
        user.setAddress(userRegistrationDTO.getAddress());
        user.setPhoneNumber(userRegistrationDTO.getPhoneNumber());

        return userRepository.save(user);
    }

    @Override
    public Optional<User> loginUser(String identifier, String password) {

        // Buscar al usuario por correo electrónico
        Optional<User> userOptional = userRepository.findByEmail(identifier);

        // Si no se encuentra por email, intentar por accountNumber si es válido
        if (userOptional.isEmpty()) {
            try {
                UUID accountNumber = UUID.fromString(identifier);
                userOptional = userRepository.findByAccountNumber(toByteArray(accountNumber));
            } catch (IllegalArgumentException e) {
                // El identificador no es UUID válido; se manejará con excepción personalizada luego
                // TODO: Reemplazar por excepciones personalizadas (tarea 7)
                throw new RuntimeException("Invalid identifier format");

            }
        }

        // Verificar existencia y validez de contraseña
        return Optional.of(userOptional.filter(user -> passwordEncoder.matches(password, user.getPassword()))
                // TODO: Reemplazar por excepciones personalizadas (tarea 7)
                .orElseThrow(() -> new RuntimeException("Invalid credentials or user not found")));

    }

    @Override
    public UserInfoResponseDTO getUserInfo(UUID accountNumber) {

        // Buscar el usuario por el número de cuenta
        User user = userRepository.findByAccountNumber(toByteArray(accountNumber))
                // TODO: Reemplazar por excepciones personalizadas (tarea 7)
                .orElseThrow(() -> new RuntimeException("User not found for account number: " + accountNumber));

        // Convertir byte[] a UUID para el DTO de la respuests
        UUID accountNumberUUID = toUUID(user.getAccountNumber()); // Asegúrate de que user.getAccountNumber() sea byte[]

        // Mapear el usuario a DTO de respuesta
        return new UserInfoResponseDTO(
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getAddress(),
                accountNumberUUID);

    }

    @Override
    public AccountInfoResponseDTO getAccountInfo(UUID accountNumber) {
        // Buscar el usuario por el número de cuenta
        User user = userRepository.findByAccountNumber(toByteArray(accountNumber))
                .orElseThrow(() -> new RuntimeException("User not found for account number: " + accountNumber));

        // Supongamos que tienes un método en User que obtiene el saldo
        double balance = user.getBalance();  // Aquí deberías tener un método en la entidad User

        // Retornar la información de la cuenta
        return new AccountInfoResponseDTO(user.getAccountNumber().toString(), balance);

    }

    @Override
    public void logoutUser(String token) {

        // Invalida el token añadiéndolo a la lista negra
        tokenBlacklistService.blacklistToken(token);
        System.out.println("User logged out. Token has been blacklisted: " + token);


    }
}
