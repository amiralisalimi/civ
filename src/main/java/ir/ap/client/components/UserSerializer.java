package ir.ap.client.components;

import ir.ap.client.View;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UserSerializer implements Serializable {
    private WritableObjectProperty<String> username;
    private WritableObjectProperty<String> nickname;
    private WritableObjectProperty<Integer> maxScore;
    private WritableObjectProperty<ImageView> avatar;
    private WritableObjectProperty<LocalDateTime> lastLogin;
    private WritableObjectProperty<Boolean> isLogin;

    private transient WritableObjectProperty<Boolean> accepted;

    public UserSerializer(String username) {
        this.username = new WritableObjectProperty<>(username);
        this.avatar = new WritableObjectProperty<>();
        this.accepted = new WritableObjectProperty<>(false);
        this.nickname = new WritableObjectProperty<>();
        this.maxScore = new WritableObjectProperty<>();
        this.isLogin = new WritableObjectProperty<>(false);
    }

    public UserSerializer(String username, boolean accepted) {
        this(username);
        this.accepted = new WritableObjectProperty<Boolean>(accepted);
    }

    public UserSerializer(String username, String nickname, int maxScore) {
        this(username);
        this.nickname = new WritableObjectProperty<>(nickname);
        this.maxScore = new WritableObjectProperty<>(maxScore);
    }

    public UserSerializer(String username, String nickname, int maxScore, boolean accepted) {
        this(username, nickname, maxScore);
        this.accepted = new WritableObjectProperty<Boolean>(accepted);
    }

    public UserSerializer(UserSerializerS userSerializerS) {
        this.username = new WritableObjectProperty<>(userSerializerS.getUsername());
        this.nickname = new WritableObjectProperty<>(userSerializerS.getNickname());
        this.maxScore = new WritableObjectProperty<>(Integer.valueOf(userSerializerS.getMaxScore()));
        this.accepted = new WritableObjectProperty<>(false);
        this.avatar = new WritableObjectProperty<>();
        setAvatar(View.getAvatar(userSerializerS.getAvatarIndex()));
        this.lastLogin = new WritableObjectProperty<>(LocalDateTime.parse(userSerializerS.getLastLogin(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        this.isLogin = new WritableObjectProperty<>(userSerializerS.isLogin());
    }

    public WritableObjectProperty<String> usernameProperty() {
        return username;
    }

    public WritableObjectProperty<String> nicknameProperty() {
        return nickname;
    }

    public WritableObjectProperty<Integer> maxScoreProperty() {
        return maxScore;
    }

    public WritableObjectProperty<Boolean> acceptedProperty() {
        return accepted;
    }

    public WritableObjectProperty<ImageView> avatarProperty() {
        return avatar;
    }

    public WritableObjectProperty<LocalDateTime> lastLoginProperty() {
        return lastLogin;
    }

    public WritableObjectProperty<Boolean> isLoginProperty() {
        return isLogin;
    }

    public String getUsername() {
        return username.get();
    }

    public String getNickname() {
        return nickname.get();
    }

    public Integer getMaxScore() {
        return maxScore.get();
    }

    public void setAccepted(boolean acc) {
        accepted.set(acc);
    }

    public Boolean getAccepted() {
        return accepted.get();
    }

    public ImageView getAvatar() {
        return avatar.get();
    }

    public LocalDateTime getLastLogin() {
        return lastLogin.get();
    }

    public void setAvatar(Image img) {
        ImageView imgView = new ImageView(img);
        imgView.setPreserveRatio(true);
        imgView.setFitHeight(80);
        avatar.set(imgView);
    }

    public Boolean getIsLogin() {
        return isLogin.get();
    }

    public void setIsLogin(Boolean isLogin) {
        this.isLogin.set(isLogin);
    }
}