package ir.ap.client;

import com.google.gson.*;
import ir.ap.client.components.UserSerializer;
import ir.ap.client.network.Request;
import ir.ap.client.network.RequestHandler;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;

public abstract class View {
    protected static Socket socket;
    protected static RequestHandler requestHandler;
    protected static RequestHandler inviteHandler;

    protected static String authToken;

    protected static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
                private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

                @Override
                public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return LocalDateTime.parse(json.getAsString(), formatter);
                }
            })
            .registerTypeAdapter(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
                private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

                @Override
                public JsonElement serialize(LocalDateTime localDateTime, Type srcType, JsonSerializationContext context) {
                    return new JsonPrimitive(formatter.format(localDateTime));
                }
            })
            .create();

    protected static String currentUsername = null;
    protected static View currentView;

    protected static final ArrayList<Image> avatars = new ArrayList<>();

    static {
        try {
            socket = new Socket("localhost", App.SERVER_PORT);
            requestHandler = new RequestHandler(socket);
        } catch (IOException e) {
            System.out.println("Unable to connect to the server");
        }
        readAvatars();
    }

    protected static void initializeInviteHandler() {
        Thread inviteHandlerThread = new Thread(() -> {
            while (true) {
                try {
                    JsonObject inviteJson = inviteHandler.read();
                    System.out.println("New invite message");
                    String sender = inviteJson.get("username").getAsString();
                    String type = inviteJson.get("type").getAsString();
                    if (type.equals("request")) {
                        Platform.runLater(() -> {
                            Alert enterGameAlert = new Alert(Alert.AlertType.CONFIRMATION, "Would you accept the invitation?", ButtonType.NO, ButtonType.YES);
                            enterGameAlert.setTitle("Invitation to game");
                            enterGameAlert.setHeaderText("You have a new invitation");
                            enterGameAlert.setContentText(sender + " has invited you to start a new game. Would you like to proceed to the game?");
                            Optional<ButtonType> response = enterGameAlert.showAndWait();
                            boolean accepted = response.isPresent() && response.get() == ButtonType.YES;
                            JsonObject respondInvitation = send("respondInvitation", currentUsername, sender, accepted);
                            if (accepted && responseOk(respondInvitation)) {
                                currentView.enterLaunchGame();
                            }
                        });
                    } else if (type.equals("response")) {
                        if (currentView instanceof LaunchGameView) {
                            ((LaunchGameView) currentView).getInvitePlayersView().respondInvitation(sender, inviteJson.get("accepted").getAsBoolean());
                        }
                    } else if (type.equals("removed")) {
                        currentView.enterMain();
                    } else if (type.equals("enterGame")) {
                        currentView.enterGame();
                    } else if (type.equals("online") || type.equals("offline")) {
                        if (currentView instanceof ScoreboardView) {
                            for (UserSerializer user : ((ScoreboardView) currentView).networkModeTable.getItems()) {
                                if (user.getUsername().equals(sender)) {
                                    user.setIsLogin(type.equals("online"));
                                    break;
                                }
                            }
                        }
                    } else if (type.equals("nextTurn")) {
                        if (currentView instanceof GameView) {
                            GameView gameView = (GameView) currentView;
                            Platform.runLater(() -> { gameView.nextTurn(); });
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Connection lost for invite handler on socket " + socket);
                    return;
                }
            }
        });
        inviteHandlerThread.setDaemon(true);
        inviteHandlerThread.start();
    }

    private static void readAvatars() {
        Image avatarImg = new Image(View.class.getResource("png/avatar.png").toExternalForm());
        PixelReader pixelReader = avatarImg.getPixelReader();
        int avatarImgWidth = (int) avatarImg.getWidth(), avatarImgHeight = (int) avatarImg.getHeight();
        int colMargin = 40, rowMargin = 30, imgWidth = 875, imgHeight = 900;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                int tx = (imgWidth + colMargin) * i;
                int ty = (imgHeight + rowMargin) * j;
                Image curImg = new WritableImage(pixelReader, tx, ty,
                        Math.min(avatarImgWidth - tx, imgWidth + colMargin),
                        Math.min(avatarImgHeight - ty, imgHeight + rowMargin));
                avatars.add(curImg);
            }
        }
    }

    public static Image getAvatar(int index) {
        return avatars.get(index);
    }

    protected static JsonObject send(String methodName, Object... params) {
        Request request = new Request(methodName, authToken, params);
        JsonObject response = null;
        try {
            response = requestHandler.send(request);
        } catch (Exception e) {
            System.out.println("Connection lost with server");
        }
        return response;
    }

    protected static boolean responseOk(String response) {
        return response != null && GSON.fromJson(response, JsonObject.class)
                .get("ok").getAsBoolean();
    }

    protected static boolean responseOk(JsonObject response) {
        return response != null && response.has("ok") && response.get("ok").getAsBoolean();
    }

    protected static <T> T getField(JsonObject response, String fieldName, Class<T> classOfT) {
        if (response == null)
            return null;
        JsonElement field = response.get(fieldName);
        if (field == null)
            return null;
        if (classOfT.equals(String.class))
            return classOfT.cast(field.getAsString());
        if (classOfT.equals(Integer.class))
            return classOfT.cast(field.getAsInt());
        if (classOfT.equals(Boolean.class))
            return classOfT.cast(field.getAsBoolean());
        if (classOfT.equals(Double.class))
            return classOfT.cast(field.getAsDouble());
        throw new RuntimeException();
    }

    protected static boolean isLogin() {
        return currentUsername != null;
    }

    public void exit() {
        logout();
        App.exit();
    }

    public void logout() {
        send("logout", currentUsername);
        currentUsername = null;
        authToken = null;
        try {
            inviteHandler.close();
        } catch (Exception e) {
            System.out.println("Unable to close invite handler");
        }
        inviteHandler = null;
        enterLogin();
    }

    private void enterLogin() {
        try {
            App.setRoot("fxml/login-view.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void enterProfile() {
        try {
            App.setRoot("fxml/profile-view.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void enterSettings() {
    }

    public void enterMain() {
        try {
            App.setRoot("fxml/main-view.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void enterGame() {
        try {
            App.setRoot("fxml/game-view.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void enterLaunchGame() {
        try {
            App.setRoot("fxml/launch-game-view.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
