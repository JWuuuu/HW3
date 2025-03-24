package application;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.util.List;
import databasePart1.*;

/**
 * The UserLoginPage class provides a login interface for users to access their accounts.
 * It validates the user's credentials and navigates to the appropriate page upon successful login.
 */
public class UserLoginPage {
    
    private final DatabaseHelper databaseHelper;
    
    public UserLoginPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
    
    public void show(Stage primaryStage) {
        // Label for username field
        Label userNameLabel = new Label("Username:");
        userNameLabel.setStyle("-fx-font-weight: bold;");
        
        // Input field for username
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter Username");
        userNameField.setMaxWidth(250);
        
        // Label for the password field
        Label passwordLabel = new Label("Password:");
        passwordLabel.setStyle("-fx-font-weight: bold;");
        
        // Hidden password field
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
                // Transfer password from PasswordField to TextField and swap visibility
                passwordVisibleField.setText(passwordField.getText());
                passwordVisibleField.setVisible(true);
                passwordField.setVisible(false);
            } else {
                // Transfer password back to PasswordField and swap visibility
                passwordField.setText(passwordVisibleField.getText());
                passwordField.setVisible(true);
                passwordVisibleField.setVisible(false);
            }
        });
        
        // Label to display error messages
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        
        // Login button to validate user credentials
        Button loginButton = new Button("Login");
        loginButton.setOnAction(a -> {
            // Retrieve user inputs
            String userName = userNameField.getText();
            // Get the correct password from whichever field is visible
            String password = showPasswordCheckBox.isSelected()
                    ? passwordVisibleField.getText()
                    : passwordField.getText();
            try {
                // Create user object with an empty role initially
                User user = new User(userName, password, "");
                
                // Retrieve the list of roles for the user using the new method
                List<String> roles = databaseHelper.getUserRoles(userName);
                if (roles != null && !roles.isEmpty()) {
                    // For the purpose of login validation, use the first role from the list.
                    user.setRole(roles.get(0));
                    
                    // Validate credentials using the login() method
                    if (databaseHelper.login(user)) {
                        // Navigate to the WelcomeLoginPage upon successful login.
                        // The WelcomeLoginPage will check if multiple roles exist and prompt for role selection.
                        new WelcomeLoginPage(databaseHelper).show(primaryStage, user);
                    } else {
                        // Display an error if login fails
                        errorLabel.setText("Error logging in");
                    }
                } else {
                    // Display an error if no roles are found (i.e., account doesn't exist)
                    errorLabel.setText("User account doesn't exist");
                }
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
            }
        });
        
        // Back button to navigate to AccountSetupPage
        Button backButton = new Button("Back");
        backButton.setOnAction(event -> {
            new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        });
        
        // Arrange UI elements in a vertical layout
        VBox layout = new VBox(10, 
                userNameLabel, userNameField, 
                passwordLabel, passwordField, passwordVisibleField, 
                showPasswordCheckBox, loginButton, errorLabel, backButton);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        
        // Set scene and show stage
        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("User Login");
        primaryStage.show();
    }
}