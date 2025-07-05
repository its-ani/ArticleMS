package com.example.articleservice.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class Author extends BaseModel{
    private String name;
    @OneToMany(mappedBy = "author", cascade = {CascadeType.REMOVE})
    @JsonIgnore
    List<Article> articles;
}
