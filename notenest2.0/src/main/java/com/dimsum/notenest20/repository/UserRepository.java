package com.dimsum.notenest20.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dimsum.notenest20.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);

	boolean existsByEmail(String email);
}
