package com.lyncas.financeiro.service.impl;

import com.lyncas.financeiro.component.security.JwtUtil;
import com.lyncas.financeiro.model.JwtResponse;
import com.lyncas.financeiro.model.LoginFormRequest;
import com.lyncas.financeiro.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public JwtResponse authenticate(LoginFormRequest login) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(login.username(), login.password()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtUtil.generateJwtToken(authentication);
//        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return new JwtResponse(token);

    }

    @Override
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public String getUsername() {
        return getAuthentication().getName();
    }

    @Override
    public UsernamePasswordAuthenticationToken getUsernamePasswordAuthenticationToken() {
        Authentication authentication = getAuthentication();
        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            return (UsernamePasswordAuthenticationToken) authentication;
        }
        return null;
    }

}
