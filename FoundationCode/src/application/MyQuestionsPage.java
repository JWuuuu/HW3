package application;

import java.util.List;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * MyQuestionsPage allows a user (student) to see and delete their own
 * questions.
 */
public class MyQuestionsPage {
	private final QuestionsManager questionsManager = QuestionsManager.getInstance();
	private final ObservableList<String> questionList = FXCollections.observableArrayList();
	private ListView<String> questionListView;

	private User currentUser;

	public MyQuestionsPage(User currentUser) {
		this.currentUser = currentUser;
	}

	public void show(Stage primaryStage) {
		VBox layout = new VBox(10);
		layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

		Label titleLabel = new Label("My Questions (Logged in as: " + currentUser.getUserName() + ")");
		titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

		questionListView = new ListView<>(questionList);
		updateQuestionList(); // load user’s questions

		Button deleteQuestionButton = new Button("Delete Selected Question");
		deleteQuestionButton.setOnAction(e -> {
			String selected = questionListView.getSelectionModel().getSelectedItem();
			if (selected != null) {
				int questionId = Integer.parseInt(selected.split(": ")[0]);
				// Use the ownership-based delete
				boolean success = questionsManager.deleteQuestion(questionId, currentUser);
				if (success) {
					updateQuestionList();
					showAlert("Success", "Question deleted successfully.", Alert.AlertType.INFORMATION);
				} else {
					showAlert("Error", "You are not allowed to delete this question.", Alert.AlertType.ERROR);
				}
			} else {
				showAlert("Error", "No question selected.", Alert.AlertType.ERROR);
			}
		});

		Button updateQuestionButton = new Button("Update Question Title");
		updateQuestionButton.setOnAction(e -> {
		    String selected = questionListView.getSelectionModel().getSelectedItem();
		    if (selected != null) {
		        int questionId = Integer.parseInt(selected.split(": ")[0]);

		        // Perform ownership check before allowing the update
		        boolean canUpdate = questionsManager.checkQuestionUpdate(questionId, currentUser);

		        if (!canUpdate) {
		            // Create a TextInputDialog to ask for a new title
		            TextInputDialog inputDialog = new TextInputDialog();
		            inputDialog.setTitle("Update Question Title");
		            inputDialog.setHeaderText("Enter a new title for the question:");
		            inputDialog.setContentText("New Title:");

		            // Show the dialog and get the result
		            Optional<String> newTitle = inputDialog.showAndWait();

		            newTitle.ifPresent(title -> {
		                // If user provided a title, update the question title
		                questionsManager.updateQuestionTitle(questionId, title);
		                updateQuestionList(); // Refresh the question list after the update
		                showAlert("Success", "Question title updated successfully.", Alert.AlertType.INFORMATION);
		            });

		        } else {
		            // If user cannot update the question, show an error
		            showAlert("Error", "You are not allowed to update this question.", Alert.AlertType.ERROR);
		        }
		    } else {
		        showAlert("Error", "No question selected.", Alert.AlertType.ERROR);
		    }
		});
		
		
		Button updateQuestionInfoButton = new Button("Update Question info");
		updateQuestionInfoButton.setOnAction(e -> {
		    showAlert("Error", "You are not allowed to update this question.", Alert.AlertType.ERROR);
		});

		
		 
		Button viewAnswersButton = new Button("View Answers");
		viewAnswersButton.setOnAction(event -> {
			String selectedQuestion = questionListView.getSelectionModel().getSelectedItem();
			if (selectedQuestion == null) {
				showAlert("Error", "Please select a question to view answers.", Alert.AlertType.ERROR);
				return;
			}
			int questionId = Integer.parseInt(selectedQuestion.split(": ")[0]);
			List<Answer> answers = questionsManager.getAnswersForQuestion(questionId);

			if (answers.isEmpty()) {
				showAlert("Info", "No answers available for this question.", Alert.AlertType.ERROR);
			} else {
				showAnswersPopup(answers);
			}
		});

		Button backButton = new Button("Back");
		backButton.setOnAction(e -> new UserHomePage(currentUser).show(primaryStage));

		layout.getChildren().addAll(titleLabel, questionListView, deleteQuestionButton, updateQuestionButton, updateQuestionInfoButton,
				viewAnswersButton, backButton);

		Scene scene = new Scene(layout, 800, 400);
		primaryStage.setScene(scene);
		primaryStage.setTitle("My Questions");
		primaryStage.show();
	}

	private void updateQuestionList() {
		questionList.clear();
		for (Question q : questionsManager.getQuestions()) {
			// only show questions owned by currentUser
			if (q.getOwner().equalsIgnoreCase(currentUser.getUserName())) {
				questionList.add(q.getId() + ": " + q.getTitle());
			}
		}
		questionListView.setItems(questionList);
	}

	/*
	 * show answers to selected question
	 */
	private void showAnswersPopup(List<Answer> answers) {
		Stage answerStage = new Stage();
		VBox layout = new VBox(10);
		layout.setPadding(new javafx.geometry.Insets(10));

		Label titleLabel = new Label("Answers for the Selected Question");
		ListView<String> answerListView = new ListView<>();
		ObservableList<String> answerList = FXCollections.observableArrayList();

		for (Answer a : answers) {
			answerList.add(a.getId() + ": " + a.getPinDisplayText());
		}
		answerListView.setItems(answerList);

		Button closeButton = new Button("Close");
		closeButton.setOnAction(e -> answerStage.close());

		/*
		 * pinAnswerButton toggles the pinStatus of the selected answer when clicked.
		 */
		Button pinAnswerButton = new Button("Pin Answer");
		pinAnswerButton.setOnAction(e -> {
			String selectedAnswer = answerListView.getSelectionModel().getSelectedItem();
			if (selectedAnswer == null) {
				showAlert("Error", "Please select an answer to pin.", Alert.AlertType.ERROR);
				return;
			}
			int answerId = Integer.parseInt(selectedAnswer.split(": ")[0]);
			for (Answer a : answers) {
				if (a.getId() == answerId) {
					a.togglePin();
					System.out.println("Answer ID " + answerId + " has been pinned.");
					return;
				}
			}
		});

		layout.getChildren().addAll(titleLabel, answerListView, closeButton, pinAnswerButton);
		Scene scene = new Scene(layout, 400, 300);
		answerStage.setScene(scene);
		answerStage.setTitle("Answers");
		answerStage.show();
	}

	private void showAlert(String title, String msg, Alert.AlertType type) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(msg);
		alert.showAndWait();
	}
}