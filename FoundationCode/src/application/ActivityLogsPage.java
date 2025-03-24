package application;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * ActivityLogsPage displays a page with a list of user activity logs.
 */
public class ActivityLogsPage {

    /**
     * Displays the Activity Logs page in the provided primary stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        
        // Title label
        Label titleLabel = new Label("Activity Logs");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // ListView to display logs
        ListView<String> logsListView = new ListView<>();
        logsListView.getItems().addAll(
            "Log 1: John Smith - 23 minutes",
            "Log 2: Amy Birch - 45 minutes",
            "Log 3: Francy Prose - 2 hours",
            "Log 4: Katie Rose - 5 hours"
        );
        logsListView.setPrefSize(600, 300);
        
        // Automated Test: Verify Title
        System.out.println("Test: Verify Activity Logs Title - Expected: 'Activity Logs', Actual: " + titleLabel.getText());

        // Automated Test: Verify Number of Logs
        System.out.println("Test: Verify Number of Logs - Expected: 4, Actual: " + logsListView.getItems().size());

        // Back button to return to the UserHomePage
        Button backButton = new Button("Back");
        backButton.setOnAction(event -> {
            System.out.println("Test: Verify Back Button - Action Triggered");
            new AdminHomePage().show(primaryStage);
        });
        
        layout.getChildren().addAll(titleLabel, logsListView, backButton);
        
        Scene scene = new Scene(layout, 800, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Activity Logs");
        primaryStage.show();
    }
}