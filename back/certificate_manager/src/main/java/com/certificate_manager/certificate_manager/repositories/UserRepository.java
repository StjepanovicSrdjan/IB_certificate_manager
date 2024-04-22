package com.certificate_manager.certificate_manager.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.certificate_manager.certificate_manager.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	
	public Optional<User> findByEmail(String email);
	
	public Optional<User> findBySocialId(String socialId);
	
	@Query("select u from User u where u.role = 0")
	public Optional<User> findAdmin();
	
	public Optional<User> findByPhoneNumber(String phoneNumber);
}
