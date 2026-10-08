package com.example.blog.security;

import com.example.blog.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.mock.web.MockHttpServletRequest;
import com.example.blog.mapper.UserMapper;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class SecurityFlowTest {
    private static final String DEMO_HASH = "$2a$10$ZaB810qETQjV2fbAdd.rYu0rVHqgWrg0vA7eSLw1OWJXQoicGcefC";

    @Test
    void seededPasswordMatchesDocumentedDemoPassword() {
        assertTrue(new BCryptPasswordEncoder().matches("password", DEMO_HASH));
    }

    @Test
    void jwtRoundTripKeepsIdentityAndRole() {
        JwtService service = new JwtService("test-secret-that-is-at-least-32-bytes-long", 1);
        User user = new User();
        user.setId(7L);
        user.setUsername("admin");
        user.setRole("ADMIN");

        JwtPayload payload = service.parse(service.create(user));

        assertEquals(7L, payload.userId());
        assertEquals("admin", payload.username());
        assertEquals("ADMIN", payload.role());
        assertEquals(0, payload.tokenVersion());
    }

    @Test
    void publicAndLoginEndpointsIgnoreInvalidStoredTokens() {
        JwtFilter filter = new JwtFilter(mock(JwtService.class), mock(UserMapper.class), new ObjectMapper());
        assertTrue(filter.shouldNotFilter(new MockHttpServletRequest("GET", "/api/public/articles")));
        assertTrue(filter.shouldNotFilter(new MockHttpServletRequest("POST", "/api/auth/login")));
        assertTrue(filter.shouldNotFilter(new MockHttpServletRequest("POST", "/api/auth/password/reset-code")));
        assertTrue(filter.shouldNotFilter(new MockHttpServletRequest("GET", "/uploads/avatars/example.png")));
    }
}
