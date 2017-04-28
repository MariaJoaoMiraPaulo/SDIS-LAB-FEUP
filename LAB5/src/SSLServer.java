import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.regex.Pattern;


public class SSLServer {

    private SSLServerSocketFactory serverSocketFactory = null;
    private SSLServerSocket listener = null;
    private Hashtable<String,String> dataBase;

    public SSLServer(int portNumber, String[] cypherSuite) throws Exception {

        System.out.println("The Server is running.");
        dataBase = new Hashtable<>();

        serverSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();

        listener = (SSLServerSocket) serverSocketFactory.createServerSocket(portNumber);

        // Require client authentication
        listener.setNeedClientAuth(true);
        listener.setEnabledCipherSuites(serverSocketFactory.getSupportedCipherSuites());

    }

    public static void main(String[] args) throws Exception {

        if (args.length < 2) {
            System.out.println("Usage: java SSLServer <port_number> <cypher-suite>*");
            return;
        }

        String[] cypherSuite = new String[args.length-1];
        int i;

        for(i = 1; i < args.length;i++)
            cypherSuite[i-1] = args[i];

        int port = Integer.parseInt(args[0]);
        SSLServer ServerObject = new SSLServer(port, cypherSuite);
        ServerObject.runServer();

    }

    public void runServer() throws IOException{

        try {
            while (true){
                new ServerThread(listener.accept(),dataBase).start();
            }
        }
        finally {
            listener.close();
        }

    }

    private static class ServerThread extends Thread{
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private String response;
        private Hashtable<String,String> dataBase;

        public ServerThread(Socket socketServer,Hashtable<String,String> databaseServer) throws IOException{
            socket = socketServer;
            dataBase = databaseServer;
            System.out.println("New conection with client");
        }

        public void run(){

            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                response = in.readLine();
                analyseString(response);
            }
            catch (IOException e){
                System.out.println("Error handling client");
            }
            finally {
                try {
                    socket.close();
                }
                catch (IOException e ){
                    System.out.println("Error closing socket");
                }
            }
        }

        public void analyseString(String response) throws IOException{

            String[] parts = response.split(":");
            String operator = parts[0];

            //OPERATOR:OP1:OP2
            if(operator.equals("REGISTER")){
                String plateNumber = parts[1];
                String ownerName = parts[2];

                System.out.println("Plate Number" + plateNumber);
                System.out.println("Owner Name" +ownerName);

                String sizeDataBase = addToDataBase(plateNumber,ownerName);
                System.out.println("DataBase Size:" + sizeDataBase);
                answerRegister(sizeDataBase);
            }
            else if(operator.equals("LOOKUP")){
                String plateNumber = parts[1];
                System.out.println("Plate Number" + plateNumber);
                answerLookUP(plateNumber);
            }

            System.out.println("SSLServer:" +  operator + parts);
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
            out.println(sizeDataBase);
        }

        public void answerLookUP(String plateNumber) throws IOException {
            String resultLookUp = checkTable(plateNumber);
            out.println(resultLookUp);
        }
    }
}