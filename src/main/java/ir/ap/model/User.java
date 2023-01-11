package ir.ap.model;

import ir.ap.controller.GameController;
import ir.ap.network.RequestHandler;
import ir.ap.network.SocketHandler;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class User implements Serializable {
    private static ArrayList< User > users = new ArrayList<>();
    private String username ;
    private String nickname ; 
    private String password ;
    private int score ;
    private int maxScore;
    private int LastWin_s;
    private int LastWin_m;
    private int LastWin_h;
    private LocalDateTime lastLogin;
    private boolean isLogin ;
    private int id ;
    private int avatarIndex;

    private transient SocketHandler socketHandler;
    private transient RequestHandler inviteHandler;
    private transient String authToken;
    
    public User(String username, String nickname, String password) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        score = 0;
        isLogin = false;
    }
    
    public User(String username, String nickname, String password, int score, boolean isLogin) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.score = score;
        this.isLogin = isLogin;
    }

    public static void resetUsers() {
        users = new ArrayList<>();
    }
    
    public static ArrayList<User> getUsers() {
        return users;
    }

    public static User getUserBySocketHandler(SocketHandler socketHandler) {
        if (socketHandler == null)
            return null;
        for (User user : users) {
            if (socketHandler.equals(user.getSocketHandler()))
                return user;
        }
        return null;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getNickname() {
        return nickname;
    }
    
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    
    public int getScore() {
        return score;
    }
    
    public void setScore(int score) {
        this.score = score;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(int maxScore) {
        this.maxScore = maxScore;
    }

    public int getLastWin_s() {
        return LastWin_s;
    }

    public void setLastWin_s(int lastWin_s) {
        LastWin_s = lastWin_s;
    }

    public int getLastWin_m() {
        return LastWin_m;
    }

    public void setLastWin_m(int lastWin_m) {
        LastWin_m = lastWin_m;
    }

    public int getLastWin_h() {
        return LastWin_h;
    }

    public void setLastWin_h(int lastWin_h) {
        LastWin_h = lastWin_h;
    }

    public boolean isLogin() {
        return isLogin;
    }
    
    public void setLogin(boolean isLogin) {
        this.isLogin = isLogin;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAvatarIndex() {
        return avatarIndex;
    }

    public void setAvatarIndex(int avatarIndex) {
        this.avatarIndex = avatarIndex;
    }

    public void setSocketHandler(SocketHandler socketHandler) {
        this.socketHandler = socketHandler;
    }

    public SocketHandler getSocketHandler() {
        return socketHandler;
    }

    public void setGameController(GameController gameController) {
        socketHandler.setGameController(gameController);
    }

    public GameController getGameController() {
        return socketHandler.getGameController();
    }

    public RequestHandler getInviteHandler() {
        return inviteHandler;
    }

    public void setInviteHandler(RequestHandler inviteHandler) {
        this.inviteHandler = inviteHandler;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    @Override
    public boolean equals( Object user ) {
        if( user != null && user instanceof User && ((User)user).getUsername() != null && this.username != null && ((User)user).getUsername().equals( this.username ) )return true ;
        else return false ;
    }
    
    public static boolean hasUser( User user ){
        for(int i = 0 ; i < users.size() ; i ++){
            if( users.get( i ).equals( user ) )return true ;
        }
        return false ;
    }
    
    public static boolean hasUser( String username ){
        for(int i = 0 ; i < users.size() ; i ++){
            if( users.get( i ).getUsername().equals( username ) )return true;
        }
        return false;
    }
    
    public static boolean usernameExists(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username))
            return true;
        }
        return false;
    }
    
    public static boolean nicknameExists(String nickname) {
        for (User user : users) {
            if (user.getNickname().equals(nickname))
            return true;
        }
        return false;
    }
    
    public static User getUser( String username ){
        for(int i = 0 ; i < users.size() ; i ++){
            if( users.get( i ).getUsername().equals( username ) )return users.get( i );
        }
        return null;
    }
    
    public boolean checkPassword( String password ){
        if( this.password.equals( password ) )return true;
        else return false;
    }
    
    public static boolean addUser( User user ){
        if( hasUser( user ) == true )return false;
        users.add( user ) ;
        return true;
    }

}
