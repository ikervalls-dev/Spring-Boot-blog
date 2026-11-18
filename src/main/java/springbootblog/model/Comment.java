package springbootblog.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private String name;

    @Column(nullable = true)
    private String email;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String text;

    @Column(nullable = false)
    private LocalDateTime publishedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("publishedAt ASC")
    private List<Comment> replies = new ArrayList<>();

    public Comment() {
        this.publishedAt = LocalDateTime.now();
    }

    public Comment(String name, String email, String text, Post post) {
        this();
        this.name = name;
        this.email = email;
        this.text = text;
        this.post = post;
    }

    public Comment(User author, String text, Post post) {
        this();
        setAuthor(author);
        this.text = text;
        this.post = post;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        if (author != null) {
            return author.getName();
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        if (author != null) {
            return author.getEmail();
        }
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
        if (author != null) {
            this.name = author.getName();
            this.email = author.getEmail();
        }
    }

    public Comment getParent() {
        return parent;
    }

    public void setParent(Comment parent) {
        this.parent = parent;
    }

    public List<Comment> getReplies() {
        return replies;
    }

    public void setReplies(List<Comment> replies) {
        this.replies = replies;
    }

    public List<Comment> getAllReplies() {
        List<Comment> all = new ArrayList<>();
        for (Comment reply : replies) {
            all.add(reply);
            all.addAll(reply.getAllReplies());
        }
        all.sort(Comparator.comparing(Comment::getPublishedAt,
                Comparator.nullsLast(Comparator.naturalOrder())));
        return all;
    }

    public String getFormattedTime() {
        if (publishedAt == null) return "";
        return publishedAt.format(DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH));
    }

    public String getFormattedDate() {
        if (publishedAt == null) return "";
        return publishedAt.format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH));
    }
}

