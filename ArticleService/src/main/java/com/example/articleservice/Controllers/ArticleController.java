package com.example.articleservice.Controllers;

import com.example.articleservice.Dto.CreateArticleRequestDto;
import com.example.articleservice.Models.Article;
import com.example.articleservice.Projections.ArticleProjection;
import com.example.articleservice.Repository.ArticleRepository;
import com.example.articleservice.Services.ArticleService;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ArticleController {

    private final ArticleRepository articleserviceRepository;
    public ArticleService articleserviceService;
    public ArticleController(ArticleService articleserviceService, ArticleRepository articleserviceRepository){
        this.articleserviceService = articleserviceService;
        this.articleserviceRepository = articleserviceRepository;
    }

    @GetMapping("/articleservices")
    public ResponseEntity<List<ArticleProjection>> getAllArticles(){
        List<ArticleProjection> articleservices = articleserviceService.getAllArticles();
        ResponseEntity<List<ArticleProjection>> responseEntity = new ResponseEntity<>(articleservices , HttpStatus.OK);
        return responseEntity;
    }

    @GetMapping("/articleservices/{id}")
    public ResponseEntity<ArticleProjection> getSingleArticle(@PathVariable("id") long id){
        ArticleProjection articleservice = articleserviceService.getArticleById(id);
        ResponseEntity<ArticleProjection> responseEntity = new ResponseEntity<>(articleservice , HttpStatus.OK);
        return responseEntity;
    }

    @PostMapping("/articleservices")
    public ResponseEntity<ArticleProjection> createArticle(@RequestBody CreateArticleRequestDto createArticleRequestDto){
        ArticleProjection articleservice = articleserviceService.createArticle(createArticleRequestDto.getAuthor() , createArticleRequestDto.getTitle() , createArticleRequestDto.getContent());
        ResponseEntity<ArticleProjection> responseEntity = new ResponseEntity<>(articleservice , HttpStatus.CREATED);
        return responseEntity;
    }

    @DeleteMapping("/articleservices/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable("id") long id){
        ResponseEntity<Void> responseEntity = articleserviceService.deleteArticle(id);
        return responseEntity;
    }

    @GetMapping("/articleservices/page")
    public Pair<List<ArticleProjection> , Pair<Boolean , Integer>> getArticlePages(@RequestParam("pageNo") int pageNo , @RequestParam("pageSize") int pageSize){
        return articleserviceService.getAllArticles(pageNo , pageSize);
    }

    @PatchMapping("/articleservices/{id}")
    public ResponseEntity<Void> updateArticle(@PathVariable("id") long id , @RequestBody CreateArticleRequestDto createArticleRequestDto){
        ResponseEntity<Void> responseEntity = articleserviceService.updateArticle(id , createArticleRequestDto.getAuthor() , createArticleRequestDto.getTitle() , createArticleRequestDto.getContent());
        return responseEntity;
    }
}
