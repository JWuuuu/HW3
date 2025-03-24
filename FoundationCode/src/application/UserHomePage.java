package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * UserHomePage displays the home screen for a regular user.
 */
public class UserHomePage {
    private QuestionsManager questionsManager = QuestionsManager.getInstance();
    private DatabaseHelper databaseHelper = new DatabaseHelper();

    private User currentUser;

    // Overload or store user in constructor
    public UserHomePage(User currentUser) {
        this.currentUser = currentUser;
    }

    // For backward compatibility, we keep a default constructor
    public UserHomePage() {
        this.currentUser = new User("Guest", "", "user");
    }

    public void show(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        Label welcomeLabel = new Label("Welcome, " + currentUser.getUserName() + "!");
        welcomeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Ask Question
        Button askQuestionButton = new Button("Ask a Question");
        askQuestionButton.setOnAction(event -> {
            System.out.println("Navigating to AskQuestionPage...");
            // We'll pass the user to AskQuestionPage so we can store the owner
            new AskQuestionPage(currentUser).show(primaryStage);
        });

        // My Questions - for deleting or viewing user’s own questions
        Button myQuestionsButton = new Button("My Questions");
        myQuestionsButton.setOnAction(event -> {
            new MyQuestionsPage(currentUser).show(primaryStage);
        });

        // Role Selection
        Button roleSelectionButton = new Button("Change Role");
        roleSelectionButton.setOnAction(event -> {
            System.out.println("Navigating to RoleSelectionPage...");
            new RoleSelectionPage(databaseHelper, databaseHelper.getUserRoles(currentUser.getUserName()))
                    .show(primaryStage, currentUser);
        });

        // Logout
        Button logoutButton = new Button("Log Out");
        logoutButton.setOnAction(event -> {
            System.out.println("Logging out...");
            new StartCSE360().start(primaryStage);
        });
        
        
        Button backadmin = new Button("Back to Admin");
        backadmin.setOnAction(event -> new AdminHomePage().show(primaryStage));

        // Layout
        layout.getChildren().addAll(
            welcomeLabel,
            askQuestionButton,
            myQuestionsButton,
            roleSelectionButton,
            logoutButton,
            backadmin
        );

        Scene scene = new Scene(layout, 800, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("User Home Page");
        primaryStage.show();
    }
}