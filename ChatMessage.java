
import java.io.Serializable;

/**
 * Chat-Client-Server Project
 *
 * This class represents a chat message sent by the client to the server.
 * Chat messages determine the manner in which output to different clients
 * and even to the server would be represented and what their content would be
 *
 * @author Neso Udora, pudora@purdue.edu
 * @author Zach Skiles, skilesz@purdue.edu
 *
 * @version 2018-11-14
 */

final class ChatMessage implements Serializable {
    private static final long serialVersionUID = 6898543889087L;

    //instance variables

    private int type;
    private String message;
    private String recipient;



    //constructors

    public ChatMessage(int type, String recipient, String message) {
        this.type = type;
        this.message = message;
        this.recipient = recipient;
    }

    public ChatMessage(int type, String message) {
        this(type, "all", message);
    }



    //getters and setters
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
}
