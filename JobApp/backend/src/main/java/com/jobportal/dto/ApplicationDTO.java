package com.jobportal.dto;

import com.jobportal.model.Application;
import com.jobportal.model.Job;
import com.jobportal.model.Resume;
import com.jobportal.model.User;

public class ApplicationDTO {
    private Application application;
    private Resume resume;
    private User user;
    private Job job; // Add job info for candidate view

    public ApplicationDTO(Application application, Resume resume, User user) {
        this.application = application;
        this.resume = resume;
        this.user = user;
    }

    public ApplicationDTO(Application application, Resume resume, User user, Job job) {
        this.application = application;
        this.resume = resume;
        this.user = user;
        this.job = job;
    }

    public Application getApplication() { return application; }
    public void setApplication(Application application) { this.application = application; }

    public Resume getResume() { return resume; }
    public void setResume(Resume resume) { this.resume = resume; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Job getJob() { return job; }
    public void setJob(Job job) { this.job = job; }
}
