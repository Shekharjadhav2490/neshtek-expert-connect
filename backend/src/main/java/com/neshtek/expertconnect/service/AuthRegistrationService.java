package com.neshtek.expertconnect.service;

import com.neshtek.expertconnect.dto.UserRegistrationRequest;
import com.neshtek.expertconnect.dto.UserRegistrationResponse;
import com.neshtek.expertconnect.entity.*;
import com.neshtek.expertconnect.exception.ResourceNotFoundException;
import com.neshtek.expertconnect.repository.AppUserRepository;
import com.neshtek.expertconnect.repository.CustomerRepository;
import com.neshtek.expertconnect.repository.ExpertRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class AuthRegistrationService {
    private final AppUserRepository users;
    private final CustomerRepository customers;
    private final ExpertRepository experts;
    private final PasswordEncoder encoder;

    public AuthRegistrationService(AppUserRepository users, CustomerRepository customers, ExpertRepository experts, PasswordEncoder encoder) {
        this.users=users; this.customers=customers; this.experts=experts; this.encoder=encoder;
    }

    @Transactional
    public UserRegistrationResponse register(UserRegistrationRequest r) {
        String email=r.email().trim().toLowerCase(Locale.ROOT);
        if(users.existsByEmailIgnoreCase(email)) throw new IllegalArgumentException("Email is already registered");
        AppUserRole role=parseRole(r.role());
        AppUser u=new AppUser(); u.setEmail(email); u.setPasswordHash(encoder.encode(r.password())); u.setRole(role); u.setStatus(AppUserStatus.ACTIVE);
        if(role==AppUserRole.CUSTOMER){
            if(r.customerId()==null || r.expertId()!=null) throw new IllegalArgumentException("CUSTOMER registration requires customerId only");
            u.setCustomer(customers.findById(r.customerId()).orElseThrow(() -> new ResourceNotFoundException("Customer not found: "+r.customerId())));
        } else if(role==AppUserRole.EXPERT){
            if(r.expertId()==null || r.customerId()!=null) throw new IllegalArgumentException("EXPERT registration requires expertId only");
            u.setExpert(experts.findById(r.expertId()).orElseThrow(() -> new ResourceNotFoundException("Expert not found: "+r.expertId())));
        } else if(r.customerId()!=null || r.expertId()!=null) throw new IllegalArgumentException("ADMIN registration cannot reference customer or expert");
        AppUser saved=users.save(u);
        return new UserRegistrationResponse(saved.getId(),saved.getEmail(),saved.getRole().name(),saved.getStatus().name(),saved.getCustomer()==null?null:saved.getCustomer().getId(),saved.getExpert()==null?null:saved.getExpert().getId());
    }
    private AppUserRole parseRole(String role){
        try{return AppUserRole.valueOf(role.trim().toUpperCase(Locale.ROOT));}
        catch(Exception e){throw new IllegalArgumentException("Role must be CUSTOMER, EXPERT or ADMIN");}
    }
}
