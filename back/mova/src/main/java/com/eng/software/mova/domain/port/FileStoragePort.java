package com.eng.software.mova.domain.port;

import org.springframework.web.multipart.MultipartFile;

public interface FileStoragePort {
    String uploadFile(MultipartFile file);
    void deleteFile(String fileUrl);
}
