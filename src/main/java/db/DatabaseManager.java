package db;

import comm.Tag;
import comm.Task;
import comm.User;


import java.sql.*;
import com.jcraft.jsch.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
    public LinkedList<Task> getActiveTasks() throws SQLException{
        String sql = "SELECT * FROM Task WHERE status = 'ACTIVE'";
        LinkedList<Task> activeTasks = new LinkedList<>();

        java.sql.Statement stmt = broncoConnection.createStatement();

        ResultSet result = stmt.executeQuery(sql);

        while(result.next()){
            Task temp = new Task(result.getInt(1), result.getString(2), result.getDate(3), result.getDate(4), result.getString(5));
            activeTasks.add(temp);
        }
        broncoConnection.commit();
        stmt.close();


        return activeTasks;
    }

    /**
     * Gets the list of tasks marked as overdue
     * @return list of tasks
     */
    public LinkedList<Task> getOverdueTasks()throws SQLException{
        String sql = "SELECT * FROM Task WHERE status = 'OVERDUE'";
        LinkedList<Task> overDueTasks = new LinkedList<>();

        java.sql.Statement stmt = broncoConnection.createStatement();

        ResultSet result = stmt.executeQuery(sql);

        while(result.next()){
            Task temp = new Task(result.getInt(1), result.getString(2), result.getDate(3), result.getDate(4), result.getString(5));
            overDueTasks.add(temp);
        }
        broncoConnection.commit();
        stmt.close();


        return overDueTasks;
    }

    /**
     * Gets the list of active tasks with a specified tag
     * @param tag the tag to find tasks with
     * @return the list of tasks
     */
    public LinkedList<Task> getActiveTasksWithTag(Tag tag) throws SQLException{
        String sql = "SELECT * FROM Task_tag JOIN Task on Task_tag.task_id = Task.task_id JOIN Tag on Task_tag.tag_id = Tag.tag_id  WHERE Tag.label = '" + tag.toString() + "' AND Task.status ='ACTIVE'";
        LinkedList<Task> activeTasks = new LinkedList<>();

        java.sql.Statement stmt = broncoConnection.createStatement();

        ResultSet result = stmt.executeQuery(sql);

        while(result.next()){
            Task temp = new Task(result.getInt(1), result.getString(4), result.getDate(5), result.getDate(6), result.getString(7));
            activeTasks.add(temp);
        }
        broncoConnection.commit();
        stmt.close();


        return activeTasks;
    }

    /**
     * Gets the list of completed tasks with a specified tag
     * @param tag the tag to find tasks with
     * @return the list of tasks
     */
    public LinkedList<Task> getCompletedTasksWithTag(Tag tag) throws SQLException{
        String sql = "SELECT * FROM Task_tag JOIN Task on Task_tag.task_id = Task.task_id JOIN Tag on Task_tag.tag_id = Tag.tag_id  WHERE Tag.label = '" + tag.toString() + "' AND Task.status ='FINISHED'";
        LinkedList<Task> completedTasks = new LinkedList<>();

        java.sql.Statement stmt = broncoConnection.createStatement();

        ResultSet result = stmt.executeQuery(sql);

        while(result.next()){
            Task temp = new Task(result.getInt(1), result.getString(4), result.getDate(5), result.getDate(6), result.getString(7));
            completedTasks.add(temp);
        }
        broncoConnection.commit();
        stmt.close();


        return completedTasks;
    }


    /**
     * Gets the list of tasks due today
     * @return the list of tasks
     */
    public LinkedList<Task> getTasksDueToday() throws SQLException{
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String today = dateFormat.format(date);

        String sql = "SELECT * FROM Task WHERE due_Date = '" + today + "'";
        LinkedList<Task> dueTasks = new LinkedList<>();

        java.sql.Statement stmt = broncoConnection.createStatement();

        ResultSet result = stmt.executeQuery(sql);

        while(result.next()){
            Task temp = new Task(result.getInt(1), result.getString(2), result.getDate(3), result.getDate(4), result.getString(5));
            dueTasks.add(temp);
        }
        broncoConnection.commit();
        stmt.close();


        return dueTasks;
    }

    /**
     * Gets the list of tasks due soon (within the next 3 days
     * @return the list of tasks
     */
    public LinkedList<Task> getTasksDueSoon() throws SQLException{
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();

        //Add three days to today
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 2);

        String today = dateFormat.format(date);
        String dayAfterTomorrow = dateFormat.format(c.getTime());

        String sql = "SELECT * FROM Task WHERE due_Date > ='" + today + "' AND <= '" + dayAfterTomorrow + "'";
        LinkedList<Task> dueTasks = new LinkedList<>();

        java.sql.Statement stmt = broncoConnection.createStatement();

        ResultSet result = stmt.executeQuery(sql);

        while(result.next()){
            Task temp = new Task(result.getInt(1), result.getString(2), result.getDate(3), result.getDate(4), result.getString(5));
            dueTasks.add(temp);
        }
        broncoConnection.commit();
        stmt.close();


        return dueTasks;
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
