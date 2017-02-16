import com.sun.deploy.util.SessionState;
import java.lang.Object;

import java.io.IOException;
import java.net.*;


public class client {

    public DatagramSocket socket;
    public DatagramPacket datagramPacketResponse;
    public DatagramPacket datagramPacketSend;
    static int TIMEOUT_SENDING = 2000;
    public InetAddress inetAddress;
    public int port;
    byte [] str;
    byte [] stringToReceive;

    public client() throws SocketException{
        //Send Request
        socket = new DatagramSocket();
    }


    public void sendRequest() throws IOException{
        datagramPacketSend = new DatagramPacket(str,str.length,inetAddress,port);
        socket.send(datagramPacketSend);
        receiveRequest();
    }

    public void receiveRequest() throws IOException{
        stringToReceive = new byte[str.length];
        datagramPacketResponse = new DatagramPacket(stringToReceive,stringToReceive.length,inetAddress,port);

        socket.setSoTimeout(TIMEOUT_SENDING);

        try {
            socket.receive(datagramPacketResponse);
        } catch (IOException e) {
            sendRequest();
        }

    }

    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.out.println("Usage: java Echo <hostname> <port> <string to echo>");
            return;
        }

        client clientObject = new client();

        clientObject.inetAddress = InetAddress.getByName(args[0]);
        clientObject.port = Integer.parseInt(args[1]);
        clientObject.str = args[2].getBytes();

        clientObject.sendRequest();

        //Display Response
        String response = new String(clientObject.datagramPacketResponse.getData());

        System.out.println("Register Answer");
        System.out.println(response);

        clientObject.socket.close();

    }
}