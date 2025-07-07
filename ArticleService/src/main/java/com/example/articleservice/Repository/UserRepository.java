package com.example.articleservice.Repository;

import com.example.articleservice.Models.Author;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<Author, Long> {
    Author findByName(String name);
    Author save(Author author);
}
