package ir.ap.network;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class RequestHandler {

    private final Socket socket;
    private final Gson gson;
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;

    public RequestHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.gson = new Gson();
        this.inputStream = new DataInputStream(socket.getInputStream());
        this.outputStream = new DataOutputStream(socket.getOutputStream());
    }

    public void send(JsonObject response) throws IOException {
        String responseJsonStr = gson.toJson(response);
        outputStream.writeUTF(responseJsonStr);
    }

    public JsonObject read() throws IOException {
        String responseJsonStr = inputStream.readUTF();
        return gson.fromJson(responseJsonStr, JsonObject.class);
    }

    public void close() {
        System.out.println("Connection closed on socket " + socket);
        try {
            socket.close();
        } catch (Exception e) {
            System.out.println("Unable to terminate socket " + socket);
        }
    }
}
