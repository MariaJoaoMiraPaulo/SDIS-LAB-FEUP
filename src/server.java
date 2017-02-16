import com.sun.org.apache.bcel.internal.generic.RETURN;

import javax.print.attribute.standard.DateTimeAtCompleted;
import java.io.IOException;
import java.net.*;
import java.util.Hashtable;
import java.lang.Object;
import java.util.regex.Pattern;

public class server {

    public static final int MAX_MSG_SIZE = 256;

    private DatagramSocket datagramSocket;
    private int portNumber;
    private InetAddress address;

    private Hashtable<String,String> dataBase;


    public server(int portNumber) throws SocketException {

        this.portNumber = portNumber;
        this.datagramSocket = new DatagramSocket(portNumber);
        this.dataBase = new Hashtable<>();

    }

    String addToDataBase(String plateNumber,String ownerName) {

        String Error = "-1";

        if(verifyPlateNumber(plateNumber)){
            dataBase.put(plateNumber,ownerName);
            return Integer.toString(dataBase.size());
        }
        else return Error;

    }

    public String checkTable(String plateNumber){

        String Error = "NOT_FOUND";

        if(this.dataBase.containsKey(plateNumber))
            return this.dataBase.get(plateNumber);
        else return Error;
    }

    public boolean verifyPlateNumber(String plateNumber){

        if(this.dataBase.contains(plateNumber))
            return false;
        else if( !Pattern.matches("[A-Za-z0-9]{2}-[A-Za-z0-9]{2}-[A-Za-z0-9]{2}", plateNumber))
            return false;

        return true;
    }

    public void answerRegister(String sizeDataBase) throws IOException {

        //Send Request

        byte message[] = sizeDataBase.getBytes();

        DatagramPacket datagramPacketResponse = new DatagramPacket(message,message.length,this.address,this.portNumber);

        this.datagramSocket.send(datagramPacketResponse);
    }

    public void answerLookUP(String plateNumber) throws IOException {

        String resultLookUp = checkTable(plateNumber);

        //Send Request

        byte message[] = resultLookUp.getBytes();

        DatagramPacket datagramPacketResponse = new DatagramPacket(message,message.length,this.address,this.portNumber);

        this.datagramSocket.send(datagramPacketResponse);
    }

    public static void main(String[] args) throws IOException {

        if (args.length != 1) {
            System.out.println("Usage: java Server <port_number>");
            return;
        }

        int port = Integer.parseInt(args[0]);

        server serverObject = new server(port);
        serverObject.listen();

    }

    public int listen() throws IOException{

        while(true){

            //Receive Request
            byte response [] = new byte[MAX_MSG_SIZE];

            DatagramPacket datagramPacket= new DatagramPacket(response,response.length);

            this.datagramSocket.receive(datagramPacket);

            //Data Packet Received contains information about address and port

            this.address = datagramPacket.getAddress();
            this.portNumber = datagramPacket.getPort();

            //Process Request

            String StringReceived = new String(datagramPacket.getData(),0, datagramPacket.getLength());


            StringReceived.trim();

            //OPERATOR:OP1:OP2

            String[] parts = StringReceived.split(":");
            String operator = parts[0];


            if(operator.equals("REGISTER")){
                String plateNumber = parts[1];
                String ownerName = parts[2];

                System.out.println(plateNumber);
                System.out.println("Passo");
                System.out.println(ownerName);

                String sizeDataBase = addToDataBase(plateNumber,ownerName);
                System.out.println(sizeDataBase);
                answerRegister(sizeDataBase);
            }
            else if(operator.equals("LOOKUP")){
                String plateNumber = parts[1];
                answerLookUP(plateNumber);
            }
        }

    }

    }