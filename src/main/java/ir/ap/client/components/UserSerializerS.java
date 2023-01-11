package ir.ap.client.components;

import javafx.scene.image.ImageView;

public class UserSerializerS {
    private String username;
    private String nickname;
    private String maxScore;
    private int avatarIndex;
    private String lastLogin;
    private boolean isLogin;

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public String getMaxScore() {
        return maxScore;
    }
    public void setMaxScore(String maxScore) {
        this.maxScore = maxScore;
    }

    public int getAvatarIndex() {
        return avatarIndex;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public boolean isLogin() {
        return isLogin;
    }
}
