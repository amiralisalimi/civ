package ir.ap.view;

import java.util.regex.Matcher;

import com.google.gson.JsonObject;

public class ProfileMenuView extends AbstractMenuView {

    public enum Command {
        ENTER_MENU("menu enter (?<menuName>\\w+)"),
        EXIT_MENU("menu exit"),
        SHOW_MENU("menu show-current"),
        CHANGE("profile change (?<args>.*)");

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
        NICKNAME_ARG("(--nickname|-n)"),
        PASSWORD_ARG("(--password|-p)"),

        CURRENT_PASSWORD_ARG("(--current|-c)"),
        NEW_PASSWORD_ARG("(--new|-w)"),

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

        USERNAME_INVALID("username invalid"),
        NICKNAME_INVALID("nickname invalid"),
        NEW_PASSWORD_INVALID("mew password invalid"),

        DUPLICATE_PASSWORD("please enter a new password"),

        USERNAME_CHANGED("username changed successfully!"),
        NICKNAME_CHANGED("nickname changed successfully"),

        INVALID_CREDENTIALS("password incorrect"),

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
        if (nextMenu == Menu.MAIN) {
            return responseAndGo(null, Menu.MAIN);
        } else
            return responseAndGo(Message.MENU_NAVIGATION_IMPOSSIBLE, Menu.PROFILE);
    }

    public Menu exitMenu(Matcher matcher) {
        return responseAndGo(null, Menu.MAIN);
    }

    public Menu showMenu(Matcher matcher) {
        return responseAndGo("Profile Menu", Menu.PROFILE);
    }

    public Menu change(Matcher matcher) {
        String[] args = matcher.group("args").trim().split("\\s+");
        for (int i = 0; i < args.length; i++) {
            if (args[i].matches(Validator.NICKNAME_ARG.toString())) {
                if (i + 1 == args.length || !args[++i].matches(Validator.NICKNAME.toString())) {
                    responseAndGo(Message.NICKNAME_INVALID, Menu.PROFILE);
                    continue;
                }
                JsonObject response = USER_CONTROLLER.changeNickname(currentUsername, args[i]);
                String msg = getField(response, "msg", String.class);
                if (msg != null)
                    responseAndGo(msg, Menu.PROFILE);
                else
                    return responseAndGo(Message.E500, Menu.PROFILE);
            } else if (args[i].matches(Validator.PASSWORD_ARG.toString())) {
                String oldPassword = null, newPassword = null;
                int j;
                for (j = i + 1; j < Math.min(args.length, i + 5); j++) {
                    if (args[j].matches(Validator.CURRENT_PASSWORD_ARG.toString())) {
                        if (j + 1 < args.length) {
                            oldPassword = args[++j];
                        }
                    } else if (args[j].matches(Validator.NEW_PASSWORD_ARG.toString())) {
                        if (j + 1 < args.length && args[j + 1].matches(Validator.PASSWORD.toString())) {
                            newPassword = args[++j];
                        }
                    } else {
                        return responseAndGo(Message.ARG_INVALID.toString().replace("%s", args[j]), Menu.PROFILE);
                    }
                }
                i = j - 1;
                if (oldPassword == null)
                    return responseAndGo(Message.INVALID_CREDENTIALS, Menu.PROFILE);
                else if (newPassword == null)
                    return responseAndGo(Message.NEW_PASSWORD_INVALID, Menu.PROFILE);
                else if (oldPassword.equals(newPassword))
                    return responseAndGo(Message.DUPLICATE_PASSWORD, Menu.PROFILE);
                JsonObject response = USER_CONTROLLER.changePassword(currentUsername, oldPassword, newPassword);
                String msg = getField(response, "msg", String.class);
                if (msg != null) {
                    responseAndGo(msg, Menu.PROFILE);
                } else {
                    return responseAndGo(Message.E500, Menu.PROFILE);
                }
            } else {
                return responseAndGo(Message.ARG_INVALID.toString().replace("%s", args[i]), Menu.PROFILE);
            }
        }
        return responseAndGo(null, Menu.PROFILE);
    }
}
