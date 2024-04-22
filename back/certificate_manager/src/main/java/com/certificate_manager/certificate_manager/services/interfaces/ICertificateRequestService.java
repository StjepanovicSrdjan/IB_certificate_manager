package com.certificate_manager.certificate_manager.services.interfaces;

import java.util.List;

import com.certificate_manager.certificate_manager.dtos.CertificateRequestCreateDTO;
import com.certificate_manager.certificate_manager.dtos.CertificateRequestReturnedDTO;

public interface ICertificateRequestService {
	
	public List<CertificateRequestReturnedDTO> getAllForRequester();

	public void acceptRequest(long id);

	public void denyRequest(long id, String rejectionReason);

	public List<CertificateRequestReturnedDTO> getAllRequestsByMe();

	public List<CertificateRequestReturnedDTO> getAllRequestsFromMe();
}
