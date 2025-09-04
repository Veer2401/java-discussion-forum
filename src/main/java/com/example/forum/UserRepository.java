package com.example.forum;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository {
    private final String storagePath;
    private final List<User> users;

    public UserRepository(String storagePath) {
        this.storagePath = storagePath;
        this.users = new ArrayList<>(FileStore.readList(storagePath));
    }

    public synchronized Optional<User> findByUsername(String username) {
        return users.stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst();
    }

    public synchronized void save(User user) {
        users.removeIf(u -> u.getId().equals(user.getId()));
        users.add(user);
        persist();
    }

    public synchronized List<User> findAll() {
        return new ArrayList<>(users);
    }

    private void persist() {
        FileStore.writeList(storagePath, users);
    }
}


