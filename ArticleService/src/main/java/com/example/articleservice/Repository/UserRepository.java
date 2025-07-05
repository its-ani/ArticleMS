package com.example.articleservice.Repository;

import com.example.articleservice.Models.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Author, Long> {
    Author findByName(String name);
    Author save(Author author);
}
