package databasePart1;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import application.User;

/**
 * The DatabaseHelper class manages the connection to the database,
 * performing operations such as user registration, login validation,
 * role updates, invitation codes, etc.
 */
public class DatabaseHelper {

    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "org.h2.Driver";
    static final String DB_URL = "jdbc:h2:~/FoundationDatabase";

    // Database credentials
    static final String USER = "sa";
    static final String PASS = "";

    private Connection connection = null;
    private Statement statement = null;

    // Singleton instance
    private static DatabaseHelper instance;
    public static DatabaseHelper getInstance() {
        if (instance == null) {
            instance = new DatabaseHelper();
        }
        return instance;
    }

    public DatabaseHelper() {
        try {
            connectToDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // -----------------------------------------------------------------------------------
    // Database Setup
    // -----------------------------------------------------------------------------------

    public void connectToDatabase() throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName(JDBC_DRIVER);
                System.out.println("Connecting to database...");
                connection = DriverManager.getConnection(DB_URL, USER, PASS);
                statement = connection.createStatement();
                createTables();
            }
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found: " + e.getMessage());
        }
    }

    private void createTables() throws SQLException {
        // Main user table
        String userTable = "CREATE TABLE IF NOT EXISTS cse360users ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "userName VARCHAR(255) UNIQUE, "
                + "password VARCHAR(255), "
                + "role VARCHAR(255))";
        statement.execute(userTable);

        // Invitation codes table
        String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
                + "code VARCHAR(10) PRIMARY KEY, "
                + "isUsed BOOLEAN DEFAULT FALSE)";
        statement.execute(invitationCodesTable);
    }

    // -----------------------------------------------------------------------------------
    // Basic Registration / Login
    // -----------------------------------------------------------------------------------

    public boolean isDatabaseEmpty() throws SQLException {
        String query = "SELECT COUNT(*) AS count FROM cse360users";
        ResultSet resultSet = statement.executeQuery(query);
        if (resultSet.next()) {
            return resultSet.getInt("count") == 0;
        }
        return true;
    }

    public void register(User user) throws SQLException {
        String insertUser = "INSERT INTO cse360users (userName, password, role) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connectAndPrepare(insertUser)) {
            pstmt.setString(1, user.getUserName());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole());
            pstmt.executeUpdate();
        }
    }

    // Returns true if a user row with that userName/password/role is found
    public boolean login(User user) throws SQLException {
        String query = "SELECT * FROM cse360users WHERE userName = ? AND password = ? AND role = ?";
        try (PreparedStatement pstmt = connectAndPrepare(query)) {
            pstmt.setString(1, user.getUserName());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole());
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean doesUserExist(String userName) {
        String query = "SELECT COUNT(*) FROM cse360users WHERE userName = ?";
        try (PreparedStatement pstmt = connectAndPrepare(query)) {
            pstmt.setString(1, userName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Return all roles (split by commas)
    public List<String> getUserRoles(String userName) {
        List<String> roles = new ArrayList<>();
        String query = "SELECT role FROM cse360users WHERE userName = ?";
        try (PreparedStatement pstmt = connectAndPrepare(query)) {
            pstmt.setString(1, userName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String roleString = rs.getString("role");
                if (roleString != null && !roleString.trim().isEmpty()) {
                    for (String r : roleString.split(",")) {
                        roles.add(r.trim());
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roles;
    }

    // -----------------------------------------------------------------------------------
    // Ensuring user is in DB with a default role, if needed
    // -----------------------------------------------------------------------------------

    /**
     * If the user doesn't exist, register them with default password + role.
     * @param userName the username to ensure
     * @param defaultRole e.g. "admin" or "user"
     * @return true if user now exists, false if something failed
     */
    public boolean ensureUserExists(String userName, String defaultRole) {
        try {
            if (!doesUserExist(userName)) {
                System.out.println("DEBUG: " + userName + " not found in DB. Creating new row...");
                register(new User(userName, "defaultPass", defaultRole));
            }
            return doesUserExist(userName); // re-check
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    
    // -----------------------------------------------------------------------------------
    // Appends a new role to the user's existing roles, if it's not already present.
    // -----------------------------------------------------------------------------------
    
    public boolean addRoleToUser(String userName, String newRole) {
        try {
            // 1) get existing roles
            List<String> currentRoles = getUserRoles(userName);
            if (currentRoles.isEmpty()) {
                // user might not exist OR has an empty role field
                // let's ensure they exist with a default role first
                ensureUserExists(userName, newRole); 
                currentRoles = getUserRoles(userName);
                // at this point they should have at least newRole
            }

            // 2) if they already have newRole, do nothing
            if (currentRoles.contains(newRole)) {
                System.out.println("addRoleToUser: " + userName + " already has role '" + newRole + "'");
                return true; // no need to update
            }

            // 3) append newRole
            currentRoles.add(newRole);
            // build comma string
            String joined = String.join(",", currentRoles);

            // 4) call changeUserRole(...) with the new combined roles
            boolean updated = changeUserRole(userName, joined);
            if (updated) {
                System.out.println("addRoleToUser: " + userName 
                                   + " roles updated => " + joined);
            }
            return updated;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    
    

    /**
     * Update a user's role if they exist. Returns true if row updated, false otherwise.
     */
    public boolean changeUserRole(String userName, String newRole) {
        String query = "UPDATE cse360users SET role = ? WHERE userName = ?";
        try (PreparedStatement pstmt = connectAndPrepare(query)) {
            pstmt.setString(1, newRole);
            pstmt.setString(2, userName);
            int updated = pstmt.executeUpdate();
            System.out.println("changeUserRole: updated=" + updated + " row(s) for userName=" + userName);
            return (updated > 0);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // -----------------------------------------------------------------------------------
    // Invitation code generation & validation
    // -----------------------------------------------------------------------------------

    public String generateInvitationCode() {
        String code = UUID.randomUUID().toString().substring(0, 4);
        String query = "INSERT INTO InvitationCodes (code) VALUES (?)";
        try (PreparedStatement pstmt = connectAndPrepare(query)) {
            pstmt.setString(1, code);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return code;
    }

    public boolean validateInvitationCode(String code) {
        String query = "SELECT * FROM InvitationCodes WHERE code = ? AND isUsed = FALSE";
        try (PreparedStatement pstmt = connectAndPrepare(query)) {
            pstmt.setString(1, code);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                markInvitationCodeAsUsed(code);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void markInvitationCodeAsUsed(String code) {
        String query = "UPDATE InvitationCodes SET isUsed = TRUE WHERE code = ?";
        try (PreparedStatement pstmt = connectAndPrepare(query)) {
            pstmt.setString(1, code);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // -----------------------------------------------------------------------------------
    // Helper
    // -----------------------------------------------------------------------------------
    private PreparedStatement connectAndPrepare(String sql) throws SQLException {
        if (connection == null || connection.isClosed()) {
            connectToDatabase();
        }
        return connection.prepareStatement(sql);
    }

    public void closeConnection() {
        try {
            if (statement != null) statement.close();
        } catch (SQLException se2) {
            se2.printStackTrace();
        }
        try {
            if (connection != null) connection.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }
}