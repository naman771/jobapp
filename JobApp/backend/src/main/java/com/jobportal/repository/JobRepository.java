package com.jobportal.repository;

import com.jobportal.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JobRepository extends JpaRepository<Job, Long> {
    Page<Job> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String t, String d, Pageable p);
    java.util.List<Job> findByRecruiterId(Long recruiterId);
}
