import com.sun.org.apache.xpath.internal.operations.Mult;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

public class MulticastServerThread {

    public static MulticastSocket multicastSocket;
    public int portService;
    public int multicastPort;
    public String multicastIPAddress;
    public InetAddress srvc_addr;
    public DatagramPacket packet;

    public static final int TIME_SLEEPING = 1000;


    public MulticastServerThread(int port, String MulticastIPaddress,int MulticastPort) throws IOException {

        portService = port;
        multicastPort = MulticastPort;
        multicastIPAddress = MulticastIPaddress;
        multicastSocket = new MulticastSocket();

        InetAddress group = InetAddress.getByName(multicastIPAddress);

        String portToSend = Integer.toString(portService);

        byte message [] = portToSend.getBytes();

        packet = new DatagramPacket(message,message.length,group,multicastPort);

        srvc_addr = packet.getAddress();

    }

    public void printInformation(InetAddress srvc_addr) {
        System.out.println("< MCast_addr = "+multicastIPAddress + " >");
        System.out.println("< MCast_port = "+ multicastPort + " >");
        System.out.println("< srvc_addr = "+ srvc_addr + " >");
        System.out.println("< srvc_port = "+ portService + " >");
    }

    public void run() throws IOException{

        ScheduledThreadPoolExecutor repetitiveTask = new ScheduledThreadPoolExecutor(1);
        repetitiveTask.scheduleAtFixedRate(() -> {

            try{
                multicastSocket.send(packet);
            }
            catch (IOException e){
                System.out.print("Error");
            }

            printInformation(srvc_addr);

        }, 0,1, TimeUnit.SECONDS);


       }


}