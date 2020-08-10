import java.net.*;
import java.util.Map;
import java.io.*;

public class UDPUnicast extends Thread{

    int listenPort;
    private boolean isActive;
    Helpers helpers;

    public UDPUnicast(int listenPort){
        this.listenPort = listenPort;
        helpers = new Helpers();
    }

    public void run(){
        isActive = true;
        listen();
    }

    public void listen(){
        DatagramSocket aSocket = null;
        try{
            aSocket = new DatagramSocket(listenPort);
            
            while(isActive){
            
                byte[] buffer = new byte[1000];
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request);
                
                Map<String,String> msgMap = Helpers.parsePacketDataToMap(request.getData());

                System.out.println("Unicast received");

                Helpers.printMap(msgMap);
                
                MulticastPeer.addPeerToKnownPeersMap(msgMap);
                                
            }
        }catch (SocketException e)
        {System.out.println("Socket: " + e.getMessage());
        }catch (IOException e)
        {
            System.out.println("IO: " + e.getMessage());
        }
        finally

            {
                if (aSocket != null)
                    aSocket.close();
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
            System.out.println("IO: " + e.getMessage());
        }
        finally {
            if(aSocket != null) aSocket.close();
        }
    } 

    public void closeServer(){
        isActive = false;
    }

}