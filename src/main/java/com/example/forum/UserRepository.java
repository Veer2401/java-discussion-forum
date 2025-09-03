package com.example.forum;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository {
    private final Path filePath;

    public UserRepository(Path filePath) {
        this.filePath = filePath;
    }

    public synchronized Optional<User> findByUsername(String username) {
        return getAllInternal().stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst();
    }

    public synchronized void save(User user) {
        List<User> users = getAllInternal();
        // replace if exists by id
        List<User> updated = new ArrayList<>();
        boolean replaced = false;
        for (User u : users) {
            if (u.getId().equals(user.getId())) {
                updated.add(user);
                replaced = true;
            } else {
                updated.add(u);
            }
        }
        if (!replaced) {
            updated.add(user);
        }
        FileStore.writeList(filePath, updated);
    }

    public synchronized List<User> getAll() {
        return getAllInternal();
    }

    private List<User> getAllInternal() {
        return FileStore.readList(filePath);
    }
}
