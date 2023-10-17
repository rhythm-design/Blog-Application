package me.rhythmvarshney.blogapplication.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "posts")
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class Post {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "title", unique = true)
    @NonNull
    private String postTitle;

    @Column(name = "excerpt")
    @NonNull
    private String excerpt;

    @Column(name = "content", columnDefinition = "text")
    @NonNull
    private String postContent;

    @Column(name = "author")
    @NonNull
    private String author;

    @Column(name = "published_at")
    @NonNull
    private Date publishTime;

    @Column(name = "is_published")
    @NonNull
    private boolean isPublished;

    @Column(name = "created_at")
    @NonNull
    private Date postCreateTime;

    @Column(name = "updated_at")
    @NonNull
    private Date updateTime;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments;

    public void addComment(Comment comment){
        if(comments == null){
            comments = new ArrayList<>();
        }
        comment.setPost(this);
        comments.add(comment);
    }
//    public void addCourse(Course course){
//        if(courses == null){
//            courses = new ArrayList<>();
//        }
//
//        course.setInstructor(this);
//        courses.add(course);
//    }
}
