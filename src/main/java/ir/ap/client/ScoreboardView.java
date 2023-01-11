package ir.ap.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ir.ap.client.components.UserSerializer;
import ir.ap.client.components.UserSerializerS;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;

public class ScoreboardView extends View {

    @FXML
    TabPane ScoreboardPane;

    @FXML
    TableView<UserSerializer> networkModeTable;
    @FXML
    TableView<UserSerializer> offlineModeTable;
    @FXML
    Label usernameLabel;

    public void initialize() {
        if (currentUsername != null) {
            usernameLabel.setText(currentUsername);
        }
        initializeTable(networkModeTable);
    }

    private void initializeTable(TableView<?> tableView) {
        tableView.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("avatar"));
        tableView.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("username"));
        tableView.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("maxScore"));
        tableView.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("isLogin"));
        tableView.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("lastLogin"));
        JsonObject usersJson = send("getAllUsers");
        ArrayList<UserSerializer> users = new ArrayList<>();
        for (JsonElement userEle : usersJson.get("users").getAsJsonArray()) {
            users.add(new UserSerializer(GSON.fromJson(userEle, UserSerializerS.class)));
        }
        ((ObservableList<UserSerializer>) tableView.getItems()).setAll(users);
    }
}
