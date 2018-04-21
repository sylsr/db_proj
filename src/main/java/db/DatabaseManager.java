package db;

import comm.Tag;
import comm.Task;
import comm.User;

import java.net.ServerSocket;
import java.sql.*;
import com.jcraft.jsch.*;
import java.util.LinkedList;


/**
 * @author Tyler Manning
 */
public class DatabaseManager {
    private User broncoUser;
    private Connection broncoConnection;
    private Session broncoSession;

    public DatabaseManager(User broncoUser) {
        this.broncoUser = broncoUser;
        this.broncoConnection = getDBConnection();
    }
    /**
     * Establishes connection to the database remotely.
     * @return Connection
     */
    public Connection getDBConnection() {

        Connection con = null;

        try {

            this.broncoSession = sessionSSH();

            Class.forName("com.mysql.jdbc.Driver").newInstance();
            con = DriverManager.getConnection("dbc:mysql://localhost:" + 7555, broncoUser.getSandboxUserId(), broncoUser.getSandboxPassword());

            return con;
        } catch (Exception ex) {
            System.out.println("Exception Connection: " + ex.getMessage());

        }
        return con;
    }
    /**
     * Gets the ssh
     * @return ssh connection
     */
    public Session sessionSSH(){

        Session session = null;
        JSch jsch = new JSch();

        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");

        try {
            session = jsch.getSession(broncoUser.getBroncoUserId(), "onyx.boisestate.edu", 22);

            session.setPassword(broncoUser.getBroncoPassword());
            session.setConfig(config);

            System.out.println("Establishing a Connection...");

            session.connect();
            session.setPortForwardingL(7555, "localhost", broncoUser.getPortNum());
            return session;

        } catch(Exception e){
            System.out.println("Exception in SSH: " + e.getMessage());
        }
        return session;
    }

    /**
     * Closes and disconnects external connections.
     */
    public void logout() throws SQLException{
        broncoConnection.setAutoCommit(true);
        broncoConnection.close();
        broncoSession.disconnect();
    }


    /**
     * Gets the list of tasks marked as active
     * @return list of tasks
     */
    public LinkedList<Task> getActiveTasks(){


        return null; //TODO
    }

    /**
     * Gets the list of tasks marked as overdue
     * @return list of tasks
     */
    public LinkedList<Task> getOverdueTasks(){
        return null; //TODO
    }

    /**
     * Gets the list of active tasks with a specified tag
     * @param tag the tag to find tasks with
     * @return the list of tasks
     */
    public LinkedList<Task> getActiveTasksWithTag(Tag tag){
        return null;//TODO
    }

    /**
     * Gets the list of completed tasks with a specified tag
     * @param tag the tag to find tasks with
     * @return the list of tasks
     */
    public LinkedList<Task> getCompletedTasksWithTag(Tag tag){
        return null;//TODO
    }


    /**
     * Gets the list of tasks due today
     * @return the list of tasks
     */
    public LinkedList<Task> getTasksDueToday(){
        return null;//TODO
    }

    /**
     * Gets the list of tasks due soon (within the next 3 days
     * @return the list of tasks
     */
    public LinkedList<Task> getTasksDueSoon(){
        return null;//TODO
    }

    /**
     * Creates a new task
     * @param create the task to create
     * @return the task that was just created (with updated fields such as id)
     */
    public Task createTask(Task create){
        return null;//TODO:
    }

    /**
     * Gets a task from the database
     * @param id the id that specified the task
     * @return the task
     */
    public Task get(int id){
        return null;//TODO:
    }

    /**
     * Updates a specified task with the values. Note that the id of the task being updated must remain the same,
     * all other fields can change
     * @param update the task to update (already includes updated fields)
     * @return the updated task
     */
    public Task update(Task update){
        return null;
    }

    /**
     * Searches for tasks with the specified term
     * @param term the term to search by
     * @return the list of tasks
     */
    public LinkedList<Task> search(String term){
        return null;//TODO
    }


}
