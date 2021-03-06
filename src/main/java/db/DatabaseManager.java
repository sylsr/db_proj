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
 * @author Lance Martin
 */

public class DatabaseManager {
    private User broncoUser;
    private static Connection broncoConnection;
    private static Session broncoSession;

    public DatabaseManager(User broncoUser) {
        final Thread mainThread = Thread.currentThread();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                DatabaseManager.logout();
            }
        });
        this.broncoUser = broncoUser;
        this.broncoConnection = getDBConnection();
        //Update Tasks
        updateTaskSatus();
    }
    /**
     * Establishes connection to the database remotely.
     * @return Connection
     */
    private Connection getDBConnection() {

        Connection con = null;

        try {

            this.broncoSession = sessionSSH();

            Class.forName("com.mysql.jdbc.Driver").newInstance();
            System.out.println("Attempting to connect to the database.");
            con = DriverManager.getConnection("jdbc:mysql://localhost:7555/todoList?verifyServerCertificate=false&useSSL=true", broncoUser.getSandboxUserId(), broncoUser.getSandboxPassword());

            return con;
        } catch (Exception ex) {
            System.err.println("Exception Connection: " + ex.getMessage());
            ex.printStackTrace();
            System.err.println("Exiting...");
            System.exit(1);
        }
        return null;
    }
    /**
     * Gets the ssh
     * @return ssh connection
     */
    private Session sessionSSH(){

        Session session = null;
        JSch jsch = new JSch();

        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");

        try {
            session = jsch.getSession(broncoUser.getBroncoUserId(), "onyx.boisestate.edu", 22);

            session.setPassword(broncoUser.getBroncoPassword());
            session.setConfig(config);
            session.setConfig("PreferredAuthentications", "pulickey,keyboard-interactive,password");

            System.out.println("Establishing an SSH Connection...");

            session.connect();
            session.setPortForwardingL(7555, "localhost", broncoUser.getPortNum());
            return session;

        } catch(Exception e){
            System.err.println("Exception in SSH: " + e.getMessage());
            e.printStackTrace();
            System.err.println("Exiting...");
            System.exit(1);
        }
        return session;
    }

    /**
     * Closes and disconnects external connections.
     */
    public static void logout(){
        try{
            broncoConnection.setAutoCommit(true);
            broncoConnection.close();
            broncoSession.disconnect();
        }catch (Exception e){
            //we tried.
        }
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
        stmt.close();


        return dueTasks;
    }

    /**
     * Gets the list of tasks due soon (within the next 3 days
     * @return the list of tasks
     */
    public LinkedList<Task> getTasksDueSoon() throws SQLException{

        String sql = "SELECT * FROM Task WHERE due_Date  >= CURDATE() AND due_Date <= (CURDATE() +3)";
        LinkedList<Task> dueTasks = new LinkedList<>();

        java.sql.Statement stmt = broncoConnection.createStatement();

        ResultSet result = stmt.executeQuery(sql);

        while(result.next()){
            Task temp = new Task(result.getInt(1), result.getString(2), result.getDate(3), result.getDate(4), result.getString(5));
            dueTasks.add(temp);
        }
        stmt.close();


        return dueTasks;
    }

    /**
     * Creates a new task
     * @param create the task to create
     * @return the task that was just created (with updated fields such as id)
     */
    public Task createTask(Task create) throws SQLException{

        String sql = "INSERT Task (label) values ('" + create.getLabel() + "' )";

        java.sql.Statement stmt = broncoConnection.createStatement();

        //Execute query and return the ID of the task that was inserted  into the query
        Boolean result = stmt.execute(sql);


        //Select id for last inserted
        sql = "SELECT LAST_INSERT_ID()";
        ResultSet results = stmt.executeQuery(sql);

        while(results.next()){
            create.setId(results.getInt(1));
        }

        stmt.close();

        return create;
    }

    /**
     * Gets a task from the database
     * @param id the id that specified the task
     * @return the task
     */
    public Task get(int id){

        Task temp = null;
        try{

            String sql = "SELECT * FROM Task WHERE task_id = " + id;

            java.sql.Statement stmt = broncoConnection.createStatement();

            ResultSet result = stmt.executeQuery(sql);

            while(result.next()){
                temp = new Task(result.getInt(1), result.getString(2), result.getDate(3), result.getDate(4), result.getString(5));

            }

            stmt.close();
        } catch(SQLException e){
            System.out.println(e.getMessage());
        }

        return temp;
    }

    /**
     * Updates a specified task with the values. Note that the id of the task being updated must remain the same,
     * all other fields can change
     * @param update the task to update (already includes updated fields)
     * @return the updated task
     */
    public Task update(Task update) throws SQLException{

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dueDate = dateFormat.format(update.getDueDate());


        String sql = "UPDATE Task SET label = '" + update.getLabel() + "', due_date = '" + dueDate + "', Status = '" + update.getStat().toString() + "'  WHERE task_id = " + update.getId() ;

        java.sql.Statement stmt = broncoConnection.createStatement();

        boolean result = stmt.execute(sql);

        if(!update.getTags().equals(null)){
            insertTag(update.getTags());

            for(Tag t : update.getTags()){
                sql = "INSERT INTO Task_tag (task_id, tag_id) " +
                        "SELECT * FROM ( SELECT " +update.getId()+", " + t.getId()+ ") AS temp " +
                        "WHERE NOT EXISTS( " +
                        "SELECT task_id, tag_id FROM Task_tag WHERE task_id =" +update.getId()+ " AND tag_id =" + t.getId()+ ");";
                result = stmt.execute(sql);

            }
        }



        stmt.close();

        return update;
    }

    /**
     * Searches for tags with the specified label and inserts the tag if it doesn't exist.
     * @param tags the tags to insert
     */
    private void insertTag(LinkedList<Tag> tags) throws SQLException{

        java.sql.Statement stmt = broncoConnection.createStatement();

        String sql = "";
        for(Tag t : tags){
            sql = "INSERT INTO Tag (label) " +
                    "SELECT * FROM ( SELECT '" + t.toString()+"') AS temp " +
                    "WHERE NOT EXISTS( " +
                    "SELECT label FROM Tag WHERE label ='" + t.toString()+"');";
            Boolean result = stmt.execute(sql);

            sql = "SELECT  * FROM Tag WHERE label = '" + t.toString() +"'";
            ResultSet results = stmt.executeQuery(sql);

            while(results.next()){
                t.setId(results.getInt(1));
            }


        }
        stmt.close();
    }

    public void updateTaskSatus(){
        try{
        java.sql.Statement stmt = broncoConnection.createStatement();
        String sql = "UPDATE Task SET status = 'OVERDUE'" +
                 " WHERE due_date < curdate()";

            boolean result = stmt.execute(sql);
            stmt.close();
        }
        catch(SQLException esql){
            System.out.println(esql.getSQLState());
        }



    }
    /**
     * Searches for tasks with the specified term
     * @param term the term to search by
     * @return the list of tasks
     */
    public LinkedList<Task> search(String term){

        String sql = "SELECT * FROM Task WHERE label LIKE '%" + term + "%' ";
        LinkedList<Task> leTasks = new LinkedList<>();

        try{
            java.sql.Statement stmt = broncoConnection.createStatement();

            ResultSet result = stmt.executeQuery(sql);

            while(result.next()){
                Task temp = new Task(result.getInt(1), result.getString(2), result.getDate(3), result.getDate(4), result.getString(5));
                leTasks.add(temp);
            }
            stmt.close();

        }catch(SQLException e){
            System.out.println(e.getSQLState());
        }

    return leTasks;

    }




}
