package com.hackathon.bankingapp.services;

public interface ITokenBlacklistService {

    void blacklistToken(String token);
    boolean isTokenBlacklisted(String token);
}
