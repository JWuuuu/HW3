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
import java.util.ArrayList;
import java.util.List;

import databasePart1.*;

/**
 * SetupAccountPage class handles the account setup process for new users.
 * Users provide their username, password, and a valid invitation code to register.
 * This updated version allows the user to select multiple roles via checkboxes.
 */
public class SetupAccountPage {
    
    private final DatabaseHelper databaseHelper;
    
    /**
     * Constructor for SetupAccountPage.
     * @param databaseHelper The database helper instance for handling database operations.
     */
    public SetupAccountPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * Displays the Setup Account page in the provided stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage) {
        // Label for username field
        Label userNameLabel = new Label("Username:");
        userNameLabel.setStyle("-fx-font-weight: bold;");
        
        // Input field for username
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter Username");
        userNameField.setMaxWidth(250);

        // Label for password field
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
        
        // Checkbox to toggle password visibility
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

        // Label for invitation code field
        Label inviteCodeLabel = new Label("Invitation Code:");
        inviteCodeLabel.setStyle("-fx-font-weight: bold;");
        
        // Input field for invitation code
        TextField inviteCodeField = new TextField();
        inviteCodeField.setPromptText("Enter Invitation Code");
        inviteCodeField.setMaxWidth(250);
        
        // Label for role selection
        Label rolesLabel = new Label("Select Roles:");
        rolesLabel.setStyle("-fx-font-weight: bold;");
        
        // Create checkboxes for each role option
        CheckBox userRoleCheckBox = new CheckBox("User");
        CheckBox reviewerRoleCheckBox = new CheckBox("Reviewer");
        CheckBox instructorRoleCheckBox = new CheckBox("Instructor");
        // You can add more role options if needed, e.g. staff, etc.

        // Label to display error messages for invalid input or registration issues
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        
        // Setup button to create a new account
        Button setupButton = new Button("Setup");
        setupButton.setOnAction(a -> {
            // Retrieve user input
            String userName = userNameField.getText();
            // Get the correct password from whichever field is visible
            String password = showPasswordCheckBox.isSelected() 
                    ? passwordVisibleField.getText() 
                    : passwordField.getText();
            String code = inviteCodeField.getText();
            
            // Build a list of selected roles
            List<String> selectedRoles = new ArrayList<>();
            if (userRoleCheckBox.isSelected()) {
                selectedRoles.add("user");
            }
            if (reviewerRoleCheckBox.isSelected()) {
                selectedRoles.add("reviewer");
            }
            if (instructorRoleCheckBox.isSelected()) {
                selectedRoles.add("instructor");
            }
            
            // Check that at least one role is selected
            if (selectedRoles.isEmpty()) {
                errorLabel.setText("Please select at least one role.");
                return;
            }
            
            // Join the selected roles into a comma-separated string
            String rolesString = String.join(",", selectedRoles);
            
            try {
                // Check if the user already exists
                if (!databaseHelper.doesUserExist(userName)) {
                    // Validate the invitation code
                    if (databaseHelper.validateInvitationCode(code)) {
                        // Create a new user with the selected roles and register them in the database
                        User user = new User(userName, password, rolesString);
                        databaseHelper.register(user);
                        
                        // Navigate to the Welcome Login Page
                        new WelcomeLoginPage(databaseHelper).show(primaryStage, user);
                    } else {
                        errorLabel.setText("Please enter a valid invitation code");
                    }
                } else {
                    errorLabel.setText("This username is taken! Please use another to set up an account.");
                }
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
            }
        });
        
        // Back button to return to the previous screen (Setup/Login Selection)
        Button backButton = new Button("Back");
        backButton.setOnAction(event -> {
            System.out.println("Test: Back Button Clicked - Navigating to SetupLoginSelectionPage");
            new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        });

        // Arrange UI elements in a vertical layout
        VBox layout = new VBox(10, 
                userNameLabel, userNameField, 
                passwordLabel, passwordField, passwordVisibleField, showPasswordCheckBox, 
                inviteCodeLabel, inviteCodeField,
                rolesLabel, userRoleCheckBox, reviewerRoleCheckBox, instructorRoleCheckBox,
                setupButton, errorLabel, backButton);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        // Set scene and show stage
        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Account Setup");
        primaryStage.show();
    }
}