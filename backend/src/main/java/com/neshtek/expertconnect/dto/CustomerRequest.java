package com.neshtek.expertconnect.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CustomerRequest(
        @NotBlank @Size(max = 200) String companyName,
        @NotBlank @Size(max = 150) String contactName,
        @NotBlank @Email @Size(max = 320) String email,
        @Size(max = 30) String phone,
        @Size(max = 100) String country,
        @Size(max = 100) String city,
        @Size(max = 100) String timezone,
        @Size(max = 150) String industry,
        @Size(max = 50) String companySize
) {}
