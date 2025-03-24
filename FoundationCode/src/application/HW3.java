package application;

/**
 * HW3.java
 *
 * A standalone automated test suite for Individual Homework 3 in CSE360.
 * This class includes five tests related to QuestionsManager methods.
 * Each test outputs its results to the console.
 *
 * Author: Yu-Hsiang Wu
 */
public class HW3 {

    /** Reset QuestionsManager by clearing all existing questions */
    public static void resetQuestionsManager() {
        QuestionsManager qm = QuestionsManager.getInstance();
        qm.getQuestions().clear();
    }

    /** Test 1: Deletes a question by admin */
    public static void testDeleteQuestionAdmin() {
        System.out.println("Test 1: Admin deleting a question...");
        resetQuestionsManager();
        QuestionsManager qm = QuestionsManager.getInstance();
        qm.addQuestion("Test Q1", "This is test content"); // default owner is 'admin'
        int before = qm.getQuestions().size();
        qm.deleteQuestion(1);
        int after = qm.getQuestions().size();
        System.out.println("Expected size: " + (before - 1) + ", Actual size: " + after);
        System.out.println(after == before - 1 ? "PASS" : "FAIL");
        System.out.println();
    }

    /** Test 2: Deletes a question if user is owner */
    public static void testDeleteQuestionByUser() {
        System.out.println("Test 2: User deleting their own question...");
        resetQuestionsManager();
        QuestionsManager qm = QuestionsManager.getInstance();
        User jack = new User("jack", "pass", "user");
        jack.setPermID(123); // simulate permID for ownership
        qm.addQuestion("User Q", "Owned content", jack.getUserName());
        qm.getQuestions().get(0).setOwnerPermID(123); // link permID to the question
        boolean result = qm.deleteQuestion(1, jack);
        System.out.println("Expected: true, Actual: " + result);
        System.out.println(result ? "PASS" : "FAIL");
        System.out.println();
    }

    /** Test 3: Update question title */
    public static void testUpdateQuestionTitle() {
        System.out.println("Test 3: Updating a question title...");
        resetQuestionsManager();
        QuestionsManager qm = QuestionsManager.getInstance();
        qm.addQuestion("Old Title", "Some content");
        qm.updateQuestionTitle(1, "New Title");
        String title = qm.getQuestions().get(0).getTitle();
        System.out.println("Expected: New Title, Actual: " + title);
        System.out.println("New Title".equals(title) ? "PASS" : "FAIL");
        System.out.println();
    }

    /** Test 4: Search question by keyword */
    public static void testSearchQuestions() {
        System.out.println("Test 4: Searching questions by keyword...");
        resetQuestionsManager();
        QuestionsManager qm = QuestionsManager.getInstance();
        qm.addQuestion("JavaFX Basics", "GUI toolkit");
        qm.addQuestion("Advanced JavaFX", "Layout and styling");
        int found = qm.searchQuestions("JavaFX").size();
        System.out.println("Expected: >= 2, Found: " + found);
        System.out.println(found >= 2 ? "PASS" : "FAIL");
        System.out.println();
    }

    /** Test 5: Retrieve all questions */
    public static void testGetAllQuestions() {
        System.out.println("Test 5: Retrieving all questions...");
        resetQuestionsManager();
        QuestionsManager qm = QuestionsManager.getInstance();
        qm.addQuestion("Question A", "Some A");
        qm.addQuestion("Question B", "Some B");
        int count = qm.getQuestions().size();
        System.out.println("Questions stored: " + count);
        System.out.println(count == 2 ? "PASS" : "FAIL");
        System.out.println();
    }

    /** Main method to run all five tests */
    public static void main(String[] args) {
        testDeleteQuestionAdmin();
        testDeleteQuestionByUser();
        testUpdateQuestionTitle();
        testSearchQuestions();
        testGetAllQuestions();
    }
}
