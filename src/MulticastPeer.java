import java.net.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.io.*;
import java.lang.management.ManagementFactory;

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

    public MulticastPeer(String groupIp, int port, int unicastListenPort, Cryptography crypto) {

        this.groupIp = groupIp;
        this.groupPortNumber = port;
        this.myUnicastPort = unicastListenPort;
        this.crypto = crypto;
        isActive = false;

        myUserName = ManagementFactory.getRuntimeMXBean().getName();

        knownPeers = new LinkedHashMap<String, String>();
        msgBuilder = new MsgBuilder(myUserName);
        repkeeper = new ReputationKeeper();

        // Start Reputation keeper
        repkeeper.startKeeper();

        // Start Unicast server (in a new thread)
        unicast = new UDPUnicast(unicastListenPort);
        unicast.start();

    }

    public void startPeer() {
        try {
            crypto.generateRSAKkeyPair();
            myPublicKey = crypto.getPublicKeyAsString();
            joinMulticastGroup();
            sendGreeting();
            this.start();
        } catch (Exception e) {
            System.out.println("Error while initiating peer");
            e.printStackTrace();
        }
    }

    public void run() {
        listenToGroup();
    }

    public void joinMulticastGroup() throws IOException {

        groupInet = InetAddress.getByName(groupIp);
        multicastSocket = new MulticastSocket(groupPortNumber);
        multicastSocket.joinGroup(groupInet);

        isActive = true;
    }

    public void listenToGroup() {

        while (isActive) {

            byte[] buffer = new byte[1000];

            DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
            try {
                multicastSocket.receive(messageIn);
            } catch (IOException e) {
                System.out.println("Error while listening to group");
                e.printStackTrace();
            }

            handleMessage(messageIn);

        }

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

    /**
     * Decides next steps depending on the map receive
     * 
     * @param map
     */
    private void handleMessage(DatagramPacket messageIn) {

        Map<String, String> msgMap = Helpers.parsePacketDataToMap(messageIn.getData());

        if (msgMap.get("sender").equals(myUserName)) {
            return;
        }

        Helpers.printMap(msgMap);

        switch (msgMap.get("msgType")) {
            case "Greeting":
                addPeerToKnownPeersMap(msgMap);
                sendGreetingBack(msgMap);
                break;

            case "News":
                String decryptedMsg = decryptMsg(msgMap);
                System.out.println("Decrypted News: " + decryptedMsg + "\n");
                String fakeNewsSubject = FakeNewsAnalyzer.containsFakeNews(decryptedMsg);
                if(fakeNewsSubject != null){
                    sendFakeNewsWarning(msgMap, fakeNewsSubject);
                    updateReputationRank(msgMap);
                }
            default:
                break;
        }

    }



    private void updateReputationRank(Map<String, String> msgMap) {
        
        String sender = msgMap.get("sender");
        repkeeper.updateFile(sender);
    }

    private String decryptMsg(Map<String, String> msgMap) {

        String sender = msgMap.get("sender");
        String msgAsString = msgMap.get("msg");
        String peerPublicKeyAsString = knownPeers.get(sender);
        
        String decryptedMsg = crypto.decryptMsg(peerPublicKeyAsString, msgAsString);

        return decryptedMsg;

    }

    public static void addPeerToKnownPeersMap(Map<String, String> msgMap) {
                
        knownPeers.putIfAbsent(msgMap.get("sender"), msgMap.get("publicKey"));        
        System.out.println("Updated peers map. Total known peers: "+ knownPeers.size());
        
    }

    public void exit() {
        isActive = false;

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