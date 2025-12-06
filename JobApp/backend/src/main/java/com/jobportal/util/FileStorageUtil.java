package com.jobportal.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.nio.file.*;
import java.util.UUID;

@Component
public class FileStorageUtil {

    @Value("${file.storage.location}")
    private String baseLocation;

    public File store(MultipartFile multipartFile) throws IOException {
        Path base = Paths.get(baseLocation).toAbsolutePath().normalize();
        if (!Files.exists(base)) Files.createDirectories(base);
        String filename = UUID.randomUUID().toString() + "-" + multipartFile.getOriginalFilename();
        Path target = base.resolve(filename);
        try (InputStream in = multipartFile.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }
        return target.toFile();
    }
}
