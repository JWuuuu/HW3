package application;

public class Question {
    private int id;
    private String title;
    private String content;
    private String owner; // User who owns the question
    private int ownerPermID; // permID of the owner
    private Integer parentQuestionId;

    public Question(int id, String title, String content, String owner) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.owner = owner;
        this.ownerPermID = 0; // default value
        this.parentQuestionId = null; // No parent for the initial question
    }

    public Question(int id, String title, String content, String owner, Integer parentQuestionId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.owner = owner;
        this.ownerPermID = 0; // default value
        this.parentQuestionId = parentQuestionId;
    }

    // Getter and Setter methods
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getOwner() { return owner; }
    public int getOwnerPermID() { return ownerPermID; }
    public Integer getParentQuestionId() { return parentQuestionId; }

    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }

    // ✅ Added setter for ownerPermID
    public void setOwnerPermID(int ownerPermID) {
        this.ownerPermID = ownerPermID;
    }
} 
