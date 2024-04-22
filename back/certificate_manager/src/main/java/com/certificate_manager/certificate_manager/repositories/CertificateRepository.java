package com.certificate_manager.certificate_manager.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.certificate_manager.certificate_manager.entities.Certificate;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {

	public Optional<Certificate> findBySerialNumber(String serialNumber);
	
	@Query(value = "select * from \"certificates\" where \"issuer_id\" = :id", nativeQuery=true)
	public List<Certificate> getAllCertificatesWithCurrentCertificateAsIssuer(int id);

	@Query("select c from Certificate c where c.issuedTo.id = ?1")
	public List<Certificate> findAllForUser(long userId);
	
	@Query("select c from Certificate c where c.type = 0")
	public List<Certificate> getRootCertificates();
	
}
