package com.example.articleservice.Repository;
import com.example.articleservice.Models.Article;
import com.example.articleservice.Projections.ArticleProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ArticleRepository extends MongoRepository<Article, Long> {
//    @Query("select a.id , a.author.name , a.title , a.content , a.createdAt from Article a where a.id = :id and a.isDeleted = false")
//    @Query("select a.id as id, a.author.name as authorName, a.title as title, a.content as content, a.createdAt as createdAt " + "from Article a where a.id = :id and a.isDeleted = false")
    ArticleProjection findArticleById(String Id);

//    @Query("select a from Article a where a.id = :id and a.isDeleted = false")
    Article findById(String Id);
    Article  save(Article article);
//    @Query("select a.id , a.author.name , a.title , a.content , a.createdAt from Article a where a.isDeleted = false")
//    @Query("select a.id as id, a.author.name as authorName, a.title as title, a.content as content, a.createdAt as createdAt " + "from Article a where a.isDeleted = false")
    List<ArticleProjection> findAllArticleById();

//    @Query("select 1 from Article a where a.id = :id and a.isDeleted = false")
    boolean existsByIdAndIsDeletedFalse(String Id);
//    void deleteById(String Id);

//    @Query("select a.id as id, a.author.name as authorName, a.title as title, a.content as content, a.createdAt as createdAt " +
//            "from Article a where a.isDeleted = false")
    @Query(value = "{ 'isDeleted' : false }", fields = "{ 'id' : 1, 'title' : 1, 'content' : 1, 'createdAt' : 1, 'author.name' : 1 }")
    Page<ArticleProjection> findAllProjection(Pageable pageable);

//    int countAllById();
}
