package com.jobportal.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
public class Application {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long jobId;
    private Long userId;
    private Long resumeId;

    private String status = "APPLIED"; // APPLIED, REVIEWED, SHORTLISTED, REJECTED, SELECTED

    private Double matchScore = 0.0;

    private LocalDateTime appliedAt = LocalDateTime.now();

    // getters/setters
    public Long getId(){ return id; }
    public void setId(Long id){ this.id = id; }
    public Long getJobId(){ return jobId; }
    public void setJobId(Long jobId){ this.jobId = jobId; }
    public Long getUserId(){ return userId; }
    public void setUserId(Long userId){ this.userId = userId; }
    public Long getResumeId(){ return resumeId; }
    public void setResumeId(Long resumeId){ this.resumeId = resumeId; }
    public String getStatus(){ return status; }
    public void setStatus(String status){ this.status = status; }
    public Double getMatchScore(){ return matchScore; }
    public void setMatchScore(Double matchScore){ this.matchScore = matchScore; }
    public LocalDateTime getAppliedAt(){ return appliedAt; }
    public void setAppliedAt(LocalDateTime appliedAt){ this.appliedAt = appliedAt; }
}
