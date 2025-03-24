package application;

/**
 * Represents an answer linked to a specific question.
 */
public class Answer {
    private int id;
    private int questionId;
    private String content;
    private String owner;
    private boolean pinStatus; //Marks a question as pinned by the user

    public Answer(int id, int questionId, String content, String owner) {
        this.id = id;
        this.questionId = questionId;
        this.content = content;
        this.owner = owner;
        this.pinStatus = false;
    }

    public int getId() { return id; }
    public int getQuestionId() { return questionId; }
    public String getContent() { return content; }
    public String getOwner() { return owner; }
    public boolean getPin() { return pinStatus; }
    
    //togglePin() inverts the pinStatus of the answer, corresponding to the user action of pinning and un-pinning that answer.
    public void togglePin() {
    	this.pinStatus = !this.pinStatus;
    }
    public void setContent(String content) { this.content = content; }
    
    //getPinDisplayText formats the answer text to include a '*' symbol for pinned answers
    public String getPinDisplayText() {
    	String formatText = "";
    	if(pinStatus) {
    		formatText += "*";
    	} else {
    		formatText += " ";
    	}
    	formatText += "<" + this.owner + ">: " + this.content;
    	return formatText;
    }
    
}