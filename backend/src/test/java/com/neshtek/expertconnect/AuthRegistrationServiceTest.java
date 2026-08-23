package com.neshtek.expertconnect;

import com.neshtek.expertconnect.dto.UserRegistrationRequest;
import com.neshtek.expertconnect.entity.AppUserRole;
import com.neshtek.expertconnect.repository.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class AuthRegistrationServiceTest {
    @Test void passwordEncoderDoesNotStorePlainText(){
        var encoder=new BCryptPasswordEncoder();
        String raw="NeshTek@12345";
        String hash=encoder.encode(raw);
        assertNotEquals(raw,hash);
        assertTrue(encoder.matches(raw,hash));
    }

    @Test void rolesAreDefined(){
        assertArrayEquals(new AppUserRole[]{AppUserRole.CUSTOMER,AppUserRole.EXPERT,AppUserRole.ADMIN},AppUserRole.values());
    }
}
