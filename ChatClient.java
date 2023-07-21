import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private String username;

    public ChatClient(Socket socket, String username){
        try{
            this.socket = socket;
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.in = new BufferedReader((new InputStreamReader(socket.getInputStream())));
            this.username = username;

        }catch (IOException e) {
            closeAll(socket, in, out);
        }
    }
    public void sending(){
        try {
            out.write(username);
            out.newLine();
            out.flush();
            Scanner scanner = new Scanner(System.in);
            while(socket.isConnected()){
                String s = scanner.nextLine();
                out.write(s);
                out.newLine();
                out.flush();
            }
        } catch (IOException e){
            closeAll(socket, in, out);
        }
    }

    public void waitingForMessage(){
        new Thread(() -> {
            String messageFromChat;

            while(socket.isConnected()){
                try{
                    messageFromChat = in.readLine();
                    System.out.println(messageFromChat);
                }catch (IOException e ){
                    closeAll(socket, in, out);
                }
            }
        }).start();
    }

    public void closeAll(Socket socket, BufferedReader in, BufferedWriter out){
        try{
            if(in != null)
                in.close();
            if(out != null )
                out.close();
            if(socket != null)
                socket.close();

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception{
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username: ");
        String username = scanner.nextLine();
        System.out.println("WELCOME!");
        System.out.println("Instructions: ");
        System.out.println("1. simply type the message to send to all active clients");
        System.out.println("2. type '@username<blank>message' without quotes to sent a message to desired client");
        System.out.println("3. type 'WHOIS' without quotes to get the list of all active clients");
        System.out.println("4. type 'LOGOUT' without quotes to log of the server");
        System.out.println("5. type 'PINGU' without quotes to get a random fact about penguins");
        System.out.println("6. type 'SPAM:<blank>number<blank>message' without quotes to repeat your message however many times you want"); // bonus function :)
        System.out.println("7. type 'NICKNAME:<blank>nickname to change your username to preferred nickname"); // bonus function :)
        Socket socket;
        if (args.length > 0) {
            socket = new Socket(args[1], Integer.parseInt(args[0]));
        } else {
            socket = new Socket("localhost", 3000);
        }
        ChatClient client = new ChatClient(socket,username);
        client.waitingForMessage();
        client.sending();
    }
}