package com.jobportal.controller;

import com.jobportal.model.Application;
import com.jobportal.repository.UserRepository;
import com.jobportal.service.ApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/applications")
@CrossOrigin(origins = "*")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final UserRepository userRepository;

    public ApplicationController(ApplicationService applicationService, UserRepository userRepository) {
        this.applicationService = applicationService;
        this.userRepository = userRepository;
    }

    @PostMapping("/apply")
    public ResponseEntity<?> apply(@RequestBody Map<String, Object> body, Principal principal) {
        try {
            // body: { "jobId": 1, "resumeId": 2 }
            if (principal == null) {
                return ResponseEntity.status(401).body("Unauthorized");
            }

            if (body == null || body.get("jobId") == null || body.get("resumeId") == null) {
                return ResponseEntity.badRequest().body(java.util.Map.of("error", "jobId and resumeId are required"));
            }

            Long jobId;
            Long resumeId;
            try {
                jobId = Long.valueOf(body.get("jobId").toString());
                resumeId = Long.valueOf(body.get("resumeId").toString());
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body(java.util.Map.of("error", "Invalid jobId or resumeId format"));
            }

            var user = userRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Application app = applicationService.apply(jobId, user.getId(), resumeId);
            return ResponseEntity.ok(app);
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(java.util.Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(java.util.Map.of("error", "Failed to apply: " + e.getMessage()));
        }
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<?> listByJob(@PathVariable("jobId") Long jobId, Principal principal) {
        // verify recruiter owns this job
        List<com.jobportal.dto.ApplicationDTO> list = applicationService.listByJob(jobId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyApplications(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        var user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(applicationService.getApplicationsByUserId(user.getId()));
    }

    @PostMapping("/{applicationId}/acknowledge")
    public ResponseEntity<?> acknowledgeApplication(@PathVariable("applicationId") Long applicationId, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        Application app = applicationService.updateApplicationStatus(applicationId, "ACKNOWLEDGED");
        return ResponseEntity.ok(app);
    }

    @PostMapping("/{applicationId}/reject")
    public ResponseEntity<?> rejectApplication(@PathVariable("applicationId") Long applicationId, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        Application app = applicationService.updateApplicationStatus(applicationId, "REJECTED");
        return ResponseEntity.ok(app);
    }
}
