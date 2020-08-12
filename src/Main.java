import java.lang.management.ManagementFactory;
import java.util.*;

/**
 * This class creates a MulticastPeer
 */
public class Main{    
    
    static String groupIp = "228.5.5.5";
    static int groupPort = 6789;
    static int unicastListenPort;
    static String myUserName;

    static MulticastPeer peer;
        
    static Scanner keyboard;
    static Cryptography crypto;
    

    public static void main(final String args[]) throws Exception {
        
        validateUserInput(args);

        myUserName = ManagementFactory.getRuntimeMXBean().getName();

        System.out.println("Welcomed " + myUserName + "!");
        System.out.println("Type in your message or 'e' to exit: ");                    
                

        // Start Multicast server
        crypto = new Cryptography();
        crypto.generateRSAKkeyPair();
        crypto.generateSignature();
        peer = new MulticastPeer(groupIp, groupPort, unicastListenPort,myUserName, crypto);                                                        
        peer.startPeer();
        
        
        // Start Main menu
        Main main = new Main();
        main.startMenu();
    }

    /**
     * Validate user's command line inputs.
     * If a input is invalid, the program use the default values
     * @param args
     */
    private static void validateUserInput(final String[] args) {
        try {            
            unicastListenPort = Integer.parseInt(args[0]);            
        
        } catch (NumberFormatException e) {
            System.out.println("Arguments must be integers" + e.getMessage());
            System.exit(1);
        } catch (ArrayIndexOutOfBoundsException e){
            System.out.println("Using default number for groupPort(6789) and unicastListenPort (6572)");                        
            unicastListenPort = 6752;
        }
    }

    private void startMenu() throws Exception {

        while(true){                    

            keyboard = new Scanner(System.in);
            String option = keyboard.nextLine();            
            
            if(option.isEmpty()){
                System.out.println("Message cannot be empty");
                continue;
            }


            switch (option) {
                case "e":
                    keyboard.close();                    
                    peer.exit();                    
                    System.exit(0);
                    break;                

                default:                    
                    peer.sendNews(option);                                                                       
                    break;
            } 

        }
        
    }

}