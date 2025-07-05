package com.example.articleservice.Services;

import com.example.articleservice.Models.Author;
import com.example.articleservice.Projections.ArticleProjection;
import org.springframework.http.ResponseEntity;
import org.antlr.v4.runtime.misc.Pair;

import java.util.List;

public interface ArticleService {
    List<ArticleProjection> getAllArticles();
    ArticleProjection getArticleById(long id);
    ArticleProjection createArticle(Author author, String title, String content);
    ResponseEntity<Void> deleteArticle(long id);
    Pair<List<ArticleProjection> , Pair<Boolean , Integer>> getAllArticles(int pageNo , int pageSize);
    ResponseEntity<Void> updateArticle(long id , Author author , String title , String content);
}
