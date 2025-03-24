package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RegisterPage {
    private final DatabaseHelper databaseHelper;

    public RegisterPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
        Label titleLabel = new Label("Register a New User");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter Username");

        PasswordField passwordField = new PasswordField();
        TextField passwordVisibleField = new TextField();
        passwordField.setPromptText("Enter Password");
        passwordVisibleField.setPromptText("Enter Password");
        passwordVisibleField.setManaged(false);
        passwordVisibleField.setVisible(false);

        CheckBox showPasswordCheckBox = new CheckBox("Show Password");

        // Sync password fields
        passwordField.textProperty().bindBidirectional(passwordVisibleField.textProperty());

        showPasswordCheckBox.selectedProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                passwordVisibleField.setManaged(true);
                passwordVisibleField.setVisible(true);
                passwordField.setManaged(false);
                passwordField.setVisible(false);
            } else {
                passwordVisibleField.setManaged(false);
                passwordVisibleField.setVisible(false);
                passwordField.setManaged(true);
                passwordField.setVisible(true);
            }
        });

        Button registerButton = new Button("Register");
        Label statusLabel = new Label();

        registerButton.setOnAction(a -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            // Validation Rules
            if (!isValidUsername(username)) {
                statusLabel.setText("Invalid username! Must start with a letter and can include '-', '_', and '.' between alphanumeric characters.");
                return;
            }

            if (!isValidPassword(password)) {
                statusLabel.setText("Invalid password! Must be at least 8 characters long, contain at least one uppercase letter, one number, and one special character.");
                return;
            }

            if (!username.isEmpty() && !password.isEmpty()) {
                try {
                    databaseHelper.connectToDatabase();
                    if (databaseHelper.doesUserExist(username)) {
                        statusLabel.setText("Username already exists! Choose another.");
                    } else {
                        databaseHelper.register(new User(username, password, "user"));
                        statusLabel.setText("User registered successfully!");
                    }
                } catch (Exception e) {
                    statusLabel.setText("Error registering user.");
                    e.printStackTrace();
                }
            } else {
                statusLabel.setText("Please enter a username and password.");
            }
        });

        VBox layout = new VBox(10, titleLabel, usernameField, passwordField, passwordVisibleField, showPasswordCheckBox, registerButton, statusLabel);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Register Page");
        primaryStage.show();
    }

    // Validate username (must start with a letter, can contain alphanumeric characters, '-', '_', and '.')
    private boolean isValidUsername(String username) {
        return username.matches("^[A-Za-z][A-Za-z0-9._-]*$");
    }

    // Validate password (at least 8 chars, one uppercase, one number, one special character)
    private boolean isValidPassword(String password) {
        return password.matches("^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$");
    }
}