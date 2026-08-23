package com.neshtek.expertconnect.dto;

public record UserRegistrationResponse(Long userId, String email, String role, String status, Long customerId, Long expertId) {}
