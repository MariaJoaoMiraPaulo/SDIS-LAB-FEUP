import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.*;


public class Client {

    private Socket socketTransmission;
    public String address;
    public int port;
    String string;
    String StringToReceive;
    private BufferedReader in;
    private PrintWriter out;


    public Client(String Address, int Port, String stringToSend) throws IOException{
        address = Address;
        port = Port;
        string = stringToSend;
        socketTransmission = new Socket(address,port);
        in = new BufferedReader(new InputStreamReader(socketTransmission.getInputStream()));
        out = new PrintWriter(socketTransmission.getOutputStream(),true);

    }

    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.out.println("Usage: java Echo <hostname> <port> <string to echo>");
            return;
        }

        Client ClientObject = new Client(args[0],Integer.parseInt(args[1]),args[2]);
        ClientObject.sendRequest();
    }

    public void sendRequest() throws IOException{
        System.out.println(string);
        out.println(string);
        receiveRequest();
    }

    public void receiveRequest() throws IOException{
        try {
            StringToReceive = in.readLine();
        } catch (IOException e) {
            sendRequest();
        }

    }

}