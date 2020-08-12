import java.net.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.io.*;

/**
 * This class creates a MulticastPeer Usage: put the message in args[0] and the
 * destination group IP in args[1] Ex: java MuticatPeer Hello, World 228.5.6.7
 */
public class MulticastPeer extends Thread {

    private String groupIp;
    private int groupPortNumber;
    private int myUnicastPort;
    private String myUserName;
    private String myPublicKey;
    private boolean isActive;

    static Map<String, String> knownPeers;

    private InetAddress groupInet;
    private MulticastSocket multicastSocket;
    private Cryptography crypto;
    private MsgBuilder msgBuilder;
    private UDPUnicast unicast;
    private ReputationKeeper repkeeper;

    public MulticastPeer(String groupIp, int port, int unicastListenPort, String username, Cryptography crypto) {

        this.groupIp = groupIp;
        this.groupPortNumber = port;
        this.myUnicastPort = unicastListenPort;
        this.crypto = crypto;
        this.myUserName = username;

        isActive = false;

    }

    public void startPeer() {

        knownPeers = new LinkedHashMap<String, String>();
        msgBuilder = new MsgBuilder(myUserName, crypto.getSignature());
        repkeeper = new ReputationKeeper(myUserName);

        // Start Reputation keeper
        repkeeper.startKeeper();

        // Start Unicast server (in a new thread)
        unicast = new UDPUnicast(myUnicastPort);
        unicast.start();

        try {
            myPublicKey = crypto.getPublicKeyAsString();
            joinMulticastGroup();
            sendGreeting();
            this.start();
        } catch (Exception e) {
            System.out.println("Error while initiating peer");
            e.printStackTrace();
        }
    }

    public void joinMulticastGroup() throws IOException {

        groupInet = InetAddress.getByName(groupIp);
        multicastSocket = new MulticastSocket(groupPortNumber);
        multicastSocket.joinGroup(groupInet);

        isActive = true;
    }

    public void run() {
        listenToGroup();
    }

    public void listenToGroup() {

        while (isActive) {

            byte[] buffer = new byte[1000];

            DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
            try {
                multicastSocket.receive(messageIn);
            } catch (IOException e) {
                System.out.println("Socket connection closed");
                e.printStackTrace();
            }

            handleMessage(messageIn);

        }

    }

    /**
     * Decides next steps depending on the map receive
     * 
     * @param map
     */
    private void handleMessage(DatagramPacket messageIn) {

        Map<String, String> msgMap = Helpers.parsePacketDataToMap(messageIn.getData());

        // no need to handle own message
        if (msgMap.get("sender").equals(myUserName)) {
            return;
        }

        Helpers.printMap(msgMap);

        try {
            verifySignature(msgMap);
        } catch (Exception e) {
            String sender = msgMap.get("sender");
            String time = msgMap.get("time");
            System.out.println("Signature could not be verified for msg: " + sender + "-" + time);
            return;
        }

        switch (msgMap.get("msgType")) {
            case "Greeting":
                addPeerToKnownPeersMap(msgMap);
                sendGreetingBack(msgMap);
                break;

            case "News":
                String decryptedMsg = decryptMsg(msgMap);
                System.out.println("Decrypted News: " + decryptedMsg + "\n");
                String fakeNewsSubject = FakeNewsAnalyzer.containsFakeNews(decryptedMsg);
                if (fakeNewsSubject != null) {
                    sendFakeNewsWarning(msgMap, fakeNewsSubject);
                    updateReputationRank(msgMap);
                }
                break;
                
            case "Goodbye":
                removePeerFromKnownPeersMap(msgMap);
                break;

            default:
                System.out.println("Message type unknown");
                break;
        }

    }

    private void verifySignature(Map<String, String> msgMap) throws Exception {
        // no need to verify signature
        if(msgMap.get("msgType").equals("Greeting"))
            return;
        
        String signature = msgMap.get("signature");        
        String peerPublicKeyAsString = retrievePeerPublicKey(msgMap);
        String decryptedSignature = crypto.decryptText(peerPublicKeyAsString, signature);

        if(!decryptedSignature.equals("Authenticated")){
            throw new Exception("Signature could not be verified");
        }
        
        return;
    }

