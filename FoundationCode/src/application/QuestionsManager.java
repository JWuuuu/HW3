package application;

import java.util.ArrayList;
import java.util.List;

/**
 * QuestionsManager handles storing and retrieving questions and answers.
 * Implements Singleton pattern to persist data throughout the application.
 */
public class QuestionsManager {
    private static QuestionsManager instance;
    private final List<Question> questions; // Stores all questions
    private final List<Answer> answers;     // Stores all answers

    private QuestionsManager() {
        this.questions = new ArrayList<>();
        this.answers = new ArrayList<>();
    }

    public static QuestionsManager getInstance() {
        if (instance == null) {
            instance = new QuestionsManager();
        }
        return instance;
    }

    /**
     * Adds a new question posted by an admin.
     */
    public void addQuestion(String title, String content) {
        addQuestion(title, content, "admin");
    }

    /**
     * Adds a new question posted by a specific user.
     */
    public void addQuestion(String title, String content, String owner) {
        if (title == null || title.isEmpty() || content == null || content.isEmpty()) {
            System.out.println("Error: Question must have a title and content.");
            return;
        }
        Question newQuestion = new Question(questions.size() + 1, title, content, owner);
        questions.add(newQuestion);
        System.out.println("Question added by " + owner + ": " + title);
    }

    /**
     * Adds a follow-up question linked to a parent question.
     */
    public void addFollowUpQuestion(int parentQuestionId, String newQuestionTitle, String content, String owner) {
        if (newQuestionTitle == null || newQuestionTitle.isEmpty() || content == null || content.isEmpty()) {
            System.out.println("Error: Follow-up question must have a title and content.");
            return;
        }

        Question parentQuestion = questions.stream()
            .filter(q -> q.getId() == parentQuestionId)
            .findFirst()
            .orElse(null);

        if (parentQuestion == null) {
            System.out.println("Error: Parent question ID " + parentQuestionId + " does not exist.");
            return;
        }

        String formattedTitle = parentQuestion.getTitle() + " : Follow-up - " + newQuestionTitle;
        Question followUp = new Question(questions.size() + 1, formattedTitle, content, owner, parentQuestionId);
        questions.add(followUp);
        System.out.println("Follow-up question added by " + owner + ": " + formattedTitle);
    }

    /**
     * Deletes a question (admin-only).
     */
    public void deleteQuestion(int questionId) {
        questions.removeIf(q -> q.getId() == questionId);
        System.out.println("Question with ID " + questionId + " has been deleted by admin.");
    }

    /**
     * Deletes a question if the user is the owner or an admin.
     */
    public boolean deleteQuestion(int questionId, User currentUser) {
        for (Question q : questions) {
            if (q.getId() == questionId) {
                if (q.getOwner().equalsIgnoreCase(currentUser.getUserName())
                        || currentUser.getRole().toLowerCase().contains("admin")) {
                    questions.remove(q);
                    System.out.println("Question ID " + questionId + " deleted by " + currentUser.getUserName());
                    return true;
                } else {
                    System.out.println("Permission denied. " + currentUser.getUserName()
                            + " is not the owner or an admin.");
                    return false;
                }
            }
        }
        System.out.println("Question ID " + questionId + " not found.");
        return false;
    }
    
    
    public boolean checkQuestionUpdate(int questionId, User currentUser) {
        if (questions.isEmpty()) {
            System.out.println("No questions available.");
            return false;
        }

        for (Question q : questions) {
            if (q.getId() == questionId) {
                // Check if the current user is the owner based on permID
                if (q.getOwnerPermID() == currentUser.getPermID()) {
                    System.out.println("Question ID " + questionId + " is owned by " + currentUser.getUserName());
                    return true; // User is the owner
                } else {
                    System.out.println("Permission denied. " + currentUser.getUserName()
                            + " is not the owner of the question.");
                    return false; // User is not the owner
                }
            }
        }
        System.out.println("Question ID " + questionId + " not found.");
        return false; // Question with the given ID not found
    }



    /**
     * Updates the title of a question.
     */
    public void updateQuestionTitle(int questionId, String newTitle) {
        for (Question q : questions) {
            if (q.getId() == questionId) {
                q.setTitle(newTitle);
                System.out.println("Question ID " + questionId + " title updated to: " + newTitle);
                return;
            }
        }
        System.out.println("Question ID " + questionId + " not found.");
    }

    /**
     * Adds an answer to a specific question.
     */
    public void addAnswer(int questionId, String answerText, String owner) {
        if (answerText == null || answerText.isEmpty()) {
            System.out.println("Error: Answer cannot be empty.");
            return;
        }
        Answer newAnswer = new Answer(answers.size() + 1, questionId, answerText, owner);
        answers.add(newAnswer);
        System.out.println("Answer added to Question ID " + questionId + ": " + answerText);
    }

    /**
     * Searches for questions based on a keyword.
     */
    public List<Question> searchQuestions(String keyword) {
        List<Question> filteredQuestions = new ArrayList<>();
        for (Question q : questions) {
            if (q.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                    q.getContent().toLowerCase().contains(keyword.toLowerCase())) {
                filteredQuestions.add(q);
            }
        }
        return filteredQuestions;
    }

    /**
     * Retrieves all stored questions.
     */
    public List<Question> getQuestions() {
        return questions;
    }

    /**
     * Retrieves all answers for a specific question.
     */
    public List<Answer> getAnswersForQuestion(int questionId) {
        List<Answer> filteredAnswers = new ArrayList<>();
        for (Answer a : answers) {
            if (a.getQuestionId() == questionId) {
                filteredAnswers.add(a);
            }
        }
        return filteredAnswers;
    }
}