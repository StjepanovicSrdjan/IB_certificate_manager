package com.certificate_manager.certificate_manager.entities;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name="used_passwords")
public class UsedPassword {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private String password;
	
	@NotNull
	private LocalDate createdAt;
	
	@ManyToOne
	private User owner;
	
	public UsedPassword() {
		
	}
	
	public UsedPassword(User owner) {
		this.password = owner.getPassword();
		this.createdAt = LocalDate.now();
		this.owner = owner;
	}
	
	public String getPassword() {
		return this.password;
	}
}
