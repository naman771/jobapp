package com.jobportal.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "resumes")
public class Resume {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String fileUrl;

    @Lob
    private String parsedJson; // store parser JSON as text

    private LocalDateTime uploadedAt = LocalDateTime.now();

    // getters/setters
    public Long getId(){ return id; }
    public void setId(Long id){ this.id = id; }
    public Long getUserId(){ return userId; }
    public void setUserId(Long userId){ this.userId = userId; }
    public String getFileUrl(){ return fileUrl; }
    public void setFileUrl(String fileUrl){ this.fileUrl = fileUrl; }
    public String getParsedJson(){ return parsedJson; }
    public void setParsedJson(String parsedJson){ this.parsedJson = parsedJson; }
    public LocalDateTime getUploadedAt(){ return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt){ this.uploadedAt = uploadedAt; }
}
