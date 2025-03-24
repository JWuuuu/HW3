package application;

import databasePart1.*;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * FirstPage class represents the initial screen for the first user.
 * It prompts the user to set up administrator access and navigate to the setup screen.
 */
public class FirstPage {
	
	// Reference to the DatabaseHelper for database interactions
	private final DatabaseHelper databaseHelper;
	
	public FirstPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

	/**
     * Displays the first page in the provided primary stage. 
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage) {
    	VBox layout = new VBox(5);
    	
    	// Label to display the welcome message for the first user
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    Label userLabel = new Label("Hello..You are the first person here. \nPlease select continue to setup administrator access");
	    userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

	    // Button to navigate to the SetupAdmin page
	    Button continueButton = new Button("Continue");
	    continueButton.setOnAction(a -> {
	        System.out.println("Test: Continue Button Clicked - Navigating to Admin Setup Page");
	        new AdminSetupPage(databaseHelper).show(primaryStage);
	    });

	    // Automated Test: Verify Button Text
	    System.out.println("Test: Verify Continue Button Text - Expected: 'Continue', Actual: " + continueButton.getText());

	    layout.getChildren().addAll(userLabel, continueButton);
	    Scene firstPageScene = new Scene(layout, 800, 400);

	    // Automated Test: Scene Loaded
	    System.out.println("Test: FirstPage Loaded - Scene Title: " + primaryStage.getTitle());

	    // Set the scene to primary stage
	    primaryStage.setScene(firstPageScene);
	    primaryStage.setTitle("First Page");
    	primaryStage.show();
    }
}