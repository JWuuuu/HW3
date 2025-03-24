package application;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * ManageUsersPage displays a page with a list of users.
 */
public class ManageUsersPage {

    /**
     * Displays the Manage Users page in the provided primary stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        
        // Title label
        Label titleLabel = new Label("Manage Users");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // ListView to display users
        ListView<String> usersListView = new ListView<>();
        usersListView.getItems().addAll(
            "User 1: John Smith",
            "User 2: Amy Birch",
            "User 3: Francy Prose",
            "User 4: Katie Rose"
        );
        usersListView.setPrefSize(600, 300);
        
        // Back button to return to the UserHomePage
        Button backButton = new Button("Back");
        backButton.setOnAction(event -> {
            new AdminHomePage().show(primaryStage);
        });
        
        layout.getChildren().addAll(titleLabel, usersListView, backButton);
        
        Scene scene = new Scene(layout, 800, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Manage Users");
        primaryStage.show();
        
        // Automated Test: Scene Loaded
        System.out.println("Test: InvitationPage Loaded - Scene Title: " + primaryStage.getTitle());
    }
}