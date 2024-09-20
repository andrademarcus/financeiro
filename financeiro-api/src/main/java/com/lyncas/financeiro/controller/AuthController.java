package com.lyncas.financeiro.controller;

import com.lyncas.financeiro.model.JwtResponse;
import com.lyncas.financeiro.model.LoginFormRequest;
import com.lyncas.financeiro.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/signin")
	public ResponseEntity<JwtResponse> authenticate(@RequestBody @Valid LoginFormRequest login) {
		return ResponseEntity.ok(authService.authenticate(login));
	}

}
