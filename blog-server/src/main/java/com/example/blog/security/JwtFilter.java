package com.example.blog.security;

import com.example.blog.common.Result;
import com.example.blog.entity.User;
import com.example.blog.mapper.UserMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/public/")
                || path.startsWith("/uploads/avatars/")
                || path.equals("/api/auth/login")
                || path.equals("/api/auth/register")
                || path.startsWith("/api/auth/password/")
                || path.equals("/error");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header == null || header.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }
        if (!header.startsWith("Bearer ") || header.length() <= 7) {
            writeUnauthorized(response, "Token 格式不正确");
            return;
        }

        try {
            JwtPayload payload = jwtService.parse(header.substring(7));
            User user = userMapper.selectActiveById(payload.userId());
            int currentVersion = user == null || user.getTokenVersion() == null ? 0 : user.getTokenVersion();
            int tokenVersion = payload.tokenVersion() == null ? 0 : payload.tokenVersion();
            if (user == null || currentVersion != tokenVersion) {
                writeUnauthorized(response, "登录状态已失效");
                return;
            }
            AuthPrincipal principal = new AuthPrincipal(user.getId(), user.getUsername(), user.getRole());
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    principal, null, List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole())));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception ignored) {
            SecurityContextHolder.clearContext();
            writeUnauthorized(response, "Token 无效或已过期");
            return;
        }
        // 业务层异常必须交给全局异常处理器，不能被误判为 JWT 失效。
        filterChain.doFilter(request, response);
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), Result.failure(4010, message));
    }
}
