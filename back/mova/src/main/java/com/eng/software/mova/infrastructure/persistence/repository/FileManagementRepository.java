package com.eng.software.mova.infrastructure.persistence.repository;

import com.eng.software.mova.domain.port.FileStoragePort;
import com.eng.software.mova.shared.exceptions.FileManipulationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Repository
public class FileManagementRepository implements FileStoragePort {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${app.base-url}")
    private String baseUrl;

    @Override
    public String uploadFile(MultipartFile file) {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";

            String uniqueFileName = UUID.randomUUID() + extension;
            Path filePath = uploadPath.resolve(uniqueFileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return baseUrl + "/uploads/" + uniqueFileName;

        } catch (IOException e) {
            throw new FileManipulationException("Failed to upload file: " + file.getOriginalFilename());
        }
    }

    @Override
    public void deleteFile(String fileUrl) {
        try {
            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            Path filePath = Paths.get(uploadDir).resolve(fileName);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new FileManipulationException("Failed to delete file: " + fileUrl);
        }
    }
}
