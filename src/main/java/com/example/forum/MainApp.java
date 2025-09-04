package com.example.forum;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MainApp extends Application {

    private AuthService authService;
    private PostService postService;

    private Stage primaryStage;

    private static final String USERS_FILE = "data/users.dat";
    private static final String POSTS_FILE = "data/posts.dat";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        UserRepository userRepository = new UserRepository(USERS_FILE);
        PostRepository postRepository = new PostRepository(POSTS_FILE);
        this.authService = new AuthService(userRepository);
        this.postService = new PostService(postRepository);

        stage.setTitle("Java Discussion Forum");
        stage.setScene(createAuthScene());
        stage.setWidth(640);
        stage.setHeight(480);
        stage.show();
    }

    private Scene createAuthScene() {
        Label title = new Label("Welcome - Login or Register");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Label message = new Label();

        Button loginBtn = new Button("Login");
        Button registerBtn = new Button("Register");

        loginBtn.setOnAction(e -> {
            boolean ok = authService.login(usernameField.getText().trim(), passwordField.getText());
            if (ok) {
                primaryStage.setScene(createFeedScene());
            } else {
                message.setText("Invalid credentials");
            }
        });

        registerBtn.setOnAction(e -> {
            boolean ok = authService.register(usernameField.getText().trim(), passwordField.getText());
            if (ok) {
                primaryStage.setScene(createFeedScene());
            } else {
                message.setText("Registration failed (username taken or invalid)");
            }
        });

        VBox form = new VBox(8, title, usernameField, passwordField, new HBox(8, loginBtn, registerBtn), message);
        form.setPadding(new Insets(16));

        return new Scene(form);
    }

    private Scene createFeedScene() {
        BorderPane root = new BorderPane();

        Label header = new Label();
        authService.getCurrentUser().ifPresent(u -> header.setText("Logged in as: " + u.getUsername()));

        Button logoutBtn = new Button("Logout");
        logoutBtn.setOnAction(e -> {
            authService.logout();
            primaryStage.setScene(createAuthScene());
        });

        HBox topBar = new HBox(10, header, logoutBtn);
        topBar.setPadding(new Insets(10));
        root.setTop(topBar);

        ListView<String> listView = new ListView<>();
        refreshPosts(listView);
        root.setCenter(listView);

        TextField newPostField = new TextField();
        newPostField.setPromptText("What's happening?");
        Button postBtn = new Button("Post");
        postBtn.setOnAction(e -> {
            String content = newPostField.getText();
            String author = authService.getCurrentUser().map(User::getUsername).orElse("anonymous");
            postService.createPost(author, content);
            newPostField.clear();
            refreshPosts(listView);
        });

        HBox composer = new HBox(8, newPostField, postBtn);
        composer.setPadding(new Insets(10));
        root.setBottom(composer);

        return new Scene(root);
    }

    private void refreshPosts(ListView<String> listView) {
        List<Post> posts = postService.getTimeline();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                .withZone(ZoneId.systemDefault());
        listView.getItems().setAll(posts.stream()
                .map(p -> String.format("%s  —  %s\n%s", p.getAuthorName(), fmt.format(p.getTimestamp()), p.getContent()))
                .toList());
    }
}


