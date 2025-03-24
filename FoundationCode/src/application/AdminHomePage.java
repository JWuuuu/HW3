package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * AdminHomePage displays the home screen for admin users.
 */
public class AdminHomePage {

    private QuestionsManager questionsManager = QuestionsManager.getInstance();
    private User currentUser; // The admin user object

    // Constructor that accepts the logged-in user
    public AdminHomePage(User currentUser) {
        this.currentUser = currentUser;
    }

    // If you still need a default constructor for older code
    public AdminHomePage() {
        // Hard-code an admin user if needed
        this.currentUser = new User("admin", "", "admin");
    }

    public void show(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        Label adminLabel = new Label("Hello, Admin!");
        adminLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Manage Users
        Button manageUsersButton = new Button("Manage Users");
        manageUsersButton.setOnAction(event -> new ManageUsersPage().show(primaryStage));

        // View Activity Logs
        Button activityLogsButton = new Button("View Activity Logs");
        activityLogsButton.setOnAction(event -> new ActivityLogsPage().show(primaryStage));

        // Manage Questions
        Button manageQuestionsButton = new Button("Manage Questions");
        manageQuestionsButton.setOnAction(event -> new ManageQuestionsPage().show(primaryStage));
        
        //
        Button backuser = new Button("Back to User");
        backuser.setOnAction(event -> new UserHomePage().show(primaryStage));


        // Ask a Question
        Button askQuestionButton = new Button("Ask a Question");
        askQuestionButton.setOnAction(event -> {
            System.out.println("DEBUG: Admin clicked 'Ask a Question'");
            new AskQuestionPage(currentUser).show(primaryStage);
        });

        // Change Role Button
        Button roleSelectionButton = new Button("Change Role");
        roleSelectionButton.setOnAction(event -> {
            System.out.println("DEBUG: Admin clicked 'Change Role'");

            DatabaseHelper dbHelper = new DatabaseHelper();
            // Trim the userName to remove leading/trailing spaces
            String rawName = currentUser.getUserName();
            String userName = (rawName == null ? "" : rawName.trim());

            System.out.println("DEBUG: Checking roles for userName='" + userName + "'");

            // 1) ensure userName is in DB, if not -> auto register as "admin"
            boolean ok = dbHelper.ensureUserExists(userName, "admin");
            if (!ok) {
                System.out.println("ERROR: Could not ensure user= " + userName + " is in DB.");
                return;
            }

            // 2) fetch roles
            java.util.List<String> userRoles = dbHelper.getUserRoles(userName);
            System.out.println("DEBUG: userRoles= " + userRoles);

            // 3) pass to RoleSelectionPage
            User trimmedUser = new User(userName, currentUser.getPassword(), currentUser.getRole());
            new RoleSelectionPage(dbHelper, userRoles).show(primaryStage, trimmedUser);
        });

        // Logout
        Button logoutButton = new Button("Log Out");
        logoutButton.setOnAction(event -> {
            System.out.println("DEBUG: Admin clicked 'Log Out'");
            new StartCSE360().start(primaryStage);
        });

        layout.getChildren().addAll(
            adminLabel,
            manageUsersButton,
            activityLogsButton,
            manageQuestionsButton,
            askQuestionButton,
            roleSelectionButton,
            logoutButton,
            backuser
        );

        Scene scene = new Scene(layout, 800, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Admin Home Page");
        primaryStage.show();

        // Debug info
        System.out.println("DEBUG: AdminHomePage loaded with userName='"
            + currentUser.getUserName()
            + "', role='"
            + currentUser.getRole()
            + "'");
    }
}