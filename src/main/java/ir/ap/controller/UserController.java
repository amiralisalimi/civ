package ir.ap.controller;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Random;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ir.ap.client.network.Request;
import ir.ap.model.User;
import ir.ap.network.SocketHandler;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

public class UserController implements JsonResponsor, AutoCloseable {

    public enum Validator {
        USERNAME("\\w+"),
        NICKNAME("\\w+"),
        PASSWORD("\\S+");

        private final String regex;

        Validator(String regex) {
            this.regex = regex;
        }

        @Override
        public String toString() {
            return regex;
        }
    }

    public enum Message {
        USER_CREATED("user created successfully"),
        USER_WITH_USERNAME_EXISTS("user with username %s already exists"),
        USER_WITH_NICKNAME_EXISTS("user with nickname %s already exists"),
        USERNAME_INVALID("username invalid"),
        NICKNAME_INVALID("nickname invalid"),
        PASSWORD_INVALID("password invalid"),

        USER_LOGGED_IN("user logged in successfully"),
        USER_NOT_LOGGED_IN("user is not logged in"),
        INVALID_CREDENTIALS("Username and password didn't match"),

        PASSWORD_CHANGED("password changed successfully"),
        NICKNAME_CHANGED("nickname changed successfully"),

        E500("Server error");

        private final String msg;

        Message(String msg) {
            this.msg = msg;
        }

        @Override
        public String toString() {
            return msg;
        }
    }

    private static final String PLAYERS_CONF_FILE = "players.json";
    private static final int AVATARS_CNT = 16;
    private static final Random rnd = new Random(System.currentTimeMillis());

    private String getRandomString(int len) {
        final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final String lower = upper.toLowerCase(Locale.ROOT);
        final String digits = "0123456789";
        final String alphanum = upper + lower + digits;
        final char[] symbols = alphanum.toCharArray();
        char[] result = new char[len];
        for (int i = 0; i < len; i++) {
            result[i] = symbols[rnd.nextInt(symbols.length)];
        }
        return new String(result);
    }

    @Override
    public void close() {
        writeUsers();
    }

    public static void readUsers() {
        try (Reader usersReader = new FileReader(PLAYERS_CONF_FILE)) {
            User[] curUsers = GSON.fromJson(usersReader, User[].class);
            for (User user : curUsers) {
                user.setLogin(false);
                User.addUser(user);
            }
        } catch (Exception ex) {
            System.out.println("Unable to read users");
        }
    }

