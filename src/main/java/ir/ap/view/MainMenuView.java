package ir.ap.view;

import java.util.ArrayList;
import java.util.regex.Matcher;

import com.google.gson.JsonObject;

public class MainMenuView extends AbstractMenuView {

    public enum Command {
        ENTER_MENU("menu enter (?<menuName>\\w+)"),
        EXIT_MENU("menu exit"),
        SHOW_MENU("menu show-current"),
        LOGOUT("user logout"),
        PLAY_GAME("play game (?<args>.*)");

        private final String regex;

        Command(String regex) {
            this.regex = regex;
        }

        @Override
        public String toString() {
            return "\\s*" + regex.replace(" ", "\\s+") + "\\s*";
        }
    }

    public enum Validator {
        PLAYER_ARG("(--player|-p)\\d+"),

        USERNAME("\\w+");

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
        MENU_NAVIGATION_IMPOSSIBLE("menu navigation is not possible"),

        USERNAME_INVALID("invalid username %s"),
        PLAYERS_INVALID("invalid players"),

        USER_LOGGED_OUT("user logged out successfully"),

        ARG_INVALID("argument %s invalid"),
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

    public Enum<?>[] getCommands() {
        return Command.values();
    }

    public Menu enterMenu(Matcher matcher) {
        Menu nextMenu = Menu.getMenuByName(matcher.group("menuName"));
        if (nextMenu == Menu.LOGIN) {
            return responseAndGo(null, Menu.LOGIN);
        } else if (nextMenu == Menu.PROFILE) {
            return responseAndGo(null, Menu.PROFILE);
        } else {
            return responseAndGo(Message.MENU_NAVIGATION_IMPOSSIBLE, Menu.MAIN);
        }
    }

    public Menu exitMenu(Matcher matcher) {
        return responseAndGo(null, Menu.LOGIN);
    }

    public Menu showMenu(Matcher matcher) {
        return responseAndGo("Main Menu", Menu.MAIN);
    }

    public Menu logout(Matcher matcher) {
        if (responseOk(USER_CONTROLLER.logout(currentUsername))) {
            currentUsername = null;
            return responseAndGo(Message.USER_LOGGED_OUT, Menu.LOGIN);
        } else {
            return responseAndGo(Message.E500, Menu.MAIN);
        }
    }

    public Menu playGame(Matcher matcher) {
        ArrayList<String> players = new ArrayList<>();
        players.add(currentUsername);
        String[] args = matcher.group("args").trim().split("\\s+");
        for (int i = 0; i < args.length; i++) {
            if (!args[i].matches(Validator.PLAYER_ARG.toString())) {
                return responseAndGo(Message.ARG_INVALID.toString().replace("%s", args[i]), Menu.MAIN);
            } else if (i + 1 == args.length || !args[i + 1].matches(Validator.USERNAME.toString())) {
                return responseAndGo(Message.PLAYERS_INVALID, Menu.MAIN);
            } else {
                players.add(args[++i]);
            }
        }
        String[] usersInGame = players.toArray(new String[0]);
        JsonObject response = GAME_CONTROLLER.newGame(usersInGame);
        String msg = getField(response, "msg", String.class);
        if (responseOk(response)) {
            System.out.format("New game started with players:\n");
            for (String username : players) {
                JsonObject civJson = GAME_CONTROLLER.getCivilizationByUsername(username);
                String civName = getField(civJson, "civName", String.class);
                if (civName == null) {
                    return responseAndGo(Message.E500, Menu.MAIN);
                }
                System.out.format("\t%s as civ %s\n", username, civName);
            }
            ((GameMenuView) Menu.GAME.getMenuView()).init(usersInGame, response);
            return responseAndGo(null, Menu.GAME);
        } else {
            return responseAndGo(msg, Menu.MAIN);
        }
    }
}
