package com.projects.commonlib.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class JwtAuthFilter extends OncePerRequestFilter {

    AuthUtil authUtil;
    HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {

            log.info("incoming request: {}", request.getRequestURI());

            //Read Authorization Header
            final var authHeader = request.getHeader("Authorization");
            //If no header or not Bearer -> skip filter
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            //Extract token
            var jwtToken = authHeader.substring(7);

            //Extract username and id
            var user = authUtil.verifyAccessToken(jwtToken);

            if (user != null && SecurityContextHolder.getContext()
                    .getAuthentication() == null) {
                var authToken = new UsernamePasswordAuthenticationToken(
                        user, jwtToken, user.authorities());

                SecurityContextHolder.getContext()
                        .setAuthentication(authToken);
            }

            //Continue filter chain
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            handlerExceptionResolver.resolveException(request, response, null, e);
        }
    }
}
