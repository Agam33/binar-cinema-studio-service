package com.ra.bioskop.security.filters;

import com.ra.bioskop.dto.response.ValidateTokenResponse;
import com.ra.bioskop.util.Constants;
import com.ra.bioskop.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.reactive.function.client.WebClient;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class AuthenticationJwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Value("${service.client.authService.url}")
    private String authClient;

    private final WebClient webClient;


    public AuthenticationJwtFilter(JwtUtil jwtUtil, WebClient webClient) {
        this.jwtUtil = jwtUtil;
        this.webClient = webClient;
    }

    @Override

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (!hasToken(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = getToken(request);

        if (!jwtUtil.validateJwtToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        setAuthentication(token, request);

        filterChain.doFilter(request, response);
    }

    private void setAuthentication(String token, HttpServletRequest request) {
        ValidateTokenResponse response = webClient.get().uri(authClient + "/api/auth/validateToken")
                .header(Constants.HEADER, Constants.TOKEN_PREFIX + token)
                .retrieve()
                .bodyToMono(ValidateTokenResponse.class).block();

        if(response == null) return;

        String email = jwtUtil.getUserNameFromJwtToken(token);

        String[] authorities = { response.getAuthority() };

        List<SimpleGrantedAuthority> simpleAuthorities = new ArrayList<>();
        simpleAuthorities.add(new SimpleGrantedAuthority(authorities[0]));

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                email,
                null,
                simpleAuthorities);
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    private String getToken(HttpServletRequest request) {
        String header = request.getHeader(Constants.HEADER);
        return header.split(" ")[1].trim();
    }

    private boolean hasToken(HttpServletRequest request) {
        String header = request.getHeader(Constants.HEADER);
        return !ObjectUtils.isEmpty(header) && header.startsWith(Constants.TOKEN_PREFIX);
    }
}