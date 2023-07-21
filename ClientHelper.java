import java.io.*;
import java.net.Socket;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringJoiner;

public class ClientHelper implements Runnable {
    public static ArrayList<ClientHelper> clientsArrayList = new ArrayList<>();
    public static HashMap<String, String> clientsMap = new HashMap<>();
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private String clientUsername;

    public ClientHelper(Socket socket) {
        try {
            this.socket = socket;
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = in.readLine();
            clientsArrayList.add(this);
            clientsMap.put(clientUsername, String.valueOf(LocalTime.now()));
            display( clientUsername + " has entered the chat! ");

        } catch (IOException e) {
            closeAll(socket, in, out);
        }
    }

    @Override
    public void run() {
        String messageFromClient;
        while (socket.isConnected()) {
            try {
                messageFromClient = in.readLine();
                display(messageFromClient);
            } catch (IOException e) {
                closeAll(socket, in, out);
                break;
            }
        }
    }

    public void display(String s) {
        if (s.equals("PINGU")) {
            for (ClientHelper clientHelper : clientsArrayList) {
                if (clientsArrayList.contains(this)) {
                    try {
                        clientHelper.out.write("Many male penguins gift female penguins with rocks in order to woo them.");
                        clientHelper.out.newLine();
                        clientHelper.out.flush();
                    } catch (IOException e) {
                        closeAll(socket, in, out);
                    }
                }
            }
        } else if (s.equals("WHOIS")) {
            for (ClientHelper clientHelper : clientsArrayList) {
                try {
                    if (clientHelper.clientUsername.equals(clientUsername)) {
                        clientHelper.out.write(clientsMap.toString());
                        clientHelper.out.newLine();
                        clientHelper.out.flush();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        } else if (s.equals("LOGOUT")) {
            removeClient();

        } else if (s.charAt(0) == '@') {
            String[] splitted = s.split(" ");
            String user = splitted[0].substring(1);
            String[] newArr = new String[splitted.length - 1];
            int n = 0;
            StringJoiner sj = new StringJoiner(" ");
            for (int i = 0; i < newArr.length; i++) {
                newArr[n] = splitted[n + 1];
                n++;
                sj.add(newArr[i]);
            }
            String finalMessage = sj.toString();
            for (ClientHelper clientHelper : clientsArrayList) {
                if (clientsArrayList.contains(this)) {
                    if (user.equals(clientHelper.clientUsername)) {
                        try {
                            clientHelper.out.write(clientUsername + ": " + finalMessage);
                            clientHelper.out.newLine();
                            clientHelper.out.flush();
                        } catch (IOException e) {
                            closeAll(socket, in, out);
                        }
                    }
                }
            }

        } else if (s.startsWith("NICKNAME:")) { // bonus function for nicknames :)
            String[] splitted = s.split(" ");
            for (ClientHelper clientHelper : clientsArrayList) {
                if (clientsArrayList.contains(this)) {
                    if (clientHelper.clientUsername.equals(clientUsername)) {
                        clientHelper.clientUsername = splitted[1];
                    }
                }
            }
        } else if (s.startsWith("SPAM:")) { // bonus function to spam the message
            String[] splitted = s.split(" ");
            int number = Integer.parseInt(splitted[1]);
            for (ClientHelper clientHandler : clientsArrayList) {
                if (clientsArrayList.contains(this)) {
                    for (int j = 0; j < number; j++) {
                        try {
                            if (!clientHandler.clientUsername.equals(clientUsername)) {
                                clientHandler.out.write(clientUsername + ": " + splitted[2]);
                                clientHandler.out.newLine();
                                clientHandler.out.flush();
                            }
                        } catch (IOException e) {
                            closeAll(socket, in, out);
                        }
                    }
                }
            }
        }  /*else if (s.startsWith("BLOCK:")) { // bonus function to block the user
            String[] splitted = s.split(" ");
            String[] blockedUsers = splitted[1].split(",");
            for (ClientHelper clientHandler : clientsArrayList) {
                if (clientsArrayList.contains(this)) {
                    try {
                        if (!clientHandler.clientUsername.equals(clientUsername) && !clientHandler.clientUsername.equals(blockedUsers)) {
                            clientHandler.out.write(clientUsername + ": " + s);
                            clientHandler.out.newLine();
                            clientHandler.out.flush();
                        }
                    } catch (IOException e) {
                        closeAll(socket, in, out);
                    }
                }
            } */
         else {
            for (ClientHelper clientHandler : clientsArrayList) {
                if (clientsArrayList.contains(this)) {
                    try {
                        if (!clientHandler.clientUsername.equals(clientUsername)) {
                            clientHandler.out.write(clientUsername + ": " + s);
                            clientHandler.out.newLine();
                            clientHandler.out.flush();
                        }
                    } catch (IOException e) {
                        closeAll(socket, in, out);
                    }
                }
            }
        }
    }
    public void removeClient() {
        clientsArrayList.remove(this);
        clientsMap.remove(clientUsername);
        for (ClientHelper clientHandler : clientsArrayList) {
                try {
                    if (!clientHandler.clientUsername.equals(clientUsername)) {
                        clientHandler.out.write(LocalTime.now() + " " + clientUsername + " has left the chat!");
                        clientHandler.out.newLine();
                        clientHandler.out.flush();
                    }
                } catch (IOException e) {
                    closeAll(socket, in, out);
            }
        }
    }

    public void closeAll(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClient();
        try {
            if (bufferedReader != null)
                bufferedReader.close();
            if (bufferedWriter != null)
                bufferedWriter.close();
            if (socket != null)
                socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}