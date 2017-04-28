import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;



public class SSLClient {


    public String address;
    public int port;
    String stringToSend;
    String StringToReceive;
    String[] cgAlgorithms;
    private BufferedReader in;
    private PrintWriter out;
    private SSLSocketFactory serverSocketFactory;
    private SSLSocket socketTransmission;

    public SSLClient(String host, int Port, String data, String[] cypherSuite) throws IOException{
        address = host;
        port = Port;
        stringToSend = data;
        cgAlgorithms = cypherSuite;

        serverSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        socketTransmission = (SSLSocket) serverSocketFactory.createSocket(host, port);

        socketTransmission.setEnabledCipherSuites(serverSocketFactory.getSupportedCipherSuites());
        in = new BufferedReader(new InputStreamReader(socketTransmission.getInputStream()));
        out = new PrintWriter(socketTransmission.getOutputStream(),true);

    }

    public static void main(String[] args) throws IOException {

        if (args.length < 5) {
            System.out.println("java SSLClient <host> <port> <oper> <opnd>* <cypher-suite>*");
            return;
        }

        String data = null;
        String[] cypherSuite = new String[args.length-4];
        int start = 0;
        int i;
        int j=0;


        if(args[2].equals("REGISTER")){
            data = args[2]+":"+args[3]+":"+args[4];
            start = 5;
        }
        else if(args[2].equals("LOOKUP")){
            data = args[2]+":"+args[3];
            start = 6;
        }

        for (i = start; i<args.length ;i++){
            cypherSuite[j] = args[i];
            j++;
        }

        SSLClient ClientObject = new SSLClient(args[0],Integer.parseInt(args[1]),data,cypherSuite);

        ClientObject.sendRequest();
    }

    public void sendRequest() throws IOException{
        System.out.println(stringToSend);
        out.println(stringToSend);
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