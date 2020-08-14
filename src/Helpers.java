import java.net.DatagramPacket;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Common class to help with general activities,
 * such as printing a Map or parsing data.
 */
public abstract class Helpers {
    /**
     * Converts a PacketData to a Map with values
     * Adapted from:
     * https://stackoverflow.com/questions/26485964/how-to-convert-string-into-hashmap-in-java/26486046
     * @param messageIn the data valeu fom the PacketData
     * @return a Map with the data
     */
    public static Map<String,String> parsePacketDataToMap(DatagramPacket messageIn){
        

        Map<String,String> map = new LinkedHashMap<>();            

        map.put("senderSocketAdress", messageIn.getSocketAddress().toString());

        String msgString =  new String(messageIn.getData());

        msgString = msgString.trim();

        msgString = msgString.substring(1, msgString.length()-1);           //remove curly brackets
        String[] keyValuePairs = msgString.split(",");              //split the string to creat key-value pairs

        for(String pair : keyValuePairs)                        //iterate over the pairs
        {
            String[] entry = pair.split("=");                   //split the pairs to get key and value 
            map.put(entry[0].trim(), entry[1].trim());          //add them to the hashmap and trim whitespaces
        }

        return map;

    }
    /**
     * Prints the Map in the console as "key:value" par
     * @param userMsg the map to be printed
     */
    public static void printMap(Map<String, String> userMsg) {

        System.out.println("\nReceived: ");                
        System.out.println();
        
        for (Map.Entry<String, String> entry : userMsg.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        System.out.println();

    }
    
    
}