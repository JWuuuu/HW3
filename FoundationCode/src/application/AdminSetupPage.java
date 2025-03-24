package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.*;

/**
 * The SetupAdmin class handles the setup process for creating an administrator account.
 * This is intended to be used by the first user to initialize the system with admin credentials.
 */
public class AdminSetupPage {
	
    private final DatabaseHelper databaseHelper;

    public AdminSetupPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
    	// Label for username field
    	Label userNameLabel = new Label("Username:");
        userNameLabel.setStyle("-fx-font-weight: bold;");
        
    	// Input fields for userName and password
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter Admin userName");
        userNameField.setMaxWidth(250);

        // Label for the password field
        Label passwordLabel = new Label("Password:");
        passwordLabel.setStyle("-fx-font-weight: bold;");
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);

        // Visible password field (initially hidden)
        TextField passwordVisibleField = new TextField();
        passwordVisibleField.setPromptText("Enter Password");
        passwordVisibleField.setMaxWidth(250);
        passwordVisibleField.setVisible(false);
        
        // Checkbox to show or hide password
        CheckBox showPasswordCheckBox = new CheckBox("Show Password");
        showPasswordCheckBox.setOnAction(event -> {
            if (showPasswordCheckBox.isSelected()) {
                passwordVisibleField.setText(passwordField.getText());
                passwordVisibleField.setVisible(true);
                passwordField.setVisible(false);
                System.out.println("Test: Password is now visible.");
            } else {
                passwordField.setText(passwordVisibleField.getText());
                passwordField.setVisible(true);
                passwordVisibleField.setVisible(false);
                System.out.println("Test: Password is now hidden.");
            }
        });


        Button setupButton = new Button("Setup");
        
        setupButton.setOnAction(a -> {
        	// Retrieve user input
            String userName = userNameField.getText();
            String password = showPasswordCheckBox.isSelected() 
                    ? passwordVisibleField.getText() 
                    : passwordField.getText();
            
            // Validate the username
            String userNameError = UserNameRecognizer.checkForValidUserName(userName);

            // Validate the password
            String passwordError = PasswordEvaluator.evaluatePassword(password);
            
            // If the UserNameRecognizer or PasswordEvaluator returns an error, display it
            if (!userNameError.isEmpty()) {
                System.out.println("Test: Username validation failed - " + userNameError);
                showAlert("Invalid Username", userNameError);
                return;
            }
            if (!passwordError.isEmpty()) {
                System.out.println("Test: Password validation failed - " + passwordError);
                showAlert("Invalid Password", passwordError);
                return;
            }
            
            try {
            	// Create a new User object with admin role and register in the database
            	User user = new User(userName, password, "admin");
                databaseHelper.register(user);
                System.out.println("Test: Administrator setup completed successfully.");
                
                // Navigate to the Welcome Login Page
                System.out.println("Test: Navigating to the Welcome Login Page.");
                new WelcomeLoginPage(databaseHelper).show(primaryStage, user);
            } catch (SQLException e) {
                System.err.println("Test: Database error occurred - " + e.getMessage());
                e.printStackTrace();
            }
        });

        VBox layout = new VBox(10, userNameLabel, userNameField, passwordLabel, passwordField,
        		passwordVisibleField, showPasswordCheckBox, setupButton);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Administrator Setup");
        primaryStage.show();

        // Test: Scene Loaded
        System.out.println("Test: Admin Setup Page Loaded - Scene Title: " + primaryStage.getTitle());
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null); 
        alert.setContentText(message);
        alert.showAndWait();
    }
}