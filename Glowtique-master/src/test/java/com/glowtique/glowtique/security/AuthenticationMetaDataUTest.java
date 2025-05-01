package com.glowtique.glowtique.security;

import com.glowtique.glowtique.user.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class AuthenticationMetaDataUTest {
    private AuthenticationMetadata authenticationMetadata;
    private UUID uuid;
    private String email;
    private String password;
    private UserRole userRole;
    private boolean isActive;


    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID();
        email = "damqnskiqq@gmail.com";
        password = "asdasd";
        userRole = UserRole.USER;
        isActive = true;

        authenticationMetadata = new AuthenticationMetadata(uuid, email, password, userRole, isActive);
    }

    @Test
    void testEveryField() {
        assertEquals(uuid, authenticationMetadata.getUserId());
        assertEquals(email, authenticationMetadata.getEmail());
        assertEquals(isActive, authenticationMetadata.isActive());
    }
    @Test
    void testUsername() {
        assertEquals(email, authenticationMetadata.getUsername());
    }
    @Test
    void testPassword() {
        assertEquals(password, authenticationMetadata.getPassword());
    }
    @Test
    void testRole() {
        assertEquals(userRole, authenticationMetadata.getUserRole());
    }
    @Test
    void testGetAuthorities() {
        Collection<? extends GrantedAuthority> authorities = authenticationMetadata.getAuthorities();
        assertEquals(1, authorities.size());
        assertEquals("ROLE_USER", authorities.iterator().next().getAuthority());
    }
    @Test
    void testIsAccountNonExpired() {
        assertTrue(authenticationMetadata.isAccountNonExpired());
    }
    @Test
    void testIsAccountNonLocked() {
        assertTrue(authenticationMetadata.isAccountNonLocked());
    }
    @Test
    void testIsCredentialsNonExpired() {
        assertTrue(authenticationMetadata.isCredentialsNonExpired());
    }
    @Test
    void testIsEnabled() {
        assertTrue(authenticationMetadata.isEnabled());
    }

}
