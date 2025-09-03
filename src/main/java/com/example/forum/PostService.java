package com.example.forum;

import java.util.List;

public class PostService {
    private final PostRepository postRepository;
    private final AuthService authService;

    public PostService(PostRepository postRepository, AuthService authService) {
        this.postRepository = postRepository;
        this.authService = authService;
    }

    public synchronized void createPost(String content) {
        String trimmed = content == null ? "" : content.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Post content cannot be empty");
        }
        String author = authService.getCurrentUser()
                .orElseThrow(() -> new IllegalStateException("Not logged in"))
                .getUsername();
        Post post = Post.createNew(author, trimmed, System.currentTimeMillis());
        postRepository.add(post);
    }

    public synchronized List<Post> getTimeline() {
        return postRepository.findAllNewestFirst();
    }
}
