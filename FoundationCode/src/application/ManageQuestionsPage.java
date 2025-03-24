package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * ManageQuestionsPage allows admins to manage questions.
 */
public class ManageQuestionsPage {
    private final QuestionsManager questionsManager = QuestionsManager.getInstance();
    private final ObservableList<String> questionList = FXCollections.observableArrayList();
    private ListView<String> questionListView;

    public void show(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        Label titleLabel = new Label("Manage Questions");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextField searchField = new TextField();
        searchField.setPromptText("Search questions...");
        searchField.setOnKeyReleased(event -> updateQuestionList(searchField.getText()));

        questionListView = new ListView<>(questionList);
        updateQuestionList("");

        TextField questionTitleField = new TextField();
        questionTitleField.setPromptText("Enter question title");

        TextArea questionContentField = new TextArea();
        questionContentField.setPromptText("Enter question content");

        // Add Question Button
        Button addQuestionButton = new Button("Add Question");
        addQuestionButton.setOnAction(event -> {
            String title = questionTitleField.getText().trim();
            String content = questionContentField.getText().trim();
            if (title.isEmpty() || content.isEmpty()) {
                showAlert("Submission Error", "Both fields must be filled.", Alert.AlertType.ERROR);
                return;
            }
            questionsManager.addQuestion(title, content);
            updateQuestionList("");
            questionTitleField.clear();
            questionContentField.clear();
            showAlert("Success", "Question added successfully.", Alert.AlertType.INFORMATION);
        });

        // Delete Question Button
        Button deleteQuestionButton = new Button("Delete Question");
        deleteQuestionButton.setOnAction(event -> {
            String selected = questionListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                int questionId = Integer.parseInt(selected.split(": ")[0]);
                questionsManager.deleteQuestion(questionId);
                updateQuestionList("");
                showAlert("Success", "Question deleted successfully.", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Error", "No question selected.", Alert.AlertType.ERROR);
            }
        });

        // Update Question Button (Title)
        Button updateQuestionButton = new Button("Update Question");
        updateQuestionButton.setOnAction(event -> {
            String selected = questionListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                int questionId = Integer.parseInt(selected.split(": ")[0]);
                String newTitle = questionTitleField.getText().trim();
                if (!newTitle.isEmpty()) {
                    questionsManager.updateQuestionTitle(questionId, newTitle);
                    updateQuestionList("");
                    showAlert("Success", "Question title updated successfully.", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Error", "Title cannot be empty.", Alert.AlertType.ERROR);
                }
            } else {
                showAlert("Error", "No question selected.", Alert.AlertType.ERROR);
            }
        });

        // Back Button
        Button backButton = new Button("Back");
        backButton.setOnAction(event -> new AdminHomePage().show(primaryStage));

        layout.getChildren().addAll(titleLabel, searchField, questionListView, questionTitleField, questionContentField, 
                                    addQuestionButton, deleteQuestionButton, updateQuestionButton, backButton);

        Scene scene = new Scene(layout, 800, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Manage Questions");
        primaryStage.show();
    }

    private void updateQuestionList(String keyword) {
        questionList.clear();
        for (Question q : questionsManager.searchQuestions(keyword)) {
            questionList.add(q.getId() + ": " + q.getTitle());
        }
        questionListView.setItems(questionList);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}