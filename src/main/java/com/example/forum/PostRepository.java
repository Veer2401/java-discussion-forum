package com.example.forum;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PostRepository {
    private final Path filePath;

    public PostRepository(Path filePath) {
        this.filePath = filePath;
    }

    public synchronized void add(Post post) {
        List<Post> posts = FileStore.readList(filePath);
        List<Post> updated = new ArrayList<>(posts);
        updated.add(post);
        FileStore.writeList(filePath, updated);
    }

    public synchronized List<Post> findAllNewestFirst() {
        List<Post> posts = FileStore.readList(filePath);
        posts.sort(Comparator.comparingLong(Post::getTimestamp).reversed());
        return posts;
    }
}
