package comm;

/**
 * @author Tyler Manning
 * Class that represents a "user" (really just a container for command line information)
 */
public class User {
    private String broncoUserId;
    private String broncoPassword;
    private String sandboxUserId;
    private String sandboxPassword;
    private int portNum;

    /**
     * Default constructor
     * @param broncoUserId the bronco user id of the user
     * @param broncoPassword the bronco password of the user
     * @param sandboxUserId the sandbox user id for the database
     * @param sandboxPassword the sandbox user password for the database
     * @param portNum the port number for the database
     */
    public User(String broncoUserId, String broncoPassword, String sandboxUserId, String sandboxPassword, int portNum){
        this.broncoUserId=broncoUserId;
        this.broncoPassword=broncoPassword;
        this.sandboxUserId=sandboxUserId;
        this.sandboxPassword=sandboxPassword;
        this.portNum=portNum;
    }

    /**
     * Gets the bronco user id
     * @return bronco user id
     */
    public String getBroncoUserId() {
        return broncoUserId;
    }

    /**
     * Gets the bronco password
     * @return bronco password
     */
    public String getBroncoPassword() {
        return broncoPassword;
    }

    /**
     * Gets the sandbox user id
     * @return sandbox user id
     */
    public String getSandboxUserId() {
        return sandboxUserId;
    }

    /**
     * Gets the sandbox password
     * @return sandbox password
     */
    public String getSandboxPassword() {
        return sandboxPassword;
    }

    /**
     * Gets the database port number
     * @return port
     */
    public int getPortNum() {
        return portNum;
    }
}
