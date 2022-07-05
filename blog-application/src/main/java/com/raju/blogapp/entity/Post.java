package com.raju.blogapp.entity;

import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.*;

import static javax.persistence.CascadeType.*;

@Component
@Entity
@Table(name = "posts")
public class Post {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    @Column(name="title")
    private String title;

    @Column(name="excerpt")
    private String excerpt;

    @Column(name="content")
    private String content;

    @Column(name="author")
    private String author;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="published_at")
    private Date publishedAt;

    @Column(name="is_published")
    private Boolean isPublished;

    @Column(name="created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name="updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    @OneToMany(targetEntity = Comment.class,cascade = ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id",referencedColumnName = "id")
    private List<Comment> comments;

    @ManyToMany(fetch = FetchType.LAZY,cascade = {DETACH, MERGE, PERSIST, REFRESH})
    @JoinTable(name="post_tags",
        joinColumns = {@JoinColumn(name="post_id")},
            inverseJoinColumns = {@JoinColumn(name="tag_id")}
    )
    private List<Tag> tags = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Date publishedAt) {
        this.publishedAt = publishedAt;
    }

    public Boolean getIsPublished() {
        return isPublished;
    }

    public void setIsPublished(Boolean published) {
        isPublished = published;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public void addComment(Comment comment){
        if(comment != null){
            if(comments == null){
                comments = new ArrayList<>();
            }
            comments.add(comment);
        }
    }
}
