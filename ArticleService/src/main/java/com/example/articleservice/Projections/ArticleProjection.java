package com.example.articleservice.Projections;

import java.time.LocalDateTime;

public interface ArticleProjection {
    Long getId();
    String getAuthorName();
    String getTitle();
    String getContent();
    LocalDateTime getCreatedAt();
}