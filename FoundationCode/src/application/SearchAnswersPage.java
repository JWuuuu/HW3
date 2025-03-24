package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * SearchAnswersPage displays a page with a list of mock answers.
 */
public class SearchAnswersPage {

    /**
     * Displays the Search Answers page in the provided primary stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        
        // Title label
        Label titleLabel = new Label("Search Answers");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Automated Test: Verify Page Title Label
        System.out.println("Test: Verify Page Title Label - Expected: 'Search Answers', Actual: " + titleLabel.getText());

        // ListView to display mock answers
        ListView<String> answersListView = new ListView<>();
        answersListView.getItems().addAll(
            "Answer 1: This is a mock answer for testing purposes.",
            "Answer 2: Another example of a mock answer.",
            "Answer 3: More mock answers can be added here.",
            "Answer 4: This is yet another mock answer."
        );
        answersListView.setPrefSize(600, 300);
        
        // Back button to return to the UserHomePage
        Button backButton = new Button("Back");
        backButton.setOnAction(event -> {
            new UserHomePage().show(primaryStage);
        });
        
        layout.getChildren().addAll(titleLabel, answersListView, backButton);
        
        Scene scene = new Scene(layout, 800, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Search Answers");
        primaryStage.show();
    }
}