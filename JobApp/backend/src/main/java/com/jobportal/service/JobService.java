package com.jobportal.service;

import com.jobportal.model.Job;
import com.jobportal.model.Resume;
import com.jobportal.repository.ApplicationRepository;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.ResumeRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final ResumeRepository resumeRepository;
    private final ApplicationRepository applicationRepository;
    
    public JobService(JobRepository jr, ResumeRepository rr, ApplicationRepository ar){ 
        this.jobRepository = jr;
        this.resumeRepository = rr;
        this.applicationRepository = ar;
    }

    public Job create(Job job) { return jobRepository.save(job); }

    public Page<Job> search(String keyword, Pageable pageable) {
        if (keyword == null || keyword.isBlank()) return jobRepository.findAll(pageable);
        return jobRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword, pageable);
    }

    public Job findById(Long id){
        return jobRepository.findById(id).orElseThrow(() -> new RuntimeException("Job not found"));
    }

    @Transactional
    public void deleteJob(Long jobId) {
        // First delete all applications for this job to avoid foreign key constraint issues
        applicationRepository.findByJobId(jobId).forEach(app -> {
            applicationRepository.delete(app);
        });
        // Then delete the job
        jobRepository.deleteById(jobId);
    }

    public List<Map<String, Object>> getRecommendedJobs(Long userId) {
        if (userId == null || userId <= 0) {
            throw new RuntimeException("Invalid user ID");
        }
        
        // Get user's most recent resume
        List<Resume> resumes = resumeRepository.findByUserId(userId);
        if (resumes == null || resumes.isEmpty()) {
            return jobRepository.findAll().stream()
                    .filter(job -> job != null)
                    .map(job -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("job", job);
                        map.put("matchScore", 0.0);
                        return map;
                    })
                    .collect(Collectors.toList());
        }

        Resume resume = resumes.get(0); // Use most recent resume
        if (resume == null) {
            throw new RuntimeException("Resume not found");
        }
        String resumeJson = resume.getParsedJson();

        // Get all jobs and calculate match scores
        List<Job> allJobs = jobRepository.findAll();
        if (allJobs == null) {
            return new ArrayList<>();
        }
        
        return allJobs.stream()
                .filter(job -> job != null)
                .map(job -> {
                    double score = computeMatchScore(job.getSkills(), resumeJson);
                    Map<String, Object> map = new HashMap<>();
                    map.put("job", job);
                    map.put("matchScore", score);
                    return map;
                })
                .sorted((a, b) -> {
                    Double scoreA = (Double) a.get("matchScore");
                    Double scoreB = (Double) b.get("matchScore");
                    if (scoreA == null) scoreA = 0.0;
                    if (scoreB == null) scoreB = 0.0;
                    return Double.compare(scoreB, scoreA);
                })
                .collect(Collectors.toList());
    }

    private double computeMatchScore(String jobSkillsCsv, String parsedJson) {
        if (jobSkillsCsv == null || jobSkillsCsv.isBlank() || parsedJson == null) return 0.0;
        String[] required = Arrays.stream(jobSkillsCsv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toLowerCase)
                .toArray(String[]::new);
        int matched = 0;
        String lowerJson = parsedJson.toLowerCase();
        for (String r : required) {
            if (lowerJson.contains("\"" + r + "\"") || lowerJson.contains(r)) matched++;
        }
        double base = required.length == 0 ? 0.0 : (double) matched / required.length * 100;
        return Math.min(100.0, base);
    }
}
