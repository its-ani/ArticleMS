package com.example.articleservice.Repository;
import com.example.articleservice.Models.Article;
import com.example.articleservice.Projections.ArticleProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
//    @Query("select a.id , a.author.name , a.title , a.content , a.createdAt from Article a where a.id = :id and a.isDeleted = false")
    @Query("select a.id as id, a.author.name as authorName, a.title as title, a.content as content, a.createdAt as createdAt " + "from Article a where a.id = :id and a.isDeleted = false")
    ArticleProjection findProjectedById(long id);

    @Query("select a from Article a where a.id = :id and a.isDeleted = false")
    Article findById(long id);
    Article  save(Article article);
//    @Query("select a.id , a.author.name , a.title , a.content , a.createdAt from Article a where a.isDeleted = false")
    @Query("select a.id as id, a.author.name as authorName, a.title as title, a.content as content, a.createdAt as createdAt " + "from Article a where a.isDeleted = false")
    List<ArticleProjection> findAllProjectedBy();

//    @Query("select 1 from Article a where a.id = :id and a.isDeleted = false")
    boolean existsByIdAndIsDeletedFalse(long id);
//    void deleteById(long id);

    @Query("select a.id as id, a.author.name as authorName, a.title as title, a.content as content, a.createdAt as createdAt " +
            "from Article a where a.isDeleted = false")
    Page<ArticleProjection> findAllProjection(Pageable pageable);

//    int countAllById();
}
