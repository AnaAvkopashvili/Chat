import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;

public class ChatServer {
    private ServerSocket serverSocket;
    public ChatServer(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }

    public void startServer(){
        try{
            while(!serverSocket.isClosed()){
                System.out.println("Server is waiting on port " + serverSocket.getLocalPort());
                Socket socket = serverSocket.accept();
                System.out.println(LocalTime.now() + "A new client joined the chat" );
                ClientHelper ClientHelper = new ClientHelper (socket);
                Thread thread = new Thread(ClientHelper);
                thread.start();
            }
        }
        catch (IOException e){
            System.out.println("connection refused");
        }
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                System.out.println("Error");
            }
        }
    }

    public static void main(String[] args) throws Exception{
        ServerSocket serverSocket;
        if (args.length == 0) {
            serverSocket = new ServerSocket(3000);
        } else {
            serverSocket = new ServerSocket(Integer.parseInt(args[0]));
        }
        ChatServer server = new ChatServer(serverSocket);
        server.startServer();
    }
}