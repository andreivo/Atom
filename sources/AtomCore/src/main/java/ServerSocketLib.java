
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author andre.ivo
 */
public class ServerSocketLib {

    private ServerSocket serverSocket;
    private boolean socketConnect = false;
    private boolean clientConnect = false;
    private List<ClientHandler> clients;
    private List<Message> messages;
    
    public ServerSocketLib() {
        clients = new ArrayList<ClientHandler>();
        messages = new ArrayList<Message>();
    }

    public boolean isSocketConnect() {
        return socketConnect;
    }

    public boolean isClientConnect() {
        return clientConnect;
    }

    public void closeAll() {
        if (clientConnect) {
            try {                
                for(ClientHandler cc : clients ){
                 cc.getClientSocket().close();  
                 cc.stop();
                }                
            } catch (IOException ex) {
            }
        }

        if ((serverSocket != null) && (socketConnect)) {
            try {
                serverSocket.close();
            } catch (IOException ex) {
            }
        }

        socketConnect = false;
        clientConnect = false;
    }

    public boolean startConnection(int port) {
        if (socketConnect == false) {
            try {
                this.serverSocket = new ServerSocket(port);
                socketConnect = true;
                return true;
            } catch (IOException e) {
                System.out.println("startConnection Error: " + e.getMessage());
            }
            return false;
        } else {
            return true;
        }
    }

    public boolean accept(int timeout) {
        if (clientConnect == false) {
            try {
                this.serverSocket.setSoTimeout(timeout);
                Socket socket = this.serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket);
                clients.add(clientHandler);
                clientHandler.start();
                clientConnect = true;
                return true;
            } catch (SocketException e) {
                clientConnect = false;
                System.out.println("Accept Timed Out (" + timeout + " milisec)!");
            } catch (IOException ex) {
                clientConnect = false;
                System.out.println("Accept Error: " + ex.getMessage());
            }
            return false;
        } else {
            return true;
        }
    }
    
    private static class Message{
        private String clientIP;
        private String message;

        public Message(String clientIP, String message) {
            this.clientIP = clientIP;
            this.message = message;
        }
        
        public static Message add(String clientIP, String message){
            return new Message(clientIP, message);        
        }

        public String getClientIP() {
            return clientIP;
        }

        public void setClientIP(String clientIP) {
            this.clientIP = clientIP;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
        
    }

    private class ClientHandler extends Thread {
        private Socket clientSocket;
        private DataInputStream in;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;            
        }

        public Socket getClientSocket() {
            return clientSocket;
        }

        public void run() {
            try {
                System.out.println("Client connected: " + this.clientSocket.getInetAddress().getHostAddress());
                
                in = new DataInputStream(clientSocket.getInputStream());

                String inputLine;
                while ((inputLine = in.readUTF()) != null) {
                    if ("quit".equals(inputLine)) {
                        messages.add(Message.add(this.clientSocket.getInetAddress().getHostAddress(), "Client disconnected"));
                        break;
                    }
                    messages.add(Message.add(this.clientSocket.getInetAddress().getHostAddress(), inputLine));
                }

                in.close();
                clientSocket.close();
            } catch (IOException ex) {
            }
        }
    }

    public String receive() {
        if(hasNextMessage()){
            String msg =  messages.get(messages.size()-1).clientIP + ": " +messages.get(messages.size()-1).message;
            messages.remove(messages.size()-1);
            return msg;
        }else{
            return "";
        }
    }

    public boolean hasNextMessage() {
        return messages.size()>0;
    }
}