    public void writeUsers() {
        try (Writer usersWriter = new FileWriter(PLAYERS_CONF_FILE)) {
            GSON.toJson(User.getUsers(), usersWriter);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static int getRandomAvatar() {
        return rnd.nextInt(AVATARS_CNT);
    }

    public JsonObject getAllUsers() {
        JsonObject response = new JsonObject();
        response.add("users", GSON.fromJson(GSON.toJson(User.getUsers()), JsonArray.class));
        return setOk(response, true);
    }

    public JsonObject getAvatar(String username) {
        User user = User.getUser(username);
        if (user == null)
            return messageToJsonObj("No such user", false);
        JsonObject response = new JsonObject();
        response.addProperty("index", user.getAvatarIndex());
        return setOk(response, true);
    }

    public JsonObject setAvatar(String username, int avatarIndex) {
        User user = User.getUser(username);
        if (user == null)
            return messageToJsonObj("No such user", false);
        user.setAvatarIndex(avatarIndex);
        writeUsers();
        return messageToJsonObj("Successfully changed avatar", true);
    }

    public JsonObject register(String username, String nickname, String password) {
        if (!username.matches(Validator.USERNAME.toString()))
            return messageToJsonObj(Message.USERNAME_INVALID, false);
        if (!nickname.matches(Validator.NICKNAME.toString()))
            return messageToJsonObj(Message.NICKNAME_INVALID, false);
        if (!password.matches(Validator.PASSWORD.toString()))
            return messageToJsonObj(Message.PASSWORD_INVALID, false);

        if (User.usernameExists(username)) {
            return messageToJsonObj(Message.USER_WITH_USERNAME_EXISTS
                    .toString().replace("%s", username), false);
        }
        if (User.nicknameExists(nickname)) {
            return messageToJsonObj(Message.USER_WITH_NICKNAME_EXISTS
                    .toString().replace("%s", nickname), false);
        }

        User curUser = new User(username, nickname, password);
        if (User.addUser(curUser)) {
            curUser.setAvatarIndex(getRandomAvatar());
            writeUsers();
            return messageToJsonObj(Message.USER_CREATED, true);
        } else
            return messageToJsonObj(Message.E500, false);
    }

    public JsonObject login(String username, String password, SocketHandler socketHandler) {
        User user = User.getUser(username);
        if (user == null || !user.checkPassword(password))
            return messageToJsonObj(Message.INVALID_CREDENTIALS, false);
        if (user.isLogin())
            return messageToJsonObj("Already login", false);
        user.setLogin(true);
        user.setLastLogin(LocalDateTime.now());
        user.setSocketHandler(socketHandler);
        socketHandler.setUser(user);
        user.setAuthToken(getRandomString(20));
        JsonObject onlineJson = new JsonObject();
        onlineJson.addProperty("type", "online");
        onlineJson.addProperty("username", user.getUsername());
        for (User otherUser : User.getUsers()) {
            if (user != otherUser && otherUser.getInviteHandler() != null) {
                try {
                    otherUser.getInviteHandler().send(onlineJson);
                } catch (Exception e) {
                    System.out.println("Connection lost with invite handler for " + otherUser.getUsername());
                }
            }
        }
        writeUsers();
        JsonObject response = new JsonObject();
        response.addProperty("msg", Message.USER_LOGGED_IN.toString());
        response.addProperty("authToken", user.getAuthToken());
        return setOk(response, true);
    }

    public JsonObject logout(String username) {
        User user = User.getUser(username);
        if (user == null || !user.isLogin())
            return messageToJsonObj(Message.USER_NOT_LOGGED_IN, false);
        user.setLogin(false);
        user.setAuthToken(null);
        user.getSocketHandler().setUser(null);
        user.setInviteHandler(null);
        JsonObject onlineJson = new JsonObject();
        onlineJson.addProperty("type", "offline");
        onlineJson.addProperty("username", user.getUsername());
        for (User otherUser : User.getUsers()) {
            if (user != otherUser && otherUser.getInviteHandler() != null) {
                try {
                    otherUser.getInviteHandler().send(onlineJson);
                } catch (Exception e) {
                    System.out.println("Connection lost with invite handler for " + otherUser.getUsername());
                }
            }
        }
        return messageToJsonObj(Message.USER_LOGGED_IN, true);
    }

    public JsonObject changeNickname(String username, String newNickname) {
        User user = User.getUser(username);
        if (user == null || !user.isLogin())
            return messageToJsonObj(Message.USER_NOT_LOGGED_IN, false);
        if (User.nicknameExists(newNickname)) {
            return messageToJsonObj(Message.USER_WITH_NICKNAME_EXISTS
                    .toString().replace("%s", newNickname), false);
        }
        user.setNickname(newNickname);
        writeUsers();
        return messageToJsonObj(Message.NICKNAME_CHANGED, true);
    }

    public JsonObject changePassword(String username, String oldPassword, String newPassword) {
        User user = User.getUser(username);
        if (user == null || !user.isLogin())
            return messageToJsonObj(Message.USER_NOT_LOGGED_IN, false);
        if (!user.checkPassword(oldPassword))
            return messageToJsonObj(Message.INVALID_CREDENTIALS, false);
        user.setPassword(newPassword);
        writeUsers();
        return messageToJsonObj(Message.PASSWORD_CHANGED, true);
    }

    public JsonObject inviteUser(String username, String toInvite) {
        User user = User.getUser(username);
        if (user == null || !user.isLogin())
            return messageToJsonObj(Message.USER_NOT_LOGGED_IN, false);
        User toInviteUser = User.getUser(toInvite);
        if (toInviteUser == null || !toInviteUser.isLogin())
            return messageToJsonObj(Message.USER_NOT_LOGGED_IN, false);
        if (username.equals(toInvite) || user.getGameController().getArrayOfInvitedUsers().contains(toInvite)) {
            return messageToJsonObj("Player already invited", false);
        }
        JsonObject invite = new JsonObject();
        invite.addProperty("type", "request");
        invite.addProperty("username", username);
        if (toInviteUser.getInviteHandler() != null) {
            try {
                toInviteUser.getInviteHandler().send(invite);
            } catch (Exception e) {
                toInviteUser.getInviteHandler().close();
                toInviteUser.setInviteHandler(null);
            }
        }
        return messageToJsonObj("Invitation successful", true);
    }

    public JsonObject respondInvitation(String username, String inviter, boolean accepted) {
        User user = User.getUser(username);
        User inviterUser = User.getUser(inviter);
        if (user == null || inviterUser == null)
            return messageToJsonObj(Message.USER_NOT_LOGGED_IN, false);
        JsonObject respondInvitation = new JsonObject();
        respondInvitation.addProperty("type", "response");
        respondInvitation.addProperty("username", username);
        respondInvitation.addProperty("accepted", accepted);
        GameController gameController = inviterUser.getGameController();
        if (accepted) {
            gameController.addInvitedUser(username);
            user.setGameController(gameController);
        }
        if (inviterUser.getInviteHandler() != null) {
            for (JsonElement usernameEle : gameController.getInvitedUsers().getAsJsonArray("users")) {
                try {
                    String invitedUsername = usernameEle.getAsString();
                    User invitedUser = User.getUser(invitedUsername);
                    invitedUser.getInviteHandler().send(respondInvitation);
                } catch (Exception e) {
                    System.out.println("Connection with player " + usernameEle.getAsString() + " lost");
                }
            }
        }
        return messageToJsonObj("Invitation respond successful", true);
    }
}
