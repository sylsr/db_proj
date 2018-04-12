package cmd;

import asg.cliche.Command;
import asg.cliche.ShellFactory;
import comm.Tag;
import comm.Task;
import comm.User;
import db.DatabaseManager;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;

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
        return db.getActiveTasks().toString();
    }

    /**
     * Adds a task to the database
     * @param label the label of the task
     * @return the id of the task
     */
    @Command
    public int add(String label){
        return db.createTask(new Task(label)).getId();
    }

    /**
     * Sets the date of task with id to the date specified
     * @param id the id of the task to set the date to
     * @param date the due date of the task
     * @return output string that lets the user know their command has completed successfully
     */
    @Command
    public String due(int id, String date){
        Task update = db.get(id);
        update.setDueDate(new Date(date));
        db.update(update);
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
        db.update(update);
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
        db.update(update);
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
        db.update(update);
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
        return db.getActiveTasksWithTag(new Tag(tag)).toString();
    }


    /**
     * Shows completed tasks fo a tag
     * @param tag the tag to get completed tasks for
     * @return a string representing the list of completed tasks with tag
     */
    @Command
    public String completed(String tag){
        return db.getCompletedTasksWithTag(new Tag(tag)).toString();
    }


    /**
     * Gets the list of overdue tasks
     * @return a string representing the list of overdue tasks
     */
    @Command
    public String overdue(){
        return db.getOverdueTasks().toString();
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
                return db.getTasksDueToday().toString();
            case "soon":
                return db.getTasksDueSoon().toString();
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
        db.update(update);
        return "Successfully changed the label";
    }

    /**
     * Searches for tasks with a certain search term.
     * @param searchTerm the term to search for
     * @return a list of tasks that match the search term.
     */
    @Command
    public String search(String searchTerm){
        return db.search(searchTerm).toString();
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
