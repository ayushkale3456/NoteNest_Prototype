package com.dimsum.notenest20.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;
import java.util.*;

@Entity
public class User implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;
	private String email;
	private String password;

	@Enumerated(EnumType.STRING)
	private Role role;

	private String stream;
	private String year;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

//	public String getPassword() {
//		return password;
//	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public String getStream() {
		return stream;
	}

	public void setStream(String stream) {
		this.stream = stream;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	// --- Implement all methods from UserDetails interface ---

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// This method defines the roles/authorities granted to the user.
		// Assuming 'role' is a single enum field for simplicity.
		return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
		// If you had multiple roles, you'd convert them all to SimpleGrantedAuthority
		// objects.
	}

	@Override
	public String getPassword() {
		// Returns the password used to authenticate the user.
		// IMPORTANT: This should be the HASHED password from the database.
		return password;
	}

	@Override
	public String getUsername() {
		// Returns the username used to authenticate the user.
		// In your case, you are using 'email' as the username.
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		// Indicates whether the user's account has expired.
		// You can add logic here, e.g., based on an 'expiryDate' field.
		return true; // For now, assume it never expires
	}

	@Override
	public boolean isAccountNonLocked() {
		// Indicates whether the user is locked or unlocked.
		// You can add logic here, e.g., based on an 'locked' boolean field.
		return true; // For now, assume it's never locked
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// Indicates whether the user's credentials (password) have expired.
		// You can add logic here, e.g., based on a 'passwordLastChangedDate' field.
		return true; // For now, assume credentials never expire
	}

	@Override
	public boolean isEnabled() {
		// Indicates whether the user is enabled or disabled.
		// You can add logic here, e.g., based on an 'enabled' boolean field.
		return true; // For now, assume user is always enabled
	}

}