    public void sendGreeting() throws Exception {

        try {

            String greetingMsg = msgBuilder.buildGreetingMsg(myUnicastPort, myPublicKey);
            sendStringToGroup(greetingMsg);

        } catch (Exception e) {
            System.out.println("Error while sending greeting");
            e.printStackTrace();
        }
    }

    private void sendGreetingBack(Map<String, String> msgMap) {

        int newPeerUnicastPort = Integer.parseInt(msgMap.get("unicastPort"));
        String greetingBackMsg = msgBuilder.buildGreetingBackMsg(myPublicKey);
        unicast.sendString(greetingBackMsg, "localhost", newPeerUnicastPort);

    }

    public void sendNews(String msg) throws Exception {

        try {

            String encryptedMsg = crypto.do_RSAEncryption(msg);
            String newsMsg = msgBuilder.buildNewsMsg(encryptedMsg);
            sendStringToGroup(newsMsg);

        } catch (IllegalArgumentException | IOException e) {
            System.out.println("Error while sending News");
            e.printStackTrace();
        }
    }

    private void sendFakeNewsWarning(Map<String, String> msgMap, String subject) {
        String sender = msgMap.get("sender");
        String time = msgMap.get("time");
        String newsMsg = msgBuilder.buildFakeNewsWarningMsg(sender, time, subject);
        try {
            sendStringToGroup(newsMsg);
        } catch (IllegalArgumentException | IOException e) {
            System.out.println("Error while sending FakeNewsWarning");
            e.printStackTrace();
        }
    }

    public void sendStringToGroup(String msg) throws IOException, IllegalArgumentException {

        if (multicastSocket == null)
            throw new NullPointerException("Multicast Socket is null");

        if (msg.isEmpty() || msg == null)
            throw new IllegalArgumentException("Message is not valid");

        byte[] m = msg.getBytes();

        DatagramPacket messageOut = new DatagramPacket(m, m.length, groupInet, groupPortNumber);

        multicastSocket.send(messageOut);

    }

    private void updateReputationRank(Map<String, String> msgMap) {

        String sender = msgMap.get("sender");
        repkeeper.updateFile(sender);
    }

    private String decryptMsg(Map<String, String> msgMap) {

        String peerPublicKeyAsString;
        try {
            peerPublicKeyAsString = retrievePeerPublicKey(msgMap);
        } catch (Exception e) {
            System.out.println("Error decrypting msg, sender is unknown");
            e.printStackTrace();
            return null;
        }
        
        String msgAsString = msgMap.get("msg");
        String decryptedMsg = crypto.decryptText(peerPublicKeyAsString, msgAsString);

        return decryptedMsg;

    }

    private String retrievePeerPublicKey(Map<String, String> msgMap) throws Exception {
        String sender = msgMap.get("sender");
        String peerPublicKeyAsString = knownPeers.get(sender);
        
        if(peerPublicKeyAsString == null){
            throw new Exception("Could not find peer public key");
        }

        return peerPublicKeyAsString;
    }

    public static void addPeerToKnownPeersMap(Map<String, String> msgMap) {
                
        knownPeers.putIfAbsent(msgMap.get("sender"), msgMap.get("publicKey"));        
        System.out.println("Updated peers map: add. Total known peers: "+ knownPeers.size());
        
    }

    public static void removePeerFromKnownPeersMap(Map<String, String> msgMap) {
                        
        knownPeers.remove(msgMap.get("sender"));
        System.out.println("Updated peers map: remove. Total known peers: "+ knownPeers.size());
        
    }

    

	public void sendGoodbye() {
        try {

            String goodbyeMsg = msgBuilder.buildGoodbyeMsg();
            sendStringToGroup(goodbyeMsg);

        } catch (Exception e) {
            System.out.println("Error while sending goodbye");
            e.printStackTrace();
        }
	}

    public void exit() {
        isActive = false;

        sendGoodbye();

        try {
            multicastSocket.leaveGroup(groupInet);
        } catch (IOException e) {            
            e.printStackTrace();
        }

        if (multicastSocket != null)
            multicastSocket.close();

        unicast.closeServer();
    }

}