package com.jobportal.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "jobs")
public class Job {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long recruiterId;

    private String title;
    private String company;

    @Column(columnDefinition = "text")
    private String description;

    private String location;
    private String salaryRange;
    private String jobType;

    @Column(columnDefinition = "text")
    private String skills; // csv of required skills

    private LocalDateTime createdAt = LocalDateTime.now();

    // getters/setters
    public Long getId(){ return id; }
    public void setId(Long id){ this.id = id; }
    public Long getRecruiterId(){ return recruiterId; }
    public void setRecruiterId(Long recruiterId){ this.recruiterId = recruiterId; }
    public String getTitle(){ return title; }
    public void setTitle(String title){ this.title = title; }
    public String getCompany(){ return company; }
    public void setCompany(String company){ this.company = company; }
    public String getDescription(){ return description; }
    public void setDescription(String description){ this.description = description; }
    public String getLocation(){ return location; }
    public void setLocation(String location){ this.location = location; }
    public String getSalaryRange(){ return salaryRange; }
    public void setSalaryRange(String salaryRange){ this.salaryRange = salaryRange; }
    public String getJobType(){ return jobType; }
    public void setJobType(String jobType){ this.jobType = jobType; }
    public String getSkills(){ return skills; }
    public void setSkills(String skills){ this.skills = skills; }
    public LocalDateTime getCreatedAt(){ return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt){ this.createdAt = createdAt; }
}
