import java.net.*;
import java.util.Map;
import java.io.*;

public class UDPUnicast extends Thread{

    private int listenPort;
    private boolean isActive;    

    private DatagramSocket listenSocket;

    // public UDPUnicast(int listenPort){
    //     this.listenPort = listenPort;        
    // }

    public UDPUnicast() throws SocketException {
        listenSocket = new DatagramSocket();
        listenPort = listenSocket.getLocalPort();

    }

    public void run(){
        isActive = true;
        listen();
    }

    private void listen(){
        //DatagramSocket aSocket = null;
        try{
            
            
            while(isActive){
            
                byte[] buffer = new byte[1000];
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                listenSocket.receive(request);
                
                Map<String,String> msgMap = Helpers.parsePacketDataToMap(request);

                System.out.println("Unicast received");

                Helpers.printMap(msgMap);
                
                MulticastPeer.addPeerToKnownPeersMap(msgMap);
                                
            }
        }catch (IOException e)
        {
            System.out.println("IO at Unicast listen: " + e.getMessage());
        }
        finally

            {
                if (listenSocket != null)
                    listenSocket.close();
            }
    }


    public void sendString (String msg, String destinationHostname, int destinationHostPort){
        
        DatagramSocket aSocket = null;
        try {

            aSocket = new DatagramSocket();
            byte [] m = msg.getBytes();
            InetAddress inetAdress = InetAddress.getByName(destinationHostname);                      
            DatagramPacket request = new DatagramPacket(m, m.length, inetAdress, destinationHostPort);
            aSocket.send(request);
            
        }catch (SocketException e)
        {
            System.out.println("Socket: " + e.getMessage());
        }catch (IOException e)
        {
            System.out.println("IO at Unicast send: " + e.getMessage());
        }
        finally {
            if(aSocket != null) aSocket.close();
        }
    } 

    public void closeServer(){
        isActive = false;
    }

	public int getPort() {
		return listenPort;
	}

}