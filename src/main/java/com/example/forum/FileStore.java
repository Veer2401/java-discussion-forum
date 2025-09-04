package com.example.forum;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileStore {

    public static <T extends Serializable> List<T> readList(String filePath) {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(Files.newInputStream(path)))) {
            Object obj = ois.readObject();
            @SuppressWarnings("unchecked")
            List<T> list = (List<T>) obj;
            return new ArrayList<>(list);
        } catch (EOFException e) {
            return new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static <T extends Serializable> void writeList(String filePath, List<T> items) {
        try {
            Path path = Paths.get(filePath);
            Path parent = path.getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }
            try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(Files.newOutputStream(path)))) {
                oos.writeObject(new ArrayList<>(items));
                oos.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


