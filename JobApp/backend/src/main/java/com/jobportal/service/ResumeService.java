package com.jobportal.service;

import com.jobportal.model.Resume;
import com.jobportal.repository.ResumeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.LinkedMultiValueMap;

import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;
    
    public ResumeService(ResumeRepository resumeRepository) {
        this.resumeRepository = resumeRepository;
    }

    @Value("${ai.resume.parser.url:http://localhost:5001}")
    private String aiParserUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public Resume saveResume(MultipartFile file, Long userId) throws Exception {
        // 1. Save file locally
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path uploadPath = Paths.get("uploads");
        if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
        Files.copy(file.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);

        // 2. Parse
        Map<String, Object> parsedData = parseResume(file);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(parsedData);

        // 3. Save Entity
        Resume resume = new Resume();
        resume.setUserId(userId);
        resume.setFileUrl("uploads/" + fileName);
        resume.setParsedJson(json);
        resume.setUploadedAt(LocalDateTime.now());

        return resumeRepository.save(resume);
    }

    public List<Resume> getResumesByUserId(Long userId) {
        return resumeRepository.findByUserId(userId);
    }

    public Map<String, Object> parseResume(MultipartFile file) throws Exception {

        // Prepare multipart form-data body
        ByteArrayResource fileAsResource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };

        // Create the body content
        LinkedMultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", fileAsResource);

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<LinkedMultiValueMap<String, Object>> request =
                new HttpEntity<>(body, headers);

        // Call the AI Parser API
        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    aiParserUrl + "/parse",
                    HttpMethod.POST,
                    request,
                    Map.class
            );
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback if parser fails
            return Map.of("error", "Parser failed: " + e.getMessage());
        }
    }
}
