package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.List;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;



/**
 * AskQuestionPage allows users to ask, answer, and follow up on questions.
 */
public class AskQuestionPage {
    private QuestionsManager questionsManager = QuestionsManager.getInstance();
    private ListView<String> questionListView;
    private ObservableList<String> questionList = FXCollections.observableArrayList();
    private User currentUser;
    
    // Added searchField instance variable for search functionality
    private TextField searchField;

    public AskQuestionPage(User currentUser) {
        this.currentUser = currentUser;
    }

    public void show(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        Label titleLabel = new Label("Ask or Answer Questions");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextField questionTitleField = new TextField();
        questionTitleField.setPromptText("Enter question title");

        TextArea questionContentField = new TextArea();
        questionContentField.setPromptText("Enter question content");

        Button submitQuestionButton = new Button("Submit Question");
        submitQuestionButton.setOnAction(event -> {
            String title = questionTitleField.getText().trim();
            String content = questionContentField.getText().trim();
            if (title.isEmpty() || content.isEmpty()) {
                showAlertError("Error", "Question must have a title and content.");
                return;
            }
            questionsManager.addQuestion(title, content, currentUser.getUserName());
            updateQuestionList();
            questionTitleField.clear();
            questionContentField.clear();
            showAlert("Success", "Question submitted successfully.");
        });

        Label questionListLabel = new Label("Select a question to answer or follow up:");
        questionListView = new ListView<>(questionList);
        updateQuestionList();
        
        searchField = new TextField();
        searchField.setPromptText("Search questions...");
        searchField.setOnKeyReleased(event -> filterQuestions());
        // Insert the search field into the layout without altering your existing controls.
        // This places the search field right before the question list label.
        layout.getChildren().add(searchField);


        TextField answerField = new TextField();
        answerField.setPromptText("Enter your answer");

        Button submitAnswerButton = new Button("Submit Answer");
        submitAnswerButton.setOnAction(event -> {
            String selectedQuestion = questionListView.getSelectionModel().getSelectedItem();
            if (selectedQuestion == null) {
                showAlertError("Error", "Please select a question to answer.");
                return;
            }
            int questionId = extractQuestionId(selectedQuestion);
            String answerText = answerField.getText().trim();
            if (answerText.isEmpty()) {
                showAlertError("Error", "Answer cannot be empty.");
                return;
            }
            questionsManager.addAnswer(questionId, answerText, currentUser.getUserName());
            answerField.clear();
            showAlert("Success", "Answer submitted successfully.");
        });

        Label followUpLabel = new Label("Follow-Up Question:");
        TextField followUpField = new TextField();
        followUpField.setPromptText("Enter follow-up question");

        Button followUpButton = new Button("Submit Follow-Up");
        followUpButton.setOnAction(event -> {
            String selectedQuestion = questionListView.getSelectionModel().getSelectedItem();
            if (selectedQuestion == null) {
                showAlertError("Error", "Please select a question to follow up on.");
                return;
            }
            int parentQuestionId = extractQuestionId(selectedQuestion);
            String followUpText = followUpField.getText().trim();
            if (followUpText.isEmpty()) {
                showAlertError("Error", "Follow-up question cannot be empty.");
                return;
            }

            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Follow-up Question");
            dialog.setHeaderText("Enter the follow-up question title:");
            dialog.setContentText("Title:");
            Optional<String> result = dialog.showAndWait();

            if (!result.isPresent() || result.get().trim().isEmpty()) {
                showAlertError("Error", "Follow-up question title cannot be empty.");
                return;
            }
            questionsManager.addFollowUpQuestion(parentQuestionId, result.get().trim(), followUpText, currentUser.getUserName());
            followUpField.clear();
            updateQuestionList();
            showAlert("Success", "Follow-up question submitted successfully.");
        });

        Button viewAnswersButton = new Button("View Answers");
        viewAnswersButton.setOnAction(event -> {
            String selectedQuestion = questionListView.getSelectionModel().getSelectedItem();
            if (selectedQuestion == null) {
                showAlertError("Error", "Please select a question to view answers.");
                return;
            }
            int questionId = extractQuestionId(selectedQuestion);
            List<Answer> answers = questionsManager.getAnswersForQuestion(questionId);
            if (answers.isEmpty()) {
                showAlert("Info", "No answers available for this question.");
            } else {
                showAnswersPopup(answers);
            }
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(event -> {
            if (currentUser.getRole().contains("admin")) {
                new AdminHomePage(currentUser).show(primaryStage);
            } else {
                new UserHomePage(currentUser).show(primaryStage);
            }
        });

        layout.getChildren().addAll(titleLabel, questionTitleField, questionContentField, submitQuestionButton,
                questionListLabel, questionListView, followUpLabel, followUpField,
                followUpButton, answerField, submitAnswerButton, viewAnswersButton, backButton);

        Scene scene = new Scene(layout, 800, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Ask a Question");
        primaryStage.show();
    }

    private void updateQuestionList() {
        questionList.clear();
        for (Question q : questionsManager.getQuestions()) {
            String displayText = q.getId() + ": " + q.getTitle();
            if (q.getParentQuestionId() != null) {
                displayText = "[Follow-up] " + displayText;
            }
            questionList.add(displayText);
        }
        questionListView.setItems(questionList);
    }

    private int extractQuestionId(String questionText) {
        String questionIdStr = questionText.split(": ")[0].replaceAll("[^0-9]", "");
        return questionIdStr.isEmpty() ? -1 : Integer.parseInt(questionIdStr);
    }
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private void showAlertError(String title, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    // filterQuestions applies search filtering when text is entered in the search field.
    private void filterQuestions() {
        String keyword = searchField.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            updateQuestionList();
            return;
        }
        ObservableList<String> filteredList = FXCollections.observableArrayList();
        for (Question q : questionsManager.getQuestions()) {
            if (q.getTitle().toLowerCase().contains(keyword) || q.getContent().toLowerCase().contains(keyword)) {
                String displayText = q.getId() + ": " + q.getTitle();
                if (q.getParentQuestionId() != null) {
                    displayText = "[Follow-up] " + displayText;
                }
                filteredList.add(displayText);
            }
        }
        questionListView.setItems(filteredList);
    }

    private void showAnswersPopup(List<Answer> answers) {
        Stage answerStage = new Stage();
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        Label titleLabel = new Label("Answers for the Selected Question");
        ListView<String> answerListView = new ListView<>();
        ObservableList<String> answerList = FXCollections.observableArrayList();
        for (Answer a : answers) {
            answerList.add(a.getId() + ": " + a.getPinDisplayText());
        }
        answerListView.setItems(answerList);
        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> answerStage.close());
        layout.getChildren().addAll(titleLabel, answerListView, closeButton);
        answerStage.setScene(new Scene(layout, 400, 300));
        answerStage.setTitle("Answers");
        answerStage.show();
    }
}