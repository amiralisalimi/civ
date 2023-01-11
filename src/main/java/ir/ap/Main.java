package ir.ap;

import ir.ap.controller.GameController;
import ir.ap.controller.UserController;
import ir.ap.network.Server;

public class Main {
    private static final int SERVER_PORT = 8000;

    public static void main(String[] args) {
        GameController.initAll();
        UserController.readUsers();
        GameController.readCityNames();
        Server server = new Server(SERVER_PORT);
        try {
            server.start();
        } catch (Exception e) {
            System.out.println("Unable to start the server");
        }
    }
}
