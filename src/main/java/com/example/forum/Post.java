package com.example.forum;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Post implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String authorName;
    private final String content;
    private final Instant timestamp;

    public Post(String id, String authorName, String content, Instant timestamp) {
        this.id = id;
        this.authorName = authorName;
        this.content = content;
        this.timestamp = timestamp;
    }

    public static Post createNew(String authorName, String content) {
        return new Post(UUID.randomUUID().toString(), authorName, content, Instant.now());
    }

    public String getId() {
        return id;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getContent() {
        return content;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return Objects.equals(id, post.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}


