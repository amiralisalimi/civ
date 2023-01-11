package ir.ap.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private final int port;

    private ServerSocket serverSocket;
    private boolean terminateFlag;

    public Server(int port) {
        this.port = port;
        this.terminateFlag = false;
    }

    public void accept() throws IOException {
        Socket socket = serverSocket.accept();
        new SocketHandler(socket).start();
    }

    public void start() throws IOException {
        if (serverSocket != null && !serverSocket.isClosed())
            serverSocket.close();
        serverSocket = new ServerSocket(port);
        System.out.println("Server listening on port " + port);
        while (!terminateFlag) {
            accept();
        }
    }

    public synchronized void terminate() throws IOException {
        this.terminateFlag = true;
        serverSocket.close();
    }
}
