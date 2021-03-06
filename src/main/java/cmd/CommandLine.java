package cmd;

import asg.cliche.Command;
import asg.cliche.ShellFactory;
import comm.Tag;
import comm.Task;
import comm.User;
import db.DatabaseManager;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

/**
 * @author Tyler Manning
 * Class that control the command line interface.
 */
public class CommandLine {
    private DatabaseManager db;

    public CommandLine(User broncoUser){
        db = new DatabaseManager(broncoUser);
    }

    /**
     * The active command
     * Prints out a LinkedList of the active tasks
     * @return String that represents the list of active tasks
     */
    @Command
    public String active(){
        try {
            return buildPrintOutput(db.getActiveTasks());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Logs out from the SQL database
     */
    @Command public void logout(){
        DatabaseManager.logout();
    }

    /**
     * Adds a task to the database
     * @param label the label of the task
     * @return the id of the task
     */
    @Command
    public int add(String label){
        try {
            return db.createTask(new Task(label)).getId();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Sets the date of task with id to the date specified
     * @param id the id of the task to set the date to
     * @param date the due date of the task
     * @return output string that lets the user know their command has completed successfully
     */
    @Command
    public String due(int id, String date) throws ParseException{
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date newDate = fmt.parse(date);
        Task update = db.get(id);
        update.setDueDate(newDate);
        try {
            db.update(update);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Updated task successfully";
    }

    /**
     * Sets the task with the specified id to have the following tags
     * @param id the id of the task
     * @param tag the tags to add to
     * @return output string that lets the user know their command has completed successfully
     */
    @Command
    public String tag( int id, String ... tag){
        Task update = db.get(id);
        for(String t : tag){
            update.addTag(new Tag(t));
        }
        try {
            db.update(update);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Successfully added the tag to the task";
    }

    /**
     * Marks the specified task as finished
     * @param id the id of the task to finish
     * @return output string that lets the user know their command has completed successfully
     */
    @Command
    public String finish(int id){
        Task update = db.get(id);
        update.setStat(Task.Status.FINISHED);
        try {
            db.update(update);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Finished the task";
    }

    /**
     * Marks a specified task as cancelled
     * @param id the id of the task to cancel
     * @return output string that lets the user know their command has completed successfully
     */
    @Command
    public String cancel(int id){
        Task update = db.get(id);
        update.setStat(Task.Status.CANCELLED);
        try {
            db.update(update);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Cancelled the task";
    }

    /**
     * Shows the details of the specified task
     * @param id the id of the task to elaborate
     * @return a string describing the task
     */
    @Command
    public String show(int id){
        return db.get(id).toString();
    }

    /**
     * Shows the active tasks for a tag
     * @param tag the tag to show active tasks for
     * @return String that represents a list of active tasks with the specified task
     */
    @Command
    public String active( String tag){
        try {
            return buildPrintOutput(db.getActiveTasksWithTag(new Tag(tag)));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Shows completed tasks fo a tag
     * @param tag the tag to get completed tasks for
     * @return a string representing the list of completed tasks with tag
     */
    @Command
    public String completed(String tag){
        try {
            return buildPrintOutput(db.getCompletedTasksWithTag(new Tag(tag)));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Gets the list of overdue tasks
     * @return a string representing the list of overdue tasks
     */
    @Command
    public String overdue(){
        try {
            return buildPrintOutput(db.getOverdueTasks());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets the tasks that are either due 'today' or 'soon'
     * @param timeline 'today' or 'soon'
     * @return A string that represents the list of tasks due 'today' or 'soon'
     */
    @Command
    public String due(String timeline){
        switch(timeline){
            case "today":
                try {
                    return buildPrintOutput(db.getTasksDueToday());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            case "soon":
                try {
                    return buildPrintOutput(db.getTasksDueSoon());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
        return "Please enter a valid timeline (either 'today' or 'soon'";
    }

    /**
     * renames the specified task (with id) to the new label
     * @param id the id of the task to rename
     * @param label the new label
     * @return output string that lets the user know their command has completed successfully
     */
    @Command
    public String rename(int id, String label){
        Task update = db.get(id);
        update.setLabel(label);
        try {
            db.update(update);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Successfully changed the label";
    }

    private String buildPrintOutput(LinkedList<Task> list){
        StringBuilder str = new StringBuilder();
        for(Task t : list){
            str.append(t.toString());
            str.append("\n");
        }
        return str.toString();
    }

    /**
     * Searches for tasks with a certain search term.
     * @param searchTerm the term to search for
     * @return a list of tasks that match the search term.
     */
    @Command
    public String search(String searchTerm){
        return buildPrintOutput(db.search(searchTerm));
    }

    /**
     * Starts the interactive command line
     * @throws IOException if the command line errors out
     */
    public void startCommandLine() throws IOException{
        ShellFactory.createConsoleShell(">","Taskmanager",this)
                .commandLoop();
    }

}
