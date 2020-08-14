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

        myUserName = ManagementFactory.getRuntimeMXBean().getName();

        System.out.println("Welcomed " + myUserName + "!");
        System.out.println("Type in your message or 'e' to exit: ");                    
                

        // Start Multicast server
        crypto = new Cryptography();
        crypto.generateRSAKkeyPair();
        crypto.generateSignature();
        peer = new MulticastPeer(groupIp, groupPort,myUserName, crypto);                                                        
        peer.startPeer();
        
        
        // Start Main menu
        Main main = new Main();
        main.startMenu();
    }


    private void startMenu() throws Exception {

        while(true){                    

            System.out.print(myUserName + ": ");
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