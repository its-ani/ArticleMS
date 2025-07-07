package com.example.articleservice.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@Document
public class Author extends BaseModel{
    private String name;
    @OneToMany(mappedBy = "author", cascade = {CascadeType.REMOVE})
    @JsonIgnore
    List<Article> articles;
}
