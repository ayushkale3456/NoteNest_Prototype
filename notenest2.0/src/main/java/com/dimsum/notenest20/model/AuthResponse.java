package com.dimsum.notenest20.model;

public class AuthResponse {
	private String accessToken;
	private String refreshToken;
	private String role;
	private String email;
	private String stream;

	public AuthResponse(String accessToken, String refreshToken, String role, String email, String stream) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.role = role;
		this.email = email;
		this.stream = stream;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getStream() {
		return stream;
	}

	public void setStream(String stream) {
		this.stream = stream;
	}

}
