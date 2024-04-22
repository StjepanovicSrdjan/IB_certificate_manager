package com.certificate_manager.certificate_manager.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.certificate_manager.certificate_manager.entities.UsedPassword;

public interface UsedPasswordRepository extends JpaRepository<UsedPassword, Long>{

	public List<UsedPassword> findByOwnerId(int id);
}

