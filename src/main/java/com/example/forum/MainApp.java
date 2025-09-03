package com.example.forum;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainApp extends Application {

    private AuthService authService;
    private PostService postService;

    private ListView<Post> postsListView;
    private Label loggedInAsLabel;

    @Override
    public void start(Stage primaryStage) {
        Path dataDir = Paths.get("data");
        try {
            Files.createDirectories(dataDir);
        } catch (Exception e) {
            e.printStackTrace();
        }

        UserRepository userRepository = new UserRepository(dataDir.resolve("users.dat"));
        PostRepository postRepository = new PostRepository(dataDir.resolve("posts.dat"));
        this.authService = new AuthService(userRepository);
        this.postService = new PostService(postRepository, authService);

        Scene loginScene = createLoginScene(primaryStage);
        primaryStage.setTitle("Java Discussion Forum");
        primaryStage.setScene(loginScene);
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.show();
    }

    private Scene createLoginScene(Stage stage) {
        Label title = new Label("Welcome to the Forum");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginButton = new Button("Login");
        Button registerButton = new Button("Register");

        Label messageLabel = new Label();

        HBox buttons = new HBox(10, loginButton, registerButton);
        buttons.setAlignment(Pos.CENTER);

        VBox content = new VBox(10, title, usernameField, passwordField, buttons, messageLabel);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(20));
        content.setPrefWidth(400);

        loginButton.setOnAction(evt -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            try {
                authService.login(username, password);
                stage.setScene(createFeedScene(stage));
            } catch (IllegalArgumentException ex) {
                showError("Login failed", ex.getMessage());
            }
        });

        registerButton.setOnAction(evt -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            try {
                authService.register(username, password);
                showInfo("Registration successful", "You can now log in.");
            } catch (IllegalArgumentException ex) {
                showError("Registration failed", ex.getMessage());
            }
        });

        BorderPane root = new BorderPane();
        root.setCenter(content);
        return new Scene(root);
    }

    private Scene createFeedScene(Stage stage) {
        postsListView = new ListView<>();
        postsListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Post post, boolean empty) {
                super.updateItem(post, empty);
                if (empty || post == null) {
                    setText(null);
                } else {
                    String dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(post.getTimestamp()));
                    setText(post.getAuthorName() + "  (" + dateStr + ")\n" + post.getContent());
                }
            }
        });

        loggedInAsLabel = new Label();
        authService.getCurrentUser().ifPresent(user -> loggedInAsLabel.setText("Logged in as: " + user.getUsername()));

        TextField newPostField = new TextField();
        newPostField.setPromptText("What's happening?");
        Button postButton = new Button("Post");
        Button logoutButton = new Button("Logout");

        postButton.setOnAction(evt -> {
            String content = newPostField.getText().trim();
            if (content.isEmpty()) {
                showError("Empty post", "Please enter some text.");
                return;
            }
            try {
                postService.createPost(content);
                newPostField.clear();
                refreshPosts();
            } catch (IllegalStateException ex) {
                showError("Not logged in", ex.getMessage());
            }
        });

        logoutButton.setOnAction(evt -> {
            authService.logout();
            stage.setScene(createLoginScene(stage));
        });

        HBox inputRow = new HBox(10, newPostField, postButton, logoutButton);
        inputRow.setAlignment(Pos.CENTER_LEFT);
        inputRow.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setTop(new VBox(new Label("Feed"), loggedInAsLabel));
        root.setCenter(postsListView);
        root.setBottom(inputRow);
        BorderPane.setMargin(root.getTop(), new Insets(10));
        BorderPane.setMargin(postsListView, new Insets(10));

        refreshPosts();
        return new Scene(root);
    }

    private void refreshPosts() {
        List<Post> posts = postService.getTimeline();
        postsListView.getItems().setAll(posts);
    }

    private void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showInfo(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
