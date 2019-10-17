package com.example.demo;

import org.springframework.data.repository.CrudRepository;

import java.util.TreeSet;

public interface JobRepository extends CrudRepository<Job, Long> {
    TreeSet<Job> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(String search, String search2);
}
