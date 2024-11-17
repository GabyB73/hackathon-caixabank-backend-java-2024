package com.hackathon.bankingapp.utils;

import com.hackathon.bankingapp.entities.User;
import com.hackathon.bankingapp.exceptions.InvalidTokenException;
import com.hackathon.bankingapp.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import java.util.Date;
import java.util.Optional;

@Component
public class TokenUtil {


}
