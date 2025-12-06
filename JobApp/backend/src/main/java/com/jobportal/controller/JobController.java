package com.jobportal.controller;

import com.jobportal.dto.JobDto;
import com.jobportal.model.Job;
import com.jobportal.service.JobService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jobs")
@CrossOrigin(origins = "*")
public class JobController {

    private final JobService jobService;
    private final com.jobportal.repository.UserRepository userRepository;
    private final com.jobportal.repository.JobRepository jobRepository;

    public JobController(JobService jobService, com.jobportal.repository.UserRepository userRepository, com.jobportal.repository.JobRepository jobRepository) {
        this.jobService = jobService;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }

    // GET /jobs?q=java&page=0&size=10
    @GetMapping
    public Page<Job> search(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return jobService.search(q, PageRequest.of(page, size, Sort.by("createdAt").descending()));
    }

    // POST /jobs
    @PostMapping
    public ResponseEntity<?> create(@RequestBody JobDto dto, Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).body("Unauthorized");
        var user = userRepository.findByEmail(auth.getName()).orElseThrow();
        
        // Validate required fields
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", "Job title is required"));
        }
        if (dto.getCompany() == null || dto.getCompany().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", "Company name is required"));
        }
        if (dto.getLocation() == null || dto.getLocation().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", "Job location is required"));
        }
        if (dto.getSkills() == null || dto.getSkills().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", "Required skills are required"));
        }
        if (dto.getDescription() == null || dto.getDescription().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", "Job description is required"));
        }
        
        // Validate minimum lengths
        if (dto.getTitle().trim().length() < 3) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", "Job title must be at least 3 characters long"));
        }
        if (dto.getDescription().trim().length() < 20) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", "Job description must be at least 20 characters long"));
        }
        
        // Validate skills format (should have at least one skill)
        String[] skillsArray = dto.getSkills().split(",");
        long validSkillsCount = java.util.Arrays.stream(skillsArray)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .count();
        if (validSkillsCount == 0) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", "Please provide at least one valid skill"));
        }
        
        Job j = new Job();
        j.setTitle(dto.getTitle().trim());
        j.setCompany(dto.getCompany().trim());
        j.setDescription(dto.getDescription().trim());
        j.setLocation(dto.getLocation().trim());
        j.setSalaryRange(dto.getSalaryRange() != null ? dto.getSalaryRange().trim() : "");
        j.setJobType(dto.getJobType() != null ? dto.getJobType().trim() : "Full-time");
        j.setSkills(dto.getSkills().trim());
        j.setRecruiterId(user.getId());
        
        return ResponseEntity.ok(jobService.create(j));
    }
    
    @GetMapping("/my")
    public ResponseEntity<?> myJobs(Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).body("Unauthorized");
        var user = userRepository.findByEmail(auth.getName()).orElseThrow();
        return ResponseEntity.ok(jobRepository.findByRecruiterId(user.getId()));
    }

    // GET /jobs/1
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable("id") Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body(java.util.Map.of("error", "Invalid job ID"));
            }
            Job job = jobService.findById(id);
            return ResponseEntity.ok(job);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(java.util.Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(java.util.Map.of("error", "Failed to get job: " + e.getMessage()));
        }
    }

    // GET /jobs/recommended - Get jobs with match scores for candidate
    @GetMapping("/recommended")
    public ResponseEntity<?> getRecommendedJobs(Authentication auth) {
        if (auth == null) {
            return ResponseEntity.status(401).body(java.util.Map.of("error", "Unauthorized"));
        }
        var user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(jobService.getRecommendedJobs(user.getId()));
    }

    // DELETE /jobs/{id} - Delete a job (only by the recruiter who posted it)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJob(@PathVariable("id") Long id, Authentication auth) {
        try {
            if (auth == null) {
                return ResponseEntity.status(401).body(java.util.Map.of("error", "Unauthorized"));
            }
            
            var user = userRepository.findByEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Check if job exists
            Job job = jobRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Job not found"));
            
            // Verify the user is the recruiter who posted this job
            if (!job.getRecruiterId().equals(user.getId())) {
                return ResponseEntity.status(403).body(java.util.Map.of("error", "You can only delete jobs you posted"));
            }
            
            // Delete the job (cascade will handle applications if configured, otherwise delete manually)
            jobService.deleteJob(id);
            
            return ResponseEntity.ok(java.util.Map.of("message", "Job deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(java.util.Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(java.util.Map.of("error", "Failed to delete job: " + e.getMessage()));
        }
    }
}
