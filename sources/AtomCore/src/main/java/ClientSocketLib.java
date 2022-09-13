
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author andre
 */
public class ClientSocketLib {

    private Socket clientSocket;
    private PrintWriter doutMessage;
    private final List<ServerSocketLib.Message> messages;
    private ServerHandler serverHandler;

    public ClientSocketLib() {
        messages = new ArrayList<ServerSocketLib.Message>();
    }

    public boolean isSocketConnect() {
        return (clientSocket != null) && (!clientSocket.isClosed());
    }

    public boolean startConnection(String clientIP, int port) {
        if (!isSocketConnect()) {
            try {
                clientSocket = new Socket(clientIP, port);
                doutMessage = new PrintWriter(clientSocket.getOutputStream(), true);

                serverHandler = new ServerHandler(clientSocket);
                serverHandler.start();

                return true;
            } catch (IOException ex) {
                System.out.println("startConnection Error: " + ex.getMessage());
            }
            return false;
        } else {
            return true;
        }
    }

    public boolean closeConnection() {
        if (isSocketConnect()) {
            try {
                serverHandler.closeServer();

                if (isSocketConnect()) {
                    clientSocket.shutdownOutput();
                    doutMessage.close();
                    clientSocket.close();
                }
                return true;
            } catch (IOException ex) {
                System.out.println("closeConnection Error: " + ex.getMessage());
            }
            return false;
        } else {
            return true;
        }
    }

    public void sendMessage(String msg) {
        if (isSocketConnect()) {
            doutMessage.println(msg);
        }
    }

    private class ServerHandler extends Thread {

        private final Socket serverSocket;
        private BufferedReader in;
        private boolean stopThread = false;

        public ServerHandler(Socket socket) {
            this.serverSocket = socket;
        }

        public Socket getClientSocket() {
            return serverSocket;
        }

        public void run() {
            try {
                System.out.println("Server connected: " + this.serverSocket.getInetAddress().getHostAddress());
                in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    if (stopThread) {
                        break;
                    }
                    messages.add(ServerSocketLib.Message.add(this.serverSocket.getInetAddress().getHostAddress(), inputLine));
                }
                in.close();
                serverSocket.close();
            } catch (IOException ex) {
            }
        }

        public void closeServer() {
            try {
                stopThread = true;
                System.out.println("Server diconnected: " + this.serverSocket.getInetAddress().getHostAddress());
                serverSocket.shutdownInput();
                in.close();
            } catch (IOException ex) {
                System.out.println("closeAll: " + ex.getMessage());
            }
        }

    }

    public ServerSocketLib.Message receive() {
        if (hasNextMessage()) {
            ServerSocketLib.Message msg = messages.get(messages.size() - 1);
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

}
