package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.application.Platform;
import databasePart1.*;

import java.util.List;

/**
 * The WelcomeLoginPage class displays a welcome screen for authenticated users.
 * It allows users to navigate to their respective pages based on their role or quit the application.
 * This updated version retrieves multiple roles and, if applicable, routes the user to a role selection screen.
 */
public class WelcomeLoginPage {
    
    private final DatabaseHelper databaseHelper;

    public WelcomeLoginPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
    
    public void show(Stage primaryStage, User user) {
        
        VBox layout = new VBox(5);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        
        Label welcomeLabel = new Label("Welcome!!");
        welcomeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Button to continue after login
        Button continueButton = new Button("Continue to your Page");
        continueButton.setOnAction(a -> {
            // Retrieve the list of roles for the user.
            // Note: You will need to implement getUserRoles(String userName) in DatabaseHelper.
            List<String> roles = databaseHelper.getUserRoles(user.getUserName());
            
            // If the user has multiple roles, send them to a role selection screen.
            if (roles.size() > 1) {
                new RoleSelectionPage(databaseHelper, roles).show(primaryStage, user);
            } 
            // Otherwise, set the single role and navigate accordingly.
            else if (roles.size() == 1) {
                String role = roles.get(0);
                user.setRole(role);
                System.out.println("Role: " + role);
                if (role.equals("admin")) {
                    // Updated to use the default constructor since AdminHomePage doesn't accept a DatabaseHelper.
                    new AdminHomePage().show(primaryStage);
                } else {
                    new UserHomePage().show(primaryStage);
                }
            } 
            // If no role is found, show an error alert.
            else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("No Role Found");
                alert.setHeaderText(null);
                alert.setContentText("No roles have been assigned to your account.");
                alert.showAndWait();
            }
        });
        
        // Button to quit the application
        Button quitButton = new Button("Quit");
        quitButton.setOnAction(a -> {
            databaseHelper.closeConnection();
            Platform.exit(); // Exit the JavaFX application
        });
        
        // "Invite" button for admin to generate invitation codes
        // (Shown only if the user has a single admin role)
        if ("admin".equals(user.getRole())) {
            Button inviteButton = new Button("Invite");
            inviteButton.setOnAction(a -> {
                new InvitationPage().show(databaseHelper, primaryStage);
            });
            layout.getChildren().add(inviteButton);
        }

        layout.getChildren().addAll(welcomeLabel, continueButton, quitButton);
        Scene welcomeScene = new Scene(layout, 800, 400);

        // Set the scene to primary stage
        primaryStage.setScene(welcomeScene);
        primaryStage.setTitle("Welcome Page");
    }
}