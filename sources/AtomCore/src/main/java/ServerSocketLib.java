
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;
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
    private final List<ClientHandler> clients;
    private final List<Message> messages;
    private PrintWriter doutMessage;

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
            for (ClientHandler cc : clients) {
                cc.closeClient();
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

    public void closeClient(String clientIP) {
        if (clientConnect) {
            for (ClientHandler cc : clients) {
                Socket cS = cc.getClientSocket();
                if (cS.getInetAddress().getHostAddress().equals(clientIP)) {
                    cc.closeClient();
                }
            }
        }
        checkConnectionAlive();
    }

    public void cleanClientSocket() {
        for (Iterator<ClientHandler> cc = clients.iterator(); cc.hasNext();) {
            if (cc.next().getClientSocket().isClosed()) {
                cc.remove();
            }
        }
    }

    public boolean checkConnectionAlive() {
        cleanClientSocket();
        clientConnect = clients.size() > 0;
        return clientConnect;
    }

    public boolean hasActiveClient() {
        cleanClientSocket();
        clientConnect = clients.size() > 0;
        return clientConnect;
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
                doutMessage = new PrintWriter(socket.getOutputStream(), true);

                clientConnect = true;
                return true;
            } catch (SocketException e) {
                checkConnectionAlive();
                System.out.println("Accept Timed Out (" + timeout + " milisec)!");
            } catch (IOException ex) {
                checkConnectionAlive();
                System.out.println("Accept Error: " + ex.getMessage());
            }
            return false;
        } else {
            return true;
        }
    }

    public static class Message {

        private String clientIP;
        private String message;

        public Message(String clientIP, String message) {
            this.clientIP = clientIP;
            this.message = message;
        }

        public static Message add(String clientIP, String message) {
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

        @Override
        public String toString() {
            return clientIP + ": " + message;
        }

    }

    private class ClientHandler extends Thread {

        private final Socket clientSocket;
        private BufferedReader in;
        private boolean stopThread = false;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public Socket getClientSocket() {
            return clientSocket;
        }

        public void run() {
            try {
                System.out.println("Client connected: " + this.clientSocket.getInetAddress().getHostAddress());
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    if (stopThread) {
                        break;
                    }
                    messages.add(Message.add(this.clientSocket.getInetAddress().getHostAddress(), inputLine));
                }
                in.close();
                clientSocket.close();
            } catch (IOException ex) {
            }
        }

        public void closeClient() {
            try {
                stopThread = true;
                System.out.println("Client diconnected: " + this.clientSocket.getInetAddress().getHostAddress());
                in.close();
                clientSocket.close();
            } catch (IOException ex) {
                System.out.println("closeAll: " + ex.getMessage());
            }
        }
    }

    public Message receive() {
        if (hasNextMessage()) {
            Message msg = messages.get(messages.size() - 1);
            messages.remove(messages.size() - 1);
            return msg;
        } else {
            return null;
        }
    }

    public String receiveString() {
        if (hasNextMessage()) {
            String msg = messages.get(messages.size() - 1).toString();
            messages.remove(messages.size() - 1);
            return msg;
        } else {
            return "";
        }
    }

    public boolean hasNextMessage() {
        return messages.size() > 0;
    }

    public void sendMessage(String msg) {
        if (clientConnect) {
            doutMessage.println(msg);
        }
    }
}
