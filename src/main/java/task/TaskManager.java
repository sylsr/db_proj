package task;

import cmd.CommandLine;
import comm.User;
import asg.cliche.Command;
import asg.cliche.ShellFactory;
import java.io.IOException;

/**
 * @author Tyler Manning
 */
public class TaskManager {
    private static String usage = "java TaskManager <BroncoUserid> <BroncoPassword> <sandboxUSerID> <sandbox password>\n" +
            "<yourportnumber>";
    private static User broncoUser=null;
    public static void main(String[] args){
        parseArgs(args);
        CommandLine cmd = new CommandLine(broncoUser);
        try{
            cmd.startCommandLine();
        }catch(IOException e){
            System.err.println("The command line encountered an error starting up");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Parses command line arguments
     * @param args command line arguments
     */
    public static void parseArgs(String[] args){
        try{
            broncoUser = new User(args[0], args[1], args[2], args[3], Integer.parseInt(args[4]));
        }catch(Exception e){
            System.err.println(usage);
            System.exit(1);
        }
    }
}
