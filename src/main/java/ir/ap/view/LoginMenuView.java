package ir.ap.view;

import java.util.regex.Matcher;

import com.google.gson.JsonObject;

public class LoginMenuView extends AbstractMenuView {

    public enum Command {
        ENTER_MENU("menu enter (?<menuName>\\w+)"),
        EXIT_MENU("menu exit"),
        SHOW_MENU("menu show-current"),
        REGISTER("user create (?<args>.*)"),
        LOGIN("user login (?<args>.*)");

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
        USERNAME_ARG("(--username|-u)"),
        NICKNAME_ARG("(--nickname|-n)"),
        PASSWORD_ARG("(--password|-p)"),

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
        MENU_NAVIGATION_IMPOSSIBLE("menu navigation is not possible"),

        USER_CREATED("user created successfully"),
        USER_WITH_USERNAME_EXISTS("user with username %s already exists"),
        USER_WITH_NICKNAME_EXISTS("user with nickname %s already exists"),
        USERNAME_INVALID("username invalid"),
        NICKNAME_INVALID("nickname invalid"),
        PASSWORD_INVALID("password invalid"),

        USER_LOGGED_IN("user logged in successfully"),
        USER_NOT_LOGGED_IN("please login first"),
        USER_ALREADY_LOGGED_IN("please logout first"),
        INVALID_CREDENTIALS("Username and password didn't match"),

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
        String menuName = matcher.group("menuName");
        Menu nextMenu = Menu.getMenuByName(menuName);
        if (!isLogin()) {
            return responseAndGo(Message.USER_NOT_LOGGED_IN, Menu.LOGIN);
        } else if (nextMenu != Menu.MAIN) {
            return responseAndGo(Message.MENU_NAVIGATION_IMPOSSIBLE, Menu.LOGIN);
        } else {
            return responseAndGo(null, Menu.MAIN);
        }
    }

    public Menu exitMenu(Matcher matcher) {
        return responseAndGo(null, null);
    }

    public Menu showMenu(Matcher matcher) {
        return responseAndGo("Login Menu", Menu.LOGIN);
    }

    public Menu register(Matcher matcher) {
        String username = null, nickname = null, password = null;
        String[] args = matcher.group("args").trim().split("\\s+");
        for (int i = 0; i < args.length; i++) {
            if (args[i].matches(Validator.USERNAME_ARG.toString())) {
                if (i + 1 == args.length || !args[i + 1].matches(Validator.USERNAME.toString())) {
                    return responseAndGo(Message.USERNAME_INVALID, Menu.LOGIN);
                } else {
                    username = args[++i];
                }
            } else if (args[i].matches(Validator.PASSWORD_ARG.toString())) {
                if (i + 1 == args.length || !args[i + 1].matches(Validator.PASSWORD.toString())) {
                    return responseAndGo(Message.PASSWORD_INVALID, Menu.LOGIN);
                } else {
                    password = args[++i];
                }
            } else if (args[i].matches(Validator.NICKNAME_ARG.toString())) {
                if (i + 1 == args.length || !args[i + 1].matches(Validator.NICKNAME.toString())) {
                    return responseAndGo(Message.NICKNAME_INVALID, Menu.LOGIN);
                } else {
                    nickname = args[++i];
                }
            } else {
                return responseAndGo(Message.ARG_INVALID.toString().replace("%s", args[i]), Menu.LOGIN);
            }
        }
        if (username == null)
            return responseAndGo(Message.USERNAME_INVALID, Menu.LOGIN);
        else if (nickname == null)
            return responseAndGo(Message.NICKNAME_INVALID, Menu.LOGIN);
        else if (password == null)
            return responseAndGo(Message.PASSWORD_INVALID, Menu.LOGIN);
        JsonObject response = USER_CONTROLLER.register(username, nickname, password);
        String msg = getField(response, "msg", String.class);
        if (msg != null)
            return responseAndGo(msg, Menu.LOGIN);
        else
            return responseAndGo(Message.E500, Menu.LOGIN);
    }

    public Menu login(Matcher matcher) {
        if (isLogin())
            return responseAndGo(Message.USER_ALREADY_LOGGED_IN, Menu.LOGIN);
        String username = null, password = null;
        String[] args = matcher.group("args").trim().split("\\s+");
        for (int i = 0; i < args.length; i++) {
            if (args[i].matches(Validator.USERNAME_ARG.toString())) {
                if (i + 1 == args.length || !args[i + 1].matches(Validator.USERNAME.toString())) {
                    return responseAndGo(Message.USERNAME_INVALID, Menu.LOGIN);
                } else {
                    username = args[++i];
                }
            } else if (args[i].matches(Validator.PASSWORD_ARG.toString())) {
                if (i + 1 == args.length || !args[i + 1].matches(Validator.PASSWORD.toString())) {
                    return responseAndGo(Message.PASSWORD_INVALID, Menu.LOGIN);
                } else {
                    password = args[++i];
                }
            } else {
                return responseAndGo(Message.ARG_INVALID.toString().replace("%s", args[i]), Menu.LOGIN);
            }
        }
        if (username == null)
            return responseAndGo(Message.USERNAME_INVALID, Menu.LOGIN);
        else if (password == null)
            return responseAndGo(Message.PASSWORD_INVALID, Menu.LOGIN);
        else {
            JsonObject response = USER_CONTROLLER.login(username, password, null);
            if (!responseOk(response)) {
                return responseAndGo(Message.INVALID_CREDENTIALS, Menu.LOGIN);
            } else {
                currentUsername = username;
                return responseAndGo(Message.USER_LOGGED_IN, Menu.MAIN);
            }
        }
    }
}
