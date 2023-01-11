package ir.ap.client;

import com.google.gson.JsonObject;
import ir.ap.client.network.Request;
import ir.ap.client.network.RequestHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.net.Socket;

public class LoginView extends View {

    @FXML
    private TextField loginUsername;
    @FXML
    private TextField loginPassword;
    @FXML
    private TextField signupUsername;
    @FXML
    private TextField signupNickname;
    @FXML
    private TextField signupPassword;

    @FXML
    private Label signupMsgLabel;
    @FXML
    private Label loginMsgLabel;

    public void login() {
        String username = loginUsername.getText();
        String password = loginPassword.getText();
        if(username.equals("")) {
            error(loginMsgLabel, "username field is empty");
            return;
        }
        if(password.equals("")) {
            error(loginMsgLabel, "password field is empty");
            return;
        }
        JsonObject response = send("login", username,password);
        String msg = getField(response, "msg", String.class);
        if (!responseOk(response)) {
            error(loginMsgLabel, msg);
            return;
        }
        currentUsername = username;
        authToken = response.get("authToken").getAsString();
        if(msg != null) {
            success(loginMsgLabel, msg);
            currentUsername = username;
            try {
                inviteHandler = new RequestHandler(new Socket("localhost", App.SERVER_PORT));
                JsonObject inviteResponse = inviteHandler.send(new Request("setInviteHandler", authToken, currentUsername));
                if (!responseOk(inviteResponse)) {
                    System.out.println("Unable to set invite handler");
                } else {
                    View.initializeInviteHandler();
                }
            } catch (Exception e) {
                System.out.println("Unable to set invite handler");
            }
            enterMain();
        }
    }

    public void signup() {
        String username = signupUsername.getText();
        String password = signupPassword.getText();
        String nickname = signupNickname.getText();
        if(username.equals("")) {
            error(signupMsgLabel, "username field is empty");
            return;
        }
        if(password.equals("")) {
            error(signupMsgLabel, "password field is empty");
            return;
        }
        JsonObject response = send("register", username,nickname,password);
        String msg = getField(response, "msg", String.class);
        if(msg != null) {
            if (responseOk(response))
                success(signupMsgLabel, msg);
            else
                error(signupMsgLabel, msg);
        }
    }

    public void error(Label label, String msg) {
        label.setText(msg);
        label.setTextFill(Color.RED);
    }

    public void success(Label label, String msg) {
        label.setText(msg);
        label.setTextFill(Color.GREEN);
    }
}
