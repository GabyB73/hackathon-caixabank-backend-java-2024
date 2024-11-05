package com.hackathon.bankingapp.services;

import com.hackathon.bankingapp.DTO.AccountInfoResponseDTO;
import com.hackathon.bankingapp.DTO.UserInfoResponseDTO;
import com.hackathon.bankingapp.DTO.UserRegistrationDTO;
import com.hackathon.bankingapp.entities.User;
import com.hackathon.bankingapp.exceptions.*;
import com.hackathon.bankingapp.repositories.UserRepository;
import com.hackathon.bankingapp.utils.TokenUtil;
import com.hackathon.bankingapp.utils.UUIDUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements IUserService {

    private static final Logger logger = LoggerFactory.getLogger(TokenUtil.class);


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ITokenBlacklistService tokenBlacklistService;

    @Value("${jwt.secret}") // Clave secreta para firmar el JWT, definida en application.properties
    private String secretKey;


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
            try {
                throw new EmailAlreadyExistsException("This email already exists: " + userRegistrationDTO.getEmail());
            } catch (EmailAlreadyExistsException e) {
                throw new RuntimeException(e);
            }
        }

        // Verificar si el número de teléfono ya existe en la base de datos
        if (userRepository.findByPhoneNumber(userRegistrationDTO.getPhoneNumber()).isPresent()) {
            try {
                throw new PhoneNumberAlreadyExistsException("This phone number already exists: " + userRegistrationDTO.getPhoneNumber());
            } catch (PhoneNumberAlreadyExistsException e) {
                throw new RuntimeException(e);
            }
        }

        // Validar que los campos no estén vacíos
        if (userRegistrationDTO.getName().isEmpty() ||
                userRegistrationDTO.getEmail().isEmpty() ||
                userRegistrationDTO.getPassword().isEmpty() ||
                userRegistrationDTO.getAddress().isEmpty() ||
                userRegistrationDTO.getPhoneNumber().isEmpty()) {
            throw new EmptyFieldException("Fields cannot be empty");
        }

        // Validar el formato del correo electrónico
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        if (!userRegistrationDTO.getEmail().matches(emailRegex)) {
            throw new InvalidEmailFormatException("Invalid email format: " + userRegistrationDTO.getEmail());
        }

        // Encriptar la contraseña
        String hashedPassword = passwordEncoder.encode(userRegistrationDTO.getPassword());

        // Crear un nuevo usuario y guardarlo en la base de datos
        User user = new User();
        // Generar un nuevo UUID para el número de cuenta
        UUID accountNumber = UUID.randomUUID();
        user.setAccountNumber(UUIDUtil.toBytes(accountNumber)); // Convertir UUID a byte[]
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
                throw new InvalidIdentifierFormatException("Invalid identifier format");

            }
        }

        // Verificar existencia y validez de contraseña
        return Optional.of(userOptional.filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials or user not found")));

    }

    @Override
    public UserInfoResponseDTO getUserInfoFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();

            UUID accountNumber = UUID.fromString(claims.getSubject());
            User user = userRepository.findByAccountNumber(UUIDUtil.toBytes(accountNumber))
                    .orElseThrow(() -> new UserNotFoundException("User not found for account number: " + accountNumber));

            UUID accountNumberUUID = toUUID(user.getAccountNumber());

            return new UserInfoResponseDTO(
                    user.getName(),
                    user.getEmail(),
                    user.getPhoneNumber(),
                    user.getAddress(),
                    accountNumberUUID);
        } catch (Exception e) {
            logger.error("Error extracting user from token: {}", e.getMessage());
            throw new InvalidTokenException("Invalid token", e);
        }
    }
    @Override
    public AccountInfoResponseDTO getAccountInfo(UUID accountNumber) {
        // Buscar el usuario por el número de cuenta
        User user = userRepository.findByAccountNumber(toByteArray(accountNumber))
                .orElseThrow(() -> new UserNotFoundException("User not found for account number: " + accountNumber));

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
