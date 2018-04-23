package comm;

/**
 * @author Tyler Manning
 * Class that represents a tag for a task
 */
public class Tag implements Comparable<Tag>{
    private String label;
    private int id;
    /**
     * Default constructor
     * @param label string that represents the label
     */
    public Tag(String label){
        this.label=label;
    }

    public Tag(String label, int id){
        this.label=label;
        this.id = id;
    }
    public int getId(){return this.id;}
    public void setId(int id){ this.id=id;}

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
