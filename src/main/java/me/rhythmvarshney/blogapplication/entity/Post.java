package me.rhythmvarshney.blogapplication.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.util.*;

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

    @Column(name = "title")
    @NonNull
    private String postTitle;

    @Column(name = "excerpt")
    @NonNull
    private String excerpt;

    @Column(name = "content", columnDefinition = "text")
    @NonNull
    private String postContent;

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
    private Date updateTime;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {
            CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH
    })
    @JoinTable(name = "post_tags",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags;

    @ManyToOne(cascade = {
            CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH
    })
    @JoinColumn(name = "user_id")
    private User author;

    public void addComment(Comment comment){
        if(comments == null){
            comments = new ArrayList<>();
        }
        comment.setPost(this);
        comments.add(comment);
    }

    public void addTag(Tag tag){
        if(tags == null){
            tags = new HashSet<>();
        }

        tags.add(tag);
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", postTitle='" + postTitle + '\'' +
                ", excerpt='" + excerpt + '\'' +
                ", postContent='" + postContent + '\'' +
                ", author='" + author + '\'' +
                ", publishTime=" + publishTime +
                ", isPublished=" + isPublished +
                ", postCreateTime=" + postCreateTime +
                ", updateTime=" + updateTime +
                ", comments=" + comments +
                ", tags=" + tags +
                '}';
    }
}
