package com.jobportal.service;

import com.jobportal.model.*;
import com.jobportal.repository.*;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;


@Service
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ApplicationService(ApplicationRepository ar, JobRepository jr, ResumeRepository rr, UserRepository ur) {
        this.applicationRepository = ar;
        this.jobRepository = jr;
        this.resumeRepository = rr;
        this.userRepository = ur;
    }

    public Application apply(Long jobId, Long userId, Long resumeId) {
        if (jobId == null || jobId <= 0) {
            throw new RuntimeException("Invalid job ID");
        }
        if (userId == null || userId <= 0) {
            throw new RuntimeException("Invalid user ID");
        }
        if (resumeId == null || resumeId <= 0) {
            throw new RuntimeException("Invalid resume ID");
        }

        Job job = jobRepository.findById(jobId).orElseThrow(() -> new RuntimeException("Job not found"));
        Resume resume = resumeRepository.findById(resumeId).orElseThrow(() -> new RuntimeException("Resume not found"));

        // Check if user already applied for this job
        List<Application> existingApps = applicationRepository.findByJobId(jobId);
        boolean alreadyApplied = existingApps.stream()
                .anyMatch(app -> app.getUserId().equals(userId));
        if (alreadyApplied) {
            throw new RuntimeException("You have already applied for this job");
        }

        double score = computeMatchScore(job.getSkills(), resume.getParsedJson());
        Application app = new Application();
        app.setJobId(jobId);
        app.setUserId(userId);
        app.setResumeId(resumeId);
        app.setMatchScore(score);
        app.setStatus("APPLIED");
        return applicationRepository.save(app);
    }

    public List<com.jobportal.dto.ApplicationDTO> listByJob(Long jobId) {
        if (jobId == null || jobId <= 0) {
            throw new RuntimeException("Invalid job ID");
        }
        List<Application> apps = applicationRepository.findByJobId(jobId);
        
        return apps.stream()
                .map(app -> {
                    Resume resume = app.getResumeId() != null ? resumeRepository.findById(app.getResumeId()).orElse(null) : null;
                    User user = app.getUserId() != null ? userRepository.findById(app.getUserId()).orElse(null) : null;
                    return new com.jobportal.dto.ApplicationDTO(app, resume, user);
                })
                .filter(dto -> dto.getApplication() != null) // Filter out any null applications
                .sorted((a, b) -> {
                    Double scoreA = a.getApplication().getMatchScore() != null ? a.getApplication().getMatchScore() : 0.0;
                    Double scoreB = b.getApplication().getMatchScore() != null ? b.getApplication().getMatchScore() : 0.0;
                    return Double.compare(scoreB, scoreA);
                })
                .collect(Collectors.toList());
    }

    public List<com.jobportal.dto.ApplicationDTO> getApplicationsByUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new RuntimeException("Invalid user ID");
        }
        List<Application> apps = applicationRepository.findByUserId(userId);
        
        return apps.stream()
                .map(app -> {
                    Job job = app.getJobId() != null ? jobRepository.findById(app.getJobId()).orElse(null) : null;
                    Resume resume = app.getResumeId() != null ? resumeRepository.findById(app.getResumeId()).orElse(null) : null;
                    User user = userRepository.findById(userId).orElse(null);
                    return new com.jobportal.dto.ApplicationDTO(app, resume, user, job);
                })
                .filter(dto -> dto.getApplication() != null) // Filter out any null applications
                .sorted((a, b) -> {
                    if (a.getApplication().getAppliedAt() == null || b.getApplication().getAppliedAt() == null) {
                        return 0;
                    }
                    return b.getApplication().getAppliedAt().compareTo(a.getApplication().getAppliedAt());
                })
                .collect(Collectors.toList());
    }

    public Application updateApplicationStatus(Long applicationId, String status) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        app.setStatus(status);
        return applicationRepository.save(app);
    }

    // Very simple scoring: match skills found in parsedJson against job skills (csv)
    // Improved scoring: parse JSON and match exact skills
    private double computeMatchScore(String jobSkillsCsv, String parsedJson) {
        if (jobSkillsCsv == null || jobSkillsCsv.isBlank() || parsedJson == null) return 0.0;
        
        Set<String> required = Arrays.stream(jobSkillsCsv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
                
        if (required.isEmpty()) return 0.0;

        Set<String> candidateSkills = new HashSet<>();
        try {
            // Parse the JSON to get the "skills" list
            Map<String, Object> data = objectMapper.readValue(parsedJson, new TypeReference<Map<String, Object>>(){});
            if (data.containsKey("skills")) {
                List<String> skillsList = (List<String>) data.get("skills");
                if (skillsList != null) {
                    candidateSkills = skillsList.stream()
                            .map(String::trim)
                            .map(String::toLowerCase)
                            .collect(Collectors.toSet());
                }
            }
        } catch (Exception e) {
            // Fallback or log error
            System.err.println("Error parsing resume JSON: " + e.getMessage());
            return 0.0;
        }

        int matched = 0;
        for (String req : required) {
            if (candidateSkills.contains(req)) {
                matched++;
            }
        }
        
        return (double) matched / required.size() * 100.0;
    }
}
