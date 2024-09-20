package com.lyncas.financeiro.model;


public record JwtResponse(String accessToken, String tokenType) {

	public JwtResponse(String accessToken) {
		this(accessToken, "Bearer");
	}

}