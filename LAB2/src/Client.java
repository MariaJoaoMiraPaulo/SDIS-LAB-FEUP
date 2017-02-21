import java.io.IOException;
import java.net.*;


public class Client {

    public MulticastSocket multicastSocket;
    public DatagramSocket socket;
    public DatagramPacket datagramPacketResponse;
    public DatagramPacket datagramPacketSend;
    public DatagramPacket datagramPacketGetPort;
    static int TIMEOUT_SENDING = 2000;

    public InetAddress MultiCastInetAddress;
    public int MultiCastPort;

    public InetAddress inetAddress;
    public int port;

    byte [] str;
    byte [] stringToReceive;

    public Client() throws SocketException{
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

    public void receivePortByMulticast() throws IOException{

        stringToReceive = new byte[str.length];

        datagramPacketGetPort = new DatagramPacket(stringToReceive,stringToReceive.length);

        multicastSocket.receive(datagramPacketGetPort);

        String portString = new String(datagramPacketGetPort.getData(),0,datagramPacketGetPort.getLength());

        port = Integer.parseInt(portString);

        inetAddress = datagramPacketGetPort.getAddress();

    }

    public static void main(String[] args) throws IOException {

        //java client <mcast_addr> <mcast_port> <oper> <opnd> *

        if (args.length != 3) {
            System.out.println("Usage: java client <mcast_addr> <mcast_port> <oper> <opnd> *");
            return;
        }

        System.setProperty("java.net.preferIPv4Stack", "true");

        Client clientObject = new Client();

        clientObject.MultiCastInetAddress = InetAddress.getByName(args[0]);
        clientObject.MultiCastPort = Integer.parseInt(args[1]);
        clientObject.str = args[2].getBytes();
        System.out.println(args[0]);

        clientObject.multicastSocket = new MulticastSocket(clientObject.MultiCastPort);
        clientObject.multicastSocket.joinGroup(clientObject.MultiCastInetAddress);


        clientObject.receivePortByMulticast();
        clientObject.sendRequest();

        //Display Response
        String response = new String(clientObject.datagramPacketResponse.getData());

        System.out.println("Register Answer");
        System.out.println(response);

        clientObject.socket.close();

    }
}