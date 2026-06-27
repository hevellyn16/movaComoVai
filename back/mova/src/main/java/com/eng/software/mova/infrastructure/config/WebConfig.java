package com.eng.software.mova.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.avatar.upload-dir:uploads/avatars}")
    private String avatarUploadDir;

    @Value("${file.upload-dir:./uploads}")
    private String fileUploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path avatarUploadPath = Paths.get(avatarUploadDir).toAbsolutePath().normalize();
        Path fileUploadPath = Paths.get(fileUploadDir).toAbsolutePath().normalize();

        registry.addResourceHandler("/uploads/avatars/**")
                .addResourceLocations("file:" + avatarUploadPath.toString() + "/");

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + fileUploadPath.toString() + "/");
    }
}
