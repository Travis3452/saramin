package com.example.saramin.repository;

import com.example.saramin.entity.model.Company;
import com.example.saramin.entity.model.JobPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JobPostRepository extends JpaRepository<JobPost, Long> {
    Optional<JobPost> findByTitleAndCompany(String title, Company company);
}
