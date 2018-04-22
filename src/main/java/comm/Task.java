package comm;

import java.util.Date;
import java.util.LinkedList;

/**
 * @author Tyler Manning
 * Class that represents a task
 */
public class Task implements Comparable<Task>{
    public static enum Status{
        /**
         * Flag to denote cancelled tasks
         */
        CANCELLED,
        /**
         * Flag to denote on-going or active tasks
         */
        ACTIVE,
        /**
         * Flag to denote finished tasks
         */
        FINISHED,
        /**
         * Flag to denote overdue tasks (task is passed due date and not done)
         */
        OVERDUE
    }

    private LinkedList<Tag> tags;
    private String label;
    private int id;
    private Date createDate;
    private Date dueDate;
    private Status stat;

    /**
     * Default constructor
     * @param label the name of the task
     */
    public Task(String label){
        this.label =label;
        this.createDate = new Date();
        this.stat = Status.ACTIVE;//Default to active
    }
    /**
     * Overloaded constructor
     * @param id the primary key of the id
     * @param label the name of the task
     * @param createDate the date the task was created.
     * @param dueDate the date the task is due.
     * @param status the status of the task.
     */
    public Task(int id, String label, Date createDate, Date dueDate, String status){
        this.label =label;
        this.id = id;
        this.createDate = createDate;
        this.dueDate = dueDate;
        this.stat = Status.valueOf(status);

    }
    /**
     * Gets the list of tags associated with this task
     * @return tag list
     */
    public LinkedList<Tag> getTags() {
        return tags;
    }

    /**
     * Adds a tag to the task
     * @param tag tag to add to the list
     */
    public void addTag(Tag tag) {
        this.tags.add(tag);
    }

    /**
     * Gets the label (or name) of this task
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Gets the id of this task (this is set by the database)
     * @return task id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id of this task (should only be used by the database manager)
     * @param id the id of the task
     */
    public void setId(int id) {
        this.id = id;
    }

    public void setLabel(String label){
        this.label=label;
    }

    /**
     * Gets the creation date of this task
     * @return creation date
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * Gets the due date of this task
     * @return gets the due date of this task
     */
    public Date getDueDate() {
        return dueDate;
    }

    /**
     * Sets the due date of this task
     * @param dueDate the due date to set
     */
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * Gets the status of this task
     * @return status of this task
     */
    public Status getStat() {
        return stat;
    }

    /**
     * Sets the status of this task (the task defaults to ACTIVE
     * @param stat the status of this task
     */
    public void setStat(Status stat) {
        this.stat = stat;
    }


    @Override
    public int hashCode() {
        return label.hashCode()+createDate.hashCode()+tags.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Task){
            if(obj.hashCode() == this.hashCode()){
                return true;
            }
        }else{
            return false;
        }
        return false;//wut?
    }

    @Override
    public String toString() {
        return "id: "+id+" "+label+" Created On: "+createDate+" Due by: "+dueDate+" Tags: "+tags+"Status: "+stat;
    }

    @Override
    public int compareTo(Task o) {
        if(o.hashCode() == this.hashCode()){
            return 0;//equal
        }
        return -1;//not equal, don't care
    }
}
