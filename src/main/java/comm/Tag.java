package comm;

/**
 * @author Tyler Manning
 * Class that represents a label for a task
 */
public class Tag implements Comparable<Tag>{
    private String label;

    /**
     * Default constructor
     * @param label string that represents the label
     */
    public Tag(String label){
        this.label=label;
    }

    @Override
    public int hashCode() {
        return label.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Tag){
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
        return label;
    }

    @Override
    public int compareTo(Tag o) {
        if(o.hashCode() == this.hashCode()){
            return 0;//equal
        }
        return -1;//otherwise we don't care
    }
}
