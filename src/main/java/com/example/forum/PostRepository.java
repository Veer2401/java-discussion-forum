package com.example.forum;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PostRepository {
    private final String storagePath;
    private final List<Post> posts;

    public PostRepository(String storagePath) {
        this.storagePath = storagePath;
        this.posts = new ArrayList<>(FileStore.readList(storagePath));
    }

    public synchronized void save(Post post) {
        posts.removeIf(p -> p.getId().equals(post.getId()));
        posts.add(post);
        persist();
    }

    public synchronized List<Post> findAllNewestFirst() {
        List<Post> copy = new ArrayList<>(posts);
        copy.sort(Comparator.comparing(Post::getTimestamp).reversed());
        return copy;
    }

    private void persist() {
        FileStore.writeList(storagePath, posts);
    }
}


