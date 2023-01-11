package ir.ap.client;

import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.File;

public class ProfileView extends View {

    @FXML
    private Label messagePassword;
    @FXML
    private Label messageNickname;
    @FXML
    private TextField oldPassword;
    @FXML
    private TextField newPassword;
    @FXML
    private TextField nickname;
    @FXML
    private GridPane avatarsGrid;
    @FXML
    private Label messageLabel;

    private String cssBorderingDarkBlue = "-fx-border-color: darkblue ; \n"
            + "-fx-border-insets:5;\n"
            + "-fx-border-radius:10;\n"
            + "-fx-border-width:3.0";
    private String cssBorderingGreen = "-fx-border-color: green ; \n"
            + "-fx-border-insets:5;\n"
            + "-fx-border-radius:10;\n"
            + "-fx-border-width:3.0";
    private String cssBorderingYellow = "-fx-border-color: yellow ; \n"
            + "-fx-border-insets:5;\n"
            + "-fx-border-radius:10;\n"
            + "-fx-border-width:3.0";

    private int selectedAvatar = -1;
    private HBox[][] avatars = new HBox[4][4];

    public void initialize() {
        initializeAvatars();
    }

    private void initializeAvatars() {
        JsonObject avatarJson = send("getAvatar", currentUsername);
        int avatarIndex = avatarJson.get("index").getAsInt();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                final int currentIndex = 4 * i + j;
                ImageView avatarImgView = new ImageView(View.getAvatar(currentIndex));
                avatarImgView.setPreserveRatio(true);
                avatarImgView.setFitHeight(80);
                HBox wrapper = new HBox();
                wrapper.getChildren().add(avatarImgView);
                if (currentIndex == avatarIndex)
                    wrapper.setStyle(cssBorderingGreen);
                else
                    wrapper.setStyle(cssBorderingDarkBlue);
                avatars[i][j] = wrapper;
                wrapper.setOnMouseClicked(e -> {
                    setSelectedAvatar(currentIndex);
                });
                avatarsGrid.add(wrapper, i, j);
            }
        }
    }

    private void setSelectedAvatar(int index) {
        selectedAvatar = index;
        reloadAvatarStrokes();
    }

    private void reloadAvatarStrokes() {
        JsonObject avatarJson = send("getAvatar", currentUsername);
        int avatarIndex = avatarJson.get("index").getAsInt();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                final int currentIndex = 4 * i + j;
                if (currentIndex == selectedAvatar)
                    avatars[i][j].setStyle(cssBorderingYellow);
                else if (currentIndex == avatarIndex)
                    avatars[i][j].setStyle(cssBorderingGreen);
                else
                    avatars[i][j].setStyle(cssBorderingDarkBlue);
            }
        }
    }

    public void saveAvatar() {
        if (selectedAvatar != -1) {
            JsonObject response = send("setAvatar", currentUsername, selectedAvatar);
            if (responseOk(response)) {
                success(messageLabel, getField(response, "msg", String.class));
                selectedAvatar = -1;
                reloadAvatarStrokes();
            } else
                error(messageLabel, "Unable to set the avatar");
        } else
            error(messageLabel, "Select an avatar first");
    }

    public void onSavePassword() {
        String OldPassword = oldPassword.getText();
        String NewPassword = newPassword.getText();

        JsonObject response = send("changePassword", currentUsername,OldPassword,NewPassword);
        String Message = getField(response, "msg", String.class);
        if(Message != null) {
            if(responseOk(response))
                success(messagePassword, Message);
            else
                error(messagePassword, Message);
        }
    }
    public void onSaveNickname()
    {
        String Nickname = nickname.getText();
        JsonObject response = send("changeNickname", currentUsername,Nickname);
        String Message = getField(response, "msg", String.class);
        if(Message != null) {
            if(responseOk(response))
                success(messageNickname, Message);
            else
                error(messageNickname, Message);
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
