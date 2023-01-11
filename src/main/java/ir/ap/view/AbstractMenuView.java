package ir.ap.view;

import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ir.ap.controller.GameController;
import ir.ap.controller.UserController;

public abstract class AbstractMenuView {
    protected static final Scanner SCANNER = new Scanner(System.in);
    protected static final UserController USER_CONTROLLER = new UserController();
    protected static final GameController GAME_CONTROLLER = new GameController();
    protected static final Gson GSON = new Gson();

    protected static String currentUsername = null;
    protected static Menu currentMenu;

    public static String constantCaseToCamelCase(String constCaseStr) {
        String camelCaseStr = Arrays.stream(constCaseStr.split("_"))
                .map(t -> t.substring(0, 1).toUpperCase() + t.substring(1).toLowerCase())
                .collect(Collectors.joining(""));
        return camelCaseStr.substring(0, 1).toLowerCase() + camelCaseStr.substring(1);
    }

    public static Matcher getCommandMatcher(String regex, String input) {
        Matcher matcher = Pattern.compile(regex).matcher(input);
        return (matcher.matches() ? matcher : null);
    }

    public static boolean responseOk(String response) {
        return response != null && GSON.fromJson(response, JsonObject.class)
                .get("ok").getAsBoolean();
    }

    public static boolean responseOk(JsonObject response) {
        return response != null && response.has("ok") && response.get("ok").getAsBoolean();
    }

    public static <T> T getField(JsonObject response, String fieldName, Class<T> classOfT) {
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

    public static boolean isLogin() {
        return currentUsername != null;
    }

    public static Menu responseAndGo(Object msg, Menu nextMenu) {
        if (msg != null)
            System.out.println(msg);
        return nextMenu;
    }

    public static void run() {
        currentMenu = Menu.LOGIN;
        while ((currentMenu = currentMenu.runCommand()) != null) {
        }
    }

    public Menu runCommand() {
        if (!SCANNER.hasNextLine())
            return null;
        String input = SCANNER.nextLine();
        Matcher matcher;
        for (Enum<?> command : getCommands()) {
            matcher = getCommandMatcher(command.toString(), input);
            if (matcher != null) {
                String methodName = constantCaseToCamelCase(command.name());
                try {
                    return (Menu) this.getClass().getMethod(methodName,
                            Matcher.class)
                            .invoke(this, matcher);
                } catch (Exception ex) {
                    ex.getCause().printStackTrace();
                    throw new RuntimeException();
                }
            }
        }
        System.out.println("invalid command");
        return currentMenu;
    }

    public abstract Enum<?>[] getCommands();

    public abstract Menu enterMenu(Matcher matcher);

    public abstract Menu exitMenu(Matcher matcher);

    public abstract Menu showMenu(Matcher matcher);
}
