package application;

import java.util.Random;

/**
 * The User class represents a user entity in the system.
 */
public class User {
    private String userName;
    private String password;
    private String role;
    private int permID;  // Add permID field

    // Default constructor
    public User() {
        this.userName = "Guest";
        this.password = "";
        this.role = "user";
        this.permID = generateRandomPermID();  // Generate random permID
    }

    // Overloaded constructor
    public User(String userName, String password, String role) {
        this.userName = userName;
        this.password = password;
        this.role = role;
        this.permID = generateRandomPermID();  // Generate random permID
    }

    // Generate a random permID between 1 and 500,000,000
    private int generateRandomPermID() {
        Random random = new Random();
        return random.nextInt(500000000) + 1; // Random number between 1 and 500M
    }

    // Getter methods
    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public int getPermID() {
        return permID;
    }

    // Setter methods
    public void setRole(String role) {
        this.role = role;
    }

    public void setPermID(int permID) {
        this.permID = permID;
    }
}
