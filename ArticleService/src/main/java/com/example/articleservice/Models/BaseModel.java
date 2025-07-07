package com.example.articleservice.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Getter
@Setter
public class BaseModel {
    @Id
    private String id;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isDeleted;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        setCreatedAt(now);
    }

    @PreUpdate
    protected void onUpdate() {
        setUpdatedAt(LocalDateTime.now());
    }

    public void setIsDeleted() {
        this.isDeleted = true;
    }
}

//public class BaseModel {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private String Id;
//    @Column(nullable = false, updatable = false)
//    private LocalDateTime createdAt;
//    @Column(nullable = true, updatable = true)
//    private LocalDateTime updatedAt;
//    private boolean isDeleted;
//
//    @PrePersist
//    protected void onCreate() {
//        LocalDateTime now = LocalDateTime.now();
//        setCreatedAt(now);
//    }
//
//
//    @PreUpdate
//    protected void onUpdate() {
//        setUpdatedAt(LocalDateTime.now());
//    }
//
//    public void setIsDeleted(){
//        this.isDeleted = true;
//    }
//}


