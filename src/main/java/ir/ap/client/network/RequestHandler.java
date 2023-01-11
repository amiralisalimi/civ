package ir.ap.client.network;

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

    public JsonObject send(Request request) throws IOException {
        String requestJsonStr = gson.toJson(request);
        outputStream.writeUTF(requestJsonStr);
        return read();
    }

    public JsonObject read() throws IOException {
        String responseJsonStr = inputStream.readUTF();
        return gson.fromJson(responseJsonStr, JsonObject.class);
    }

    public void close() throws IOException {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
}
