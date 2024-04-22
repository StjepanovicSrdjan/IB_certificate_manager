package com.certificate_manager.certificate_manager.dtos;

import java.nio.file.Path;

import org.springframework.core.io.Resource;

public class DownloadCertDTO {

	private Resource file;
	private Path path;

	public DownloadCertDTO(Resource file) {
		super();
		this.file = file;
	}

	public DownloadCertDTO(Resource file, Path path) {
		super();
		this.file = file;
		this.path = path;
	}

	public Resource getFile() {
		return file;
	}

	public void setFile(Resource file) {
		this.file = file;
	}

	public Path getPath() {
		return path;
	}

	public void setPath(Path path) {
		this.path = path;
	}
	
	

}
