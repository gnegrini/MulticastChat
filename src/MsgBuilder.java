import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class is used to build the diferent types of messages
 * the peer has to send. All methods receive parameters as String
 * and return the message as a Map
 */
public class MsgBuilder {
        
    String username;

    
    public MsgBuilder(String username) {
        this.username = username;
    }

    /**
     * Start the Map and fill it with the sender username and time,
     * which are present in all messages
     * @return the created map with common entries
     */
    public Map<String, String> buildInitialMap(){
        Map<String, String> msgDict = new LinkedHashMap<String, String>();
        String now = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        msgDict.put("sender", username);
        msgDict.put("time", now);
        //msgDict.put("msgId", String.valueOf(msgDict.hashCode()));
        
        return msgDict;
    }

    public String buildGreetingMsg(int unicastPort, String publicKey) {
        Map<String, String> msgDict = buildInitialMap();
        msgDict.put("msgType", "Greeting");
        msgDict.put("unicastPort", String.valueOf(unicastPort));
        msgDict.put("publicKey", publicKey);
        return msgDict.toString();
    }

    public String buildGreetingBackMsg(String publicKey) {
        Map<String, String> msgDict = buildInitialMap();
        msgDict.put("msgType", "GreetingBack");
        msgDict.put("publicKey", publicKey);
        return msgDict.toString();
    }

    public String buildNewsMsg(String msg) throws Exception {
        Map<String, String> msgDict = buildInitialMap();        
        msgDict.put("msgType", "News");        
        msgDict.put("msg", msg);
        return msgDict.toString();
    }

	public String buildFakeNewsWarningMsg(String sender, String time, String subject) {
        Map<String, String> msgDict = buildInitialMap();
        msgDict.put("msgType", "FakeNewsWarning");
        msgDict.put("reference", sender + "-" + time);
        msgDict.put("subject", subject);
        return msgDict.toString();
	}
}