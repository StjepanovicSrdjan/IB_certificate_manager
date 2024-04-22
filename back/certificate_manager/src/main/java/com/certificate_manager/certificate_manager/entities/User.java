package com.certificate_manager.certificate_manager.entities;

import java.time.LocalDateTime;

import com.certificate_manager.certificate_manager.dtos.UserDTO;
import com.certificate_manager.certificate_manager.enums.UserRole;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name="users")
public class User {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@NotEmpty
	@Pattern(regexp = "^([a-zA-Zčćđžš ]*)$")
	private String name;

	@NotEmpty
	@Pattern(regexp = "^([a-zA-Zčćđžš ]*)$")
	private String lastname;

	@NotEmpty
	@Email
	private String email;

//	@NotEmpty
	@Pattern(regexp = "^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*")
	private String phoneNumber;

//	@NotEmpty
	private String password;

	private Boolean verified;
	
	private String socialId;

	@NotNull
	private UserRole role;
	
	private LocalDateTime timeOfLastSetPassword;

	public User() {

	}

	public User(int id, String name, String lastname, String email, String phoneNumber, String password,
			Boolean verified) {
		super();
		this.id = id;
		this.name = name;
		this.lastname = lastname;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.password = password;
		this.verified = verified;
		this.role = UserRole.USER;
		this.timeOfLastSetPassword = LocalDateTime.now();
		this.socialId = null;
	}
	
	public User(String name, String lastname, String email,
			Boolean verified, String socialId, String password) {
		super();
		this.name = name;
		this.lastname = lastname;
		this.email = email;
		this.phoneNumber = null;
		this.password = password;
		this.verified = verified;
		this.role = UserRole.USER;
		this.socialId = socialId;
	}

	public User(int id, String name, String lastname, String email, String phoneNumber, String password,
			Boolean verified, UserRole role) {
		super();
		this.id = id;
		this.name = name;
		this.lastname = lastname;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.password = password;
		this.verified = verified;
		this.role = role;
		this.timeOfLastSetPassword = LocalDateTime.now();
		this.socialId = null;
	}
	
	public User(UserDTO userDTO) {
		super();
		this.name = userDTO.getName();
		this.lastname = userDTO.getLastname();
		this.email = userDTO.getEmail();
		this.phoneNumber = userDTO.getPhoneNumber();
		this.verified = false;
		this.socialId = null;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getVerified() {
		return verified;
	}

	public void setVerified(Boolean verified) {
		this.verified = verified;
	}

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}

	public LocalDateTime getTimeOfLastSetPassword() {
		return timeOfLastSetPassword;
	}

	public void setTimeOfLastSetPassword(LocalDateTime timeOfLastSetPassword) {
		this.timeOfLastSetPassword = timeOfLastSetPassword;
	}
	
	
	public String getSocialId() {
		return socialId;
	}

	public void setSocialId(String socialId) {
		this.socialId = socialId;
	}
}
