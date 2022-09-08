
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private Socket socket;
    private int port;
    private String clientIP;
    private Scanner inClient;

    public ServerSocketLib() {
    }

    public boolean isSocketConnect() {
        return socketConnect;
    }

    public boolean isClientConnect() {
        return clientConnect;
    }

    public void closeAll() {
        if ((socket != null) && (clientConnect)) {
            try {
                inClient.close();
                socket.close();
            } catch (IOException ex) {
            }
        }

        if ((serverSocket != null) && (socketConnect)) {
            try {
                serverSocket.close();
            } catch (IOException ex) {
            }
        }

        this.clientIP = "";
        this.port = 0;
        socketConnect = false;
        clientConnect = false;
    }

    public boolean startConnection(int port) {
        if (socketConnect == false) {
            this.port = port;
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
                this.socket = this.serverSocket.accept();
                this.clientIP = this.socket.getInetAddress().getHostAddress();

                clientConnect = true;
                this.inClient = new Scanner(this.socket.getInputStream());
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

        //        Socket s = ss.accept();
        //        DataInputStream din = new DataInputStream(s.getInputStream());
        //        DataOutputStream dout = new DataOutputStream(s.getOutputStream());
        //        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    }

    public String getClientIP() {
        return clientIP;
    }

    public String receive() {
        if (this.inClient.hasNextLine()) {
            return this.inClient.nextLine();
        } else {
            return "";
        }
    }
    
    public boolean hasNextMessage() {
        return true;
        //return this.inClient.hasNextLine();
    }
    
}
