package com.neshtek.expertconnect.security;

import com.neshtek.expertconnect.entity.AppUser;
import com.neshtek.expertconnect.entity.AppUserRole;
import com.neshtek.expertconnect.entity.ConsultationRequest;
import com.neshtek.expertconnect.repository.AppUserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class ResourceAuthorizationService {
    private final AppUserRepository userRepository;

    public ResourceAuthorizationService(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AppUser currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() == null) {
            throw new AccessDeniedException("Authentication is required");
        }
        Long userId;
        try {
            userId = Long.valueOf(authentication.getPrincipal().toString());
        } catch (NumberFormatException ex) {
            throw new AccessDeniedException("Invalid authenticated user");
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new AccessDeniedException("Authenticated user was not found"));
    }

    public boolean isAdmin() {
        return currentUser().getRole() == AppUserRole.ADMIN;
    }

    public void assertCustomerOwns(Long customerId) {
        AppUser user = currentUser();
        if (user.getRole() == AppUserRole.ADMIN) return;
        if (user.getRole() != AppUserRole.CUSTOMER || user.getCustomer() == null
                || !customerId.equals(user.getCustomer().getId())) {
            throw new AccessDeniedException("You are not allowed to access this customer resource");
        }
    }

    public void assertExpertOwns(Long expertId) {
        AppUser user = currentUser();
        if (user.getRole() == AppUserRole.ADMIN) return;
        if (user.getRole() != AppUserRole.EXPERT || user.getExpert() == null
                || !expertId.equals(user.getExpert().getId())) {
            throw new AccessDeniedException("You are not allowed to access this expert resource");
        }
    }

    public void assertCanAccess(ConsultationRequest request) {
        AppUser user = currentUser();
        if (user.getRole() == AppUserRole.ADMIN) return;
        if (user.getRole() == AppUserRole.CUSTOMER && user.getCustomer() != null
                && request.getCustomer() != null && user.getCustomer().getId().equals(request.getCustomer().getId())) return;
        if (user.getRole() == AppUserRole.EXPERT && user.getExpert() != null
                && request.getExpert() != null && user.getExpert().getId().equals(request.getExpert().getId())) return;
        throw new AccessDeniedException("You are not allowed to access this consultation request");
    }

    public void assertCanCreateForCustomer(Long customerId) {
        assertCustomerOwns(customerId);
    }
}
