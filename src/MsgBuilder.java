import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

public class MsgBuilder {
        
    String username;

    public MsgBuilder(String username) {
        this.username = username;
    }

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
}