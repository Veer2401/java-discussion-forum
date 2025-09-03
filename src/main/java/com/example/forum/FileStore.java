package com.example.forum;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileStore {
    public static synchronized <T extends Serializable> List<T> readList(Path filePath) {
        try {
            if (!Files.exists(filePath)) {
                createParentDirectories(filePath);
                return new ArrayList<>();
            }
            try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(Files.newInputStream(filePath)))) {
                Object obj = in.readObject();
                @SuppressWarnings("unchecked")
                List<T> list = (List<T>) obj;
                return new ArrayList<>(list);
            }
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static synchronized <T extends Serializable> void writeList(Path filePath, List<T> list) {
        try {
            createParentDirectories(filePath);
            try (ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(Files.newOutputStream(filePath)))) {
                out.writeObject(new ArrayList<>(list));
                out.flush();
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void createParentDirectories(Path filePath) throws IOException {
        Path parent = filePath.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }
    }
}
