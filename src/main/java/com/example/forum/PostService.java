package com.example.forum;

import java.util.List;

public class PostService {
    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public void createPost(String authorName, String content) {
        if (content == null || content.isBlank()) return;
        Post post = Post.createNew(authorName, content.trim());
        postRepository.save(post);
    }

    public List<Post> getTimeline() {
        return postRepository.findAllNewestFirst();
    }
}


