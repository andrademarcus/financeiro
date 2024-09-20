package com.lyncas.financeiro.service;

import com.lyncas.financeiro.model.JwtResponse;
import com.lyncas.financeiro.model.LoginFormRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

public interface AuthService {

    JwtResponse authenticate(LoginFormRequest login);

    Authentication getAuthentication();

    UsernamePasswordAuthenticationToken getUsernamePasswordAuthenticationToken();
}
