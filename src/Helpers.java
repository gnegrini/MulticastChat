import java.util.LinkedHashMap;
import java.util.Map;

public class Helpers {
    
    // Ref: https://stackoverflow.com/questions/26485964/how-to-convert-string-into-hashmap-in-java/26486046
    public static Map<String,String> parsePacketDataToMap(byte[] value){
        
        String msgString =  new String(value);

        msgString = msgString.trim();

        Map<String,String> map = new LinkedHashMap<>();            

        msgString = msgString.substring(1, msgString.length()-1);           //remove curly brackets
        String[] keyValuePairs = msgString.split(",");              //split the string to creat key-value pairs

        for(String pair : keyValuePairs)                        //iterate over the pairs
        {
            String[] entry = pair.split("=");                   //split the pairs to get key and value 
            map.put(entry[0].trim(), entry[1].trim());          //add them to the hashmap and trim whitespaces
        }

        return map;

    }

    public static void printMap(Map<String, String> userMsg) {

        System.out.println("\nReceived: ");                
        System.out.println();
        
        for (Map.Entry<String, String> entry : userMsg.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        System.out.println();

    }
    
    
}