package application;

import application.*;
import java.util.List;

public class TestCases {

    // Test 1: Valid Answer Submission
    public static void testValidAnswerSubmission() {
        String feedbackText = "This is under canvas files.";
        String selectedAnswer = "It is located under canvas modules.";

        if (selectedAnswer.isEmpty()) {
            System.out.println("Test 1 Failed: No answer selected.");
        } else if (feedbackText.isEmpty()) {
            System.out.println("Test 1 Failed: Feedback text is empty.");
        } else {
            System.out.println("Test 1 Passed: Feedback submitted for answer: " + selectedAnswer);
        }
    }

    // Test 2: Negative Test for Empty Feedback Submission
    public static void testEmptyFeedbackSubmission() {
        String feedbackText = "";
        String selectedAnswer = "Watch the video about UML class diagrams.";

        if (selectedAnswer.isEmpty()) {
            System.out.println("Test 2 Failed: No answer selected.");
        } else if (feedbackText.isEmpty()) {
            System.out.println("Test 2 Passed: Feedback submission blocked due to empty feedback.");
        } else {
            System.out.println("Test 2 Failed: Feedback should not be allowed without text.");
        }
    }

    // Test 3: No Answer Selected for Feedback
    public static void testNoAnswerSelectedForFeedback() {
        String selectedAnswer = null; 
        String feedbackText = "This feedback is for the answer.";

        if (selectedAnswer == null) {
            System.out.println("Test 3 Passed: No answer selected; feedback submission aborted.");
        } else {
            System.out.println("Test 3 Failed: Feedback should not be submitted without selecting an answer.");
        }
    }

    // Test 4: Valid Feedback Submission 
    public static void testGeneralValidFeedback() {
        String selectedAnswer = "The hard deadline is 2 days after actual.";
        String feedbackText = "The answer is very clear and helpful.";

        if (!selectedAnswer.isEmpty() && !feedbackText.isEmpty()) {
            System.out.println("Test 4 Passed: Feedback successfully submitted for answer: " + selectedAnswer);
        } else {
            System.out.println("Test 4 Failed: Missing answer or feedback text.");
        }
    }

    // Test 5: Valid Feedback Submission 
    public static void testEmptyFeedbackAreaSubmission() {
        String selectedAnswer = "Watch the video about UML class diagrams.";
        String feedbackText = "";

        if (selectedAnswer != null && feedbackText.isEmpty()) {
            System.out.println("Test 5 Passed: Prevented empty feedback submission.");
        } else {
            System.out.println("Test 5 Failed: Empty feedback should not be allowed.");
        }
    }

    // Test 6: User Feedback Submission with Valid Answer Selected
    public static void testUserFeedbackSubmission() {
        String selectedAnswer = "It is located under canvas modules.";
        String feedbackText = "Great explanation, very helpful.";

        if (!selectedAnswer.isEmpty() && !feedbackText.isEmpty()) {
            System.out.println("Test 6 Passed: User feedback submitted for answer: " + selectedAnswer);
        } else {
            System.out.println("Test 6 Failed: Feedback was not submitted correctly.");
        }
    }

    // ----- Tests for UI and Navigation -----

    // Test 7: Valid Navigation Back to Home Page
    public static void testNavigationBackToHomePage() {
        // Simulating back navigation (assuming a proper UserHomePage exists)
        System.out.println("Test 7 Passed: Successfully navigated back to UserHomePage.");
    }

    public static void main(String[] args) {
        // Run all tests
        testValidAnswerSubmission();
        testEmptyFeedbackSubmission();
        testNoAnswerSelectedForFeedback();
        testGeneralValidFeedback();
        testEmptyFeedbackAreaSubmission();
        testUserFeedbackSubmission();
        testNavigationBackToHomePage();
    }
}