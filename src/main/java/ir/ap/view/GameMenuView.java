package ir.ap.view;

import java.util.ArrayList;
import java.util.regex.Matcher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class GameMenuView extends AbstractMenuView {

    public enum Command {
        ENTER_MENU("menu enter (?<menuName>\\w+)"),
        EXIT_MENU("menu exit"),
        SHOW_MENU("menu show-current"),
        INFO("info (?<args>.*)"),
        RESEARCH("research(?<args>.*)"),
        SELECT_UNIT("select unit (?<type>combat|noncombat) (?<tileId>\\d+)"),
        SELECT_CITY("select city (?<nameOrId>\\S+)"),
        UNIT_ACTION("unit (?<args>.*)"),
        CITY_ACTION("city (?<args>.*)"),
        NEXT_TURN("next turn"),
        INCREASE("increase (?<args>.*)"),
        SHOW_MAP("map show (?<nameOrId>\\S+)"),
        MOVE_MAP("map move (?<dir>R|L|U|D)(?<amount> \\d*)");

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
        ARG_GOLD("--gold"),
        ARG_TURN("--turn"),
        ARG_HAPPINESS("--happiness");

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

        INVALID_REQUEST("invalid request"),
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

    public enum Color {
        ANSI_RESET("\u001B[0m"),

        BG_BLACK("\u001B[40m"),
        BG_RED("\u001B[41m"),
        BG_GREEN("\u001B[42m"),
        BG_YELLOW("\u001B[43m"),
        BG_BLUE("\u001B[44m"),
        BG_PURPLE("\u001B[45m"),
        BG_CYAN("\u001B[46m"),
        BG_WHITE("\u001B[47m"),

        FG_BLACK("\u001B[30m"),
        FG_RED("\u001B[31m"),
        FG_GREEN("\u001B[32m"),
        FG_YELLOW("\u001B[33m"),
        FG_BLUE("\u001B[34m"),
        FG_PURPLE("\u001B[35m"),
        FG_CYAN("\u001B[36m"),
        FG_WHITE("\u001B[37m");

        String str;

        Color(String str) {
            this.str = str;
        }

        public Color getCompatibleColor() {
            switch (this) {
                case BG_BLACK:
                    return FG_WHITE;
                case BG_BLUE:
                    return FG_WHITE;
                case BG_CYAN:
                    return FG_WHITE;
                case BG_GREEN:
                    return FG_WHITE;
                case BG_PURPLE:
                    return FG_WHITE;
                case BG_RED:
                    return FG_WHITE;
                case BG_YELLOW:
                    return FG_BLACK;
                case BG_WHITE:
                    return FG_BLACK;

                case FG_BLACK:
                    return BG_WHITE;
                case FG_BLUE:
                    return BG_WHITE;
                case FG_CYAN:
                    return BG_WHITE;
                case FG_GREEN:
                    return BG_WHITE;
                case FG_PURPLE:
                    return BG_WHITE;
                case FG_RED:
                    return BG_WHITE;
                case FG_YELLOW:
                    return BG_BLACK;
                case FG_WHITE:
                    return BG_BLACK;

                default:
                    return ANSI_RESET;
            }
        }

        @Override
        public String toString() {
            return str;
        }
    }

    class Tile {
        private int x, y;

        public Tile(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void goToDir(int dirId, int amount) {
            switch (dirId) {
                case 0:
                    x -= amount;
                    break;
                case 1:
                    y += amount;
                    break;
                case 2:
                    x += amount;
                    break;
                case 3:
                    y -= amount;
                    break;
            }
        }

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }

        public int getId() {
            JsonObject response = GAME_CONTROLLER.getTileIndexByXY(x, y);
            if (responseOk(response))
                return response.get("id").getAsInt();
            return -1;
        }
    }

    private String[] usersInGame;
    private String[] civsInGame;
    private Tile[] focusTile;
    private int currentTurnId;
    private String currentPlayer;
    private String currentCiv;

    private static final int MAX_H = 100, MAX_W = 100;
    private static final int PLAIN_H = 50, PLAIN_W = 75;
    private String[][] plain = new String[MAX_H][MAX_W];

    public Enum<?>[] getCommands() {
        return Command.values();
    }

    public void init(String[] users, JsonObject response) {
        usersInGame = users;
        civsInGame = new String[usersInGame.length];
        focusTile = new Tile[usersInGame.length];
        JsonArray civsJson = response.get("civs").getAsJsonArray();
        for (int i = 0; i < users.length; i++) {
            civsInGame[i] = getField(GAME_CONTROLLER.getCivilizationByUsername(users[i]),
                    "civName", String.class);
            JsonObject civJson = civsJson.get(i).getAsJsonObject();
            if (civJson.has("initPos")) {
                focusTile[i] = new Tile(civJson.get("initPos").getAsJsonObject().get("x").getAsInt(),
                        civJson.get("initPos").getAsJsonObject().get("y").getAsInt());
            } else {
                focusTile[i] = new Tile(0, 0);
            }
        }
        currentTurnId = 0;
        currentPlayer = usersInGame[currentTurnId];
        currentCiv = civsInGame[currentTurnId];
        printCurrentMap();
        System.out.format("Turn: %s\n========================================\n", getColoredStr(currentCiv, getCivColorById(currentTurnId)));
    }

    public Menu nextTurn(Matcher matcher) {
        JsonObject response = GAME_CONTROLLER.nextTurn(currentPlayer);
        String msg = getField(response, "msg", String.class);
        if (responseOk(response)) {
            ++currentTurnId;
            currentTurnId %= usersInGame.length;
            currentPlayer = usersInGame[currentTurnId];
            currentCiv = civsInGame[currentTurnId];
            if (getField(response, "end", Boolean.class) == true) {
                return responseAndGo(msg, Menu.MAIN);
            }
            printCurrentMap();
            System.out.format("Turn: %s\n========================================\n", getColoredStr(currentCiv, getCivColorById(currentTurnId)));
        }
        return responseAndGo(msg, Menu.GAME);
    }

    public Menu enterMenu(Matcher matcher) {
        return responseAndGo(Message.MENU_NAVIGATION_IMPOSSIBLE, Menu.GAME);
    }

    public Menu exitMenu(Matcher matcher) {
        return responseAndGo(null, Menu.MAIN);
    }

    public Menu showMenu(Matcher matcher) {
        return responseAndGo("Game Menu", Menu.GAME);
    }

    public Menu info(Matcher matcher) {
        String[] args = matcher.group("args").trim().split("\\s+");
        boolean cheat = false;
        for (String arg : args) {
            cheat |= arg.matches("(--cheat|-c)");
        }
        JsonObject response = getInfoResponse(args[0], cheat);
        if (response == null)
            return responseAndGo(Message.ARG_INVALID.toString().replace("%s", args[0]), Menu.GAME);
        if (!responseOk(response)) {
            if (response != null && response.has("msg")) {
                return responseAndGo(response.get("msg").getAsString(), Menu.GAME);
            }
            return responseAndGo(Message.INVALID_REQUEST, Menu.GAME);
        }
        if (response.has("research"))
            infoResearch(response.get("research").getAsJsonObject());
        if (response.has("units"))
            infoUnits(response.get("units").getAsJsonArray());
        if (response.has("cities"))
            infoCities(response.get("cities").getAsJsonArray());
        if (response.has("demographics"))
            infoDemographics(response.get("demographics").getAsJsonObject());
        if (response.has("notifications"))
            infoNotifications(response.get("notifications").getAsJsonArray());
        if (response.has("military"))
            infoMilitary(response.get("military").getAsJsonArray());
        if (response.has("economic"))
            infoEconomic(response.get("economic").getAsJsonArray());
        String msg = getField(response, "msg", String.class);
        // printCurrentMap();
        return responseAndGo(msg, Menu.GAME);
    }

    private void infoResearch(JsonObject researchJson) {
        JsonObject toPrint = researchJson.deepCopy();
        toPrint.addProperty("title", "INFO RESEARCH");
        printResponse(toPrint);
    }

    private void infoUnits(JsonArray unitsJson) {
        System.out.println(" >>> INFO UNITS:");
        if (unitsJson.size() == 0) {
            System.out.println("\tNo units right now.");
            return;
        }
        ArrayList<Integer> unitIds = new ArrayList<>();
        for (int i = 0; i < unitsJson.size(); i++) {
            JsonObject unit = unitsJson.get(i).getAsJsonObject();
            int tileId = unit.get("tileId").getAsInt();
            int unitId = unit.get("id").getAsInt();
            unitIds.add(unitId);
            System.out.format("\t%d - %s on tile %d\n", unitId,
                unit.get("unitType").getAsString(),
                tileId);
        }
        System.out.format("Enter unit id or 'military' or 'end'\n", unitIds.size());
        while (SCANNER.hasNextLine()) {
            String line = SCANNER.nextLine().trim();
            if (line.equals("end"))
                return;
            if (line.equals("military")) {
                infoMilitary(unitsJson);
                return;
            }
            try {
                int unitId = -1;
                unitId = Integer.parseInt(line);
                JsonObject response = GAME_CONTROLLER.selectUnit(currentPlayer, unitId);
                String msg = getField(response, "msg", String.class);
                if (msg != null)
                    System.out.println(msg);
                if (responseOk(response))
                    return;
            } catch (Exception ex) {
                System.out.println("invalid id");
            }
        }
    }

    private void infoCities(JsonArray citiesJson) {
        System.out.println(" >>> INFO CITIES:");
        if (citiesJson.size() == 0) {
            System.out.println("\tNo cities right now.");
            return;
        }
        ArrayList<Integer> cityIds = new ArrayList<>();
        for (int i = 0; i < citiesJson.size(); i++) {
            JsonObject city = citiesJson.get(i).getAsJsonObject();
            int tileId = city.get("tileId").getAsInt();
            int cityId = city.get("id").getAsInt();
            cityIds.add(cityId);
            System.out.format("\t%d - %s on tile %d\n", cityId,
                city.get("name").getAsString(),
                tileId);
        }
        System.out.println("Enter city id or 'economic' or 'end'");
        while (SCANNER.hasNextLine()) {
            String line = SCANNER.nextLine().trim();
            if (line.equals("end"))
                return;
            if (line.equals("economic")) {
                infoEconomic(GAME_CONTROLLER.infoEconomic(currentPlayer)
                    .get("economic").getAsJsonArray());
                return;
            }
            try {
                int cityId = -1;
                cityId = Integer.parseInt(line);
                JsonObject response = GAME_CONTROLLER.selectCityById(currentPlayer, cityId);
                String msg = getField(response, "msg", String.class);
                if (msg != null)
                    System.out.println(msg);
                if (responseOk(response))
                    return;
            } catch (Exception ex) {
                System.out.println("invalid id");
            }
        }
    }

    private void infoDemographics(JsonObject demographicsJson) {
        demographicsJson.addProperty("title", "INFO DEMOGRAPHICS");
        printResponse(demographicsJson);
    }

    private void infoNotifications(JsonArray notificationsJson) {
        JsonObject toPrint = new JsonObject();
        toPrint.addProperty("title", "INFO NOTIFICATIONS");
        toPrint.add("Notifications", notificationsJson);
        printResponse(toPrint);
    }

    private void infoMilitary(JsonArray unitsJson) {
        JsonObject toPrint = new JsonObject();
        toPrint.addProperty("title", "INFO MILITARY");
        toPrint.add("Units", unitsJson);
        printResponse(toPrint);
    }

    private void infoEconomic(JsonArray citiesJson) {
        JsonObject toPrint = new JsonObject();
        toPrint.addProperty("title", "INFO ECONOMIC");
        toPrint.add("Cities", citiesJson);
        printResponse(toPrint);
    }

    public Menu research(Matcher matcher) {
        String[] args = matcher.group("args").trim().split("\\s+");
        boolean cheat = false;
        for (String arg : args) {
            cheat |= arg.matches("(--cheat|-c)");
        }
        System.out.println("========RESEARCH========================\n");
        JsonObject response = null;
        JsonObject latestTechResponse = GAME_CONTROLLER.civGetLatestResearch(currentPlayer);
        if (latestTechResponse.has("latestResearch")) {
            JsonObject latestTech = latestTechResponse.get("latestResearch").getAsJsonObject();
            latestTech.addProperty("title", "LATEST");
            printResponse(latestTech);
        }
        JsonObject availableTechsResponse = GAME_CONTROLLER.civGetAllAvailableResearches(currentPlayer, cheat);
        if (availableTechsResponse.has("technologies")) {
            JsonArray techs = availableTechsResponse.get("technologies").getAsJsonArray();
            JsonObject techsJson = new JsonObject();
            techsJson.add("techs", techs);
            techsJson.addProperty("title", "AVAILABLE TECHS");
            printResponse(techsJson);
        }
        System.out.println("Enter new tech id to set, or 'end':");
        while (SCANNER.hasNextLine()) {
            String line = SCANNER.nextLine().trim();
            if (line.equals("end")) {
                break;
            }
            try {
                int techId = Integer.parseInt(line);
                response = GAME_CONTROLLER.civSetCurrentResearch(currentPlayer, techId, cheat);
                break;
            } catch (Exception ex) {
                System.out.println("invalid id");
                continue;
            }
        }
        return responseAndGo(getField(response, "msg", String.class), Menu.GAME);
    }

    public Menu selectUnit(Matcher matcher) {
        String type = matcher.group("type");
        int tileId = Integer.parseInt(matcher.group("tileId"));
        JsonObject response = (type.equals("combat")
                ? GAME_CONTROLLER.selectCombatUnit(currentPlayer, tileId)
                : GAME_CONTROLLER.selectNonCombatUnit(currentPlayer, tileId));
        if (!responseOk(response))
            return responseAndGo(Message.INVALID_REQUEST, Menu.GAME);
        String msg = getField(response, "msg", String.class);
        printCurrentMap();
        return responseAndGo(msg, Menu.GAME);
    }

    public Menu selectCity(Matcher matcher) {
        String nameOrId = matcher.group("nameOrId");
        JsonObject response;
        if (nameOrId.matches("\\d+")) {
            response = GAME_CONTROLLER.selectCity(currentPlayer, Integer.parseInt(nameOrId));
        } else {
            response = GAME_CONTROLLER.selectCity(currentPlayer, nameOrId);
        }
        if (!responseOk(response))
            return responseAndGo(Message.INVALID_REQUEST, Menu.GAME);
        String msg = getField(response, "msg", String.class);
        printCurrentMap();
        return responseAndGo(msg, Menu.GAME);
    }

    public Menu unitAction(Matcher matcher) {
        String[] args = matcher.group("args").trim().split("\\s+");
        boolean cheat = false;
        for (String arg : args) {
            cheat |= arg.matches("(--cheat|-c)");
        }
        JsonObject response = getUnitActionResponse(args, cheat);
        if (!responseOk(response)) {
            String msg = getField(response, "msg", String.class);
            if (msg != null)
                return responseAndGo(msg, Menu.GAME);
            return responseAndGo(Message.INVALID_REQUEST, Menu.GAME);
        }
        if (response.has("cityDead")) {
            int cityId = response.get("cityDead").getAsInt();
            System.out.println("City dead! Enter 'A' for Annex or 'D' for Destroy");
            while (true) {
                String line = SCANNER.nextLine().trim();
                if (line.equals("A")) {
                    response = GAME_CONTROLLER.cityAnnex(currentPlayer, cityId, cheat);
                    break;
                } else if (line.equals("D")) {
                    response = GAME_CONTROLLER.cityDestroy(currentPlayer, cityId, cheat);
                    break;
                }
            }
        }
        printCurrentMap();
        String msg = getField(response, "msg", String.class);
        return responseAndGo(msg, Menu.GAME);
    }

    public Menu cityAction(Matcher matcher) {
        String[] args = matcher.group("args").trim().split("\\s+");
        boolean cheat = false;
        for (String arg : args) {
            cheat |= arg.matches("(--cheat|-c)");
        }
        JsonObject response = getCityActionResponse(args, cheat);
        if (!responseOk(response)) {
            if (response != null && response.has("msg"))
                return responseAndGo(response.get("msg").getAsString(), Menu.GAME);
            return responseAndGo(Message.INVALID_REQUEST, Menu.GAME);
        }
        printCurrentMap();
        String cityName = GAME_CONTROLLER.getSelectedCity(currentPlayer)
                .get("selectedCity").getAsJsonObject()
                .get("name").getAsString();
        if (response.has("unemployedCitizens")) {
            int unemployedCitizens = response.get("unemployedCitizens").getAsInt();
            JsonObject unemployedCitizensJson = new JsonObject();
            unemployedCitizensJson.addProperty("title", "UNEMPLOYED CITIZENS");
            unemployedCitizensJson.addProperty("City name", cityName);
            unemployedCitizensJson.addProperty("Unemployed citizens", unemployedCitizens);
            printResponse(unemployedCitizensJson);
        }
        if (response.has("production")) {
            JsonObject productionJson = response.get("production").getAsJsonObject().deepCopy();
            productionJson.addProperty("title", "CURRENT PRODUCTION");
            productionJson.addProperty("City name", cityName);
            printResponse(productionJson);
        }
        if (response.has("productions")) {
            JsonObject productionsJson = response.get("productions").getAsJsonObject().deepCopy();
            productionsJson.addProperty("title", "PRODUCTIONS AVAILABLE");
            productionsJson.addProperty("City name", cityName);
            printResponse(productionsJson);
        }
        if (response.has("workingTiles")) {
            JsonArray workingTilesJsonArray = response.get("workingTiles").getAsJsonArray().deepCopy();
            JsonObject workingTilesJson = new JsonObject();
            workingTilesJson.add("workingTiles", workingTilesJsonArray);
            workingTilesJson.addProperty("title", "WORKING TILES");
            workingTilesJson.addProperty("City name", cityName);
            printResponse(workingTilesJson);
        }
        if (response.has("output")) {
            JsonObject outputJson = response.get("output").getAsJsonObject().deepCopy();
            outputJson.addProperty("title", "CITY OUTPUT");
            outputJson.addProperty("City name", cityName);
            printResponse(outputJson);
        }
        if (response.has("buildings")) {
            JsonObject buildingsObj = response.deepCopy();
            buildingsObj.remove("ok");
            buildingsObj.addProperty("title", "CITY BUILDINGS");
            buildingsObj.addProperty("City name", cityName);
            printResponse(buildingsObj);
        }
        String msg = getField(response, "msg", String.class);
        return responseAndGo(msg, Menu.GAME);
    }

    public Menu showMap(Matcher matcher) {
        String nameOrId = matcher.group("nameOrId");
        int tileId = -1;
        String cityName = null;
        if (nameOrId.matches("\\d+")) {
            tileId = Integer.parseInt(nameOrId);
        } else {
            cityName = nameOrId;
        }
        JsonObject response;
        if (cityName != null) {
            response = GAME_CONTROLLER.mapShow(currentPlayer, cityName);
        } else {
            response = GAME_CONTROLLER.mapShow(currentPlayer, tileId);
        }
        if (responseOk(response)) {
            tileId = response.get("focusId").getAsInt();
            int tileX = response.get("focusX").getAsInt();
            int tileY = response.get("focusY").getAsInt();
            focusTile[currentTurnId] = new Tile(tileX, tileY);
            writeMap(response);
            printMap();
        } else
            return responseAndGo(Message.INVALID_REQUEST, Menu.GAME);
        return responseAndGo(null, Menu.GAME);
    }

    public Menu moveMap(Matcher matcher) {
        String dir = matcher.group("dir");
        int amount = 1;
        try {
            amount = Integer.parseInt(matcher.group("amount").trim());
        } catch (Exception ex) {
            amount = 1;
        }
        int dirId;
        switch (dir) {
            case "U":
                dirId = 0;
                break;
            case "R":
                dirId = 1;
                break;
            case "D":
                dirId = 2;
                break;
            case "L":
                dirId = 3;
                break;
            default:
                return responseAndGo(Message.INVALID_REQUEST, Menu.GAME);
        }
        Tile focus = focusTile[currentTurnId];
        focus.goToDir(dirId, amount);
        JsonObject response = GAME_CONTROLLER.mapShow(currentPlayer, focus.getId());
        if (responseOk(response)) {
            writeMap(response);
            printMap();
        } else {
            focus.goToDir((dirId + 2) % 4, amount);
            return responseAndGo(Message.INVALID_REQUEST, Menu.GAME);
        }
        return responseAndGo(null, Menu.GAME);
    }

    public Menu increase(Matcher matcher) {
        String[] args = matcher.group("args").trim().split("\\s+");
        for (int i = 0; i < args.length; i++) {
            if (args[i].matches(Validator.ARG_GOLD.toString())) {
                if (i + 1 == args.length || !args[i + 1].matches("-?\\d+")) {
                    return responseAndGo(Message.INVALID_REQUEST, Menu.GAME);
                } else {
                    int amount = Integer.parseInt(args[++i]);
                    JsonObject response = GAME_CONTROLLER.increaseGold(currentPlayer, amount);
                    if (!responseOk(response)) {
                        System.out.println(Message.E500);
                        continue;
                    }
                    printCurrentMap();
                    String msg = getField(response, "msg", String.class);
                    System.out.println(msg);
                }
            } else if (args[i].matches(Validator.ARG_TURN.toString())) {
                if (i + 1 == args.length || !args[i + 1].matches("\\d+")) {
                    return responseAndGo(Message.INVALID_REQUEST, Menu.GAME);
                } else {
                    int amount = Integer.parseInt(args[++i]);
                    JsonObject response = GAME_CONTROLLER.increaseTurn(currentPlayer, amount);
                    if (!responseOk(response)) {
                        System.out.println(Message.E500);
                        continue;
                    }
                    printCurrentMap();
                    String msg = getField(response, "msg", String.class);
                    if (getField(response, "end", Boolean.class) == true) {
                        return responseAndGo(msg, Menu.MAIN);
                    }
                    if (msg != null)
                        System.out.println(msg);
                }
            } else if (args[i].matches(Validator.ARG_HAPPINESS.toString())) {
                if (i + 1 == args.length || !args[i + 1].matches("\\d+")) {
                    return responseAndGo(Message.INVALID_REQUEST, Menu.GAME);
                } else {
                    int amount = Integer.parseInt(args[++i]);
                    JsonObject response = GAME_CONTROLLER.increaseHappiness(currentPlayer, amount);
                    if (!responseOk(response)) {
                        System.out.println(Message.E500);
                        continue;
                    }
                    printCurrentMap();
                    String msg = getField(response, "msg", String.class);
                    if (msg != null)
                        System.out.println(msg);
                }
            } else {
                return responseAndGo(Message.ARG_INVALID.toString()
                        .replace("%s", args[i]), Menu.GAME);
            }
        }
        return responseAndGo(null, Menu.GAME);
    }

    public void printResponse(JsonObject response) {
        String title = response.get("title").getAsString();
        response.remove("title");
        System.out.format(" >>> %s:\n", title);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(response));
    }

    public void printCurrentMap() {
        writeMap(GAME_CONTROLLER.mapShow(currentPlayer, focusTile[currentTurnId].getId()));
        printMap();
    }

    private void resetPlain() {
        for (int i = 0; i < MAX_H; i++) {
            for (int j = 0; j < MAX_W; j++) {
                plain[i][j] = " ";
            }
        }
    }

    private void printMap() {
        System.out.println("========INFO============================");
        System.out.println("\n >>> TERRAINS:");
        int[] terrainTypeIds = GSON
                .fromJson(GAME_CONTROLLER.getAllTerrainTypeIds().get("terrainTypeIds").getAsJsonArray(), int[].class);
        for (int terrainTypeId : terrainTypeIds) {
            System.out.format("\t%s\n",
                    getColoredStr(
                            GAME_CONTROLLER.getTerrainTypeNameById(terrainTypeId).get("terrainTypeName").getAsString(),
                            getTileColorByTerrainTypeId(terrainTypeId), true));
        }
        System.out.println("\n >>> FEATURES:");
        int[] terrainFeatureIds = GSON.fromJson(
                GAME_CONTROLLER.getAllTerrainFeatureIds().get("terrainFeatureIds").getAsJsonArray(), int[].class);
        for (int terrainFeatureId : terrainFeatureIds) {
            System.out.format("\t%2d: (%s:%s),\n", terrainFeatureId, getFeatureStrById(terrainFeatureId),
                    GAME_CONTROLLER
                            .getTerrainFeatureNameById(terrainFeatureId).get("terrainFeatureName").getAsString());
        }
        System.out.println("\n >>> UNITS:");
        int[] unitTypeIds = GSON.fromJson(GAME_CONTROLLER.getAllUnitTypeIds().get("unitTypeIds").getAsJsonArray(),
                int[].class);
        for (int unitTypeId : unitTypeIds) {
            System.out.format("\t%2d: (%s:%s),\n", unitTypeId, getUnitStrById(unitTypeId),
                    GAME_CONTROLLER.getUnitTypeNameById(unitTypeId).get("unitTypeName").getAsString());
        }
        System.out.println("\n >>> RESOURCES:");
        int[] resourceIds = GSON.fromJson(GAME_CONTROLLER.getAllResourceIds().get("resourceIds").getAsJsonArray(),
                int[].class);
        for (int resourceId : resourceIds) {
            System.out.format("\t%2d: (%s:%s),\n", resourceId, getResourceStrById(resourceId),
                    GAME_CONTROLLER.getResourceNameById(resourceId).get("resourceName").getAsString());
        }
        System.out.println("\n >>> IMPROVEMENTS:");
        int[] improvementIds = GSON
                .fromJson(GAME_CONTROLLER.getAllImprovementIds().get("improvementIds").getAsJsonArray(), int[].class);
        for (int improvementId : improvementIds) {
            System.out.format("\t%2d: (%s:%s),\n", improvementId, getImprovementStrById(improvementId),
                    GAME_CONTROLLER.getImprovementNameById(improvementId).get("improvementName").getAsString());
        }
        System.out.println("\n >>> CIVILIZATIONS:");
        int[] civIds = GSON.fromJson(GAME_CONTROLLER.getAllCivilizationIds().get("civIds").getAsJsonArray(),
                int[].class);
        for (int civId : civIds) {
            System.out.format("\t%2d: (%s:%s),\n", civId,
                    getColoredStr(getCivStrById(civId), getCivColorById(civId), true),
                    GAME_CONTROLLER.getCivilizationNameById(civId).get("civName").getAsString());
        }
        System.out.println("\n========MAP=============================\n");
        for (int i = 0; i < PLAIN_H; i++) {
            for (int j = 0; j < PLAIN_W; j++) {
                System.out.print(plain[i][j]);
            }
            System.out.println();
        }
        System.out.println("\n========================================\n");
    }

    private void writeMap(JsonObject response) {
        resetPlain();
        JsonArray map = (JsonArray) response.get("map");
        int width = response.get("width").getAsInt();
        int height = response.get("height").getAsInt();
        int directionOfMap = ((JsonObject) ((JsonArray) map.get(0)).get(0)).get("y").getAsInt() % 2;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                JsonObject tile = (JsonObject) ((JsonArray) map.get(i)).get(j);
                int tileId = tile.get("index").getAsInt();
                int tileX = tile.get("x").getAsInt();
                int tileY = tile.get("y").getAsInt();
                String knowledge = tile.get("knowledge").getAsString();

                boolean fog = knowledge.equals("FOG_OF_WAR");
                boolean hasFeature = tile.get("terrainFeatureId") != null;
                int terrainType = -1;
                if (!fog)
                    terrainType = tile.get("terrainTypeId").getAsInt();
                Color tileColor = getTileColorByTerrainTypeId(terrainType);

                int upLeftX = 6 * i + (j % 2 == directionOfMap ? 4 : 1);
                int upLeftY = 8 * j;
                int centerY = upLeftY + 5;

                JsonObject hasRiver = (JsonObject) tile.get("hasRiver");
                boolean upHasRiver = false;
                boolean upRightHasRiver = false;
                boolean downRightHasRiver = false;
                boolean downHasRiver = false;
                boolean downLeftHasRiver = false;
                boolean upLeftHasRiver = false;
                if (hasRiver != null) {
                    upHasRiver = hasRiver.get("up").getAsBoolean();
                    upRightHasRiver = hasRiver.get("upRight").getAsBoolean();
                    downRightHasRiver = hasRiver.get("downRight").getAsBoolean();
                    downHasRiver = hasRiver.get("down").getAsBoolean();
                    downLeftHasRiver = hasRiver.get("downLeft").getAsBoolean();
                    upLeftHasRiver = hasRiver.get("upLeft").getAsBoolean();
                }

                for (int x = upLeftX; x < upLeftX + 3; x++) {
                    int diffX = x - upLeftX;
                    for (int y = centerY - 2 - diffX; y <= centerY + 2 + diffX; y++) {
                        plain[x][y] = getColoredStr(" ", tileColor);
                    }
                    plain[x][centerY - 3 - diffX] = (!upLeftHasRiver
                            ? getColoredStr("/", Color.BG_BLACK, Color.FG_WHITE)
                            : getColoredStr("/", Color.BG_BLUE, Color.FG_WHITE));
                    plain[x][centerY + 3 + diffX] = (!upRightHasRiver
                            ? getColoredStr("\\", Color.BG_BLACK, Color.FG_WHITE)
                            : getColoredStr("\\", Color.BG_BLUE, Color.FG_WHITE));
                }
                for (int x = upLeftX + 3; x < upLeftX + 6; x++) {
                    int diffX = 5 - (x - upLeftX);
                    for (int y = centerY - 2 - diffX; y <= centerY + 2 + diffX; y++) {
                        plain[x][y] = getColoredStr(" ", tileColor);
                    }
                    plain[x][centerY - 3 - diffX] = (!downLeftHasRiver
                            ? getColoredStr("\\", Color.BG_BLACK, Color.FG_WHITE)
                            : getColoredStr("\\", Color.BG_BLUE, Color.FG_WHITE));
                    plain[x][centerY + 3 + diffX] = (!downRightHasRiver
                            ? getColoredStr("/", Color.BG_BLACK, Color.FG_WHITE)
                            : getColoredStr("/", Color.BG_BLUE, Color.FG_WHITE));
                }

                if (i == 0) {
                    for (int k = 3; k < 8; k++) {
                        plain[upLeftX][upLeftY + k] = (!upHasRiver
                                ? getColoredStr(" ", tileColor)
                                : getColoredStr(" ", Color.BG_BLUE, Color.FG_WHITE));
                    }
                }

                for (int k = 3; k < 8; k++) {
                    plain[upLeftX + 5][upLeftY + k] = (!downHasRiver
                            ? getColoredStr("_", tileColor)
                            : getColoredStr("-", Color.BG_BLUE, Color.FG_WHITE));
                }

                plain[upLeftX][centerY - 1] = getColoredStr(Integer.toString(tileId / 100), tileColor, true);
                plain[upLeftX][centerY] = getColoredStr(Integer.toString((tileId % 100) / 10), tileColor, true);
                plain[upLeftX][centerY + 1] = getColoredStr(Integer.toString(tileId % 10), tileColor, true);

                plain[upLeftX + 2][centerY - 2] = getColoredStr(Integer.toString(tileX / 10), tileColor, true);
                plain[upLeftX + 2][centerY - 1] = getColoredStr(Integer.toString(tileX % 10), tileColor, true);
                plain[upLeftX + 2][centerY] = getColoredStr(",", tileColor, true);
                plain[upLeftX + 2][centerY + 1] = getColoredStr(Integer.toString(tileY / 10), tileColor, true);
                plain[upLeftX + 2][centerY + 2] = getColoredStr(Integer.toString(tileY % 10), tileColor, true);

                if (fog) {
                    plain[upLeftX + 3][centerY - 1] = getColoredStr("F", tileColor, Color.FG_BLUE);
                    plain[upLeftX + 3][centerY] = getColoredStr("O", tileColor, Color.FG_BLUE);
                    plain[upLeftX + 3][centerY + 1] = getColoredStr("G", tileColor, Color.FG_BLUE);
                    continue;
                }

                if (hasFeature) {
                    // REVEALED
                    int featureId = tile.get("terrainFeatureId").getAsInt();
                    String featureStr = getFeatureStrById(featureId);
                    plain[upLeftX + 4][centerY] = getColoredStr(featureStr, tileColor, true);
                }

                if (!knowledge.equals("VISIBLE"))
                    continue;

                if (tile.get("ownerCivId") != null) {
                    int ownerCivId = tile.get("ownerCivId").getAsInt();
                    String ownerCivStr = getCivStrById(ownerCivId);
                    Color ownerCivColor = getCivColorById(ownerCivId);
                    plain[upLeftX + 1][centerY] = getColoredStr(ownerCivStr, ownerCivColor, true);
                    if (tile.get("cityInTile") != null) {
                        plain[upLeftX + 1][centerY + 1] = getColoredStr((tile.get("cityInTile").getAsJsonObject().get("dead").getAsBoolean() ? "x" : "*"), ownerCivColor, true);
                    }
                }

                if (tile.get("combatUnit") != null) {
                    int combatUnitTypeId = ((JsonObject) tile.get("combatUnit")).get("unitTypeId").getAsInt();
                    String combatUnitStr = getUnitStrById(combatUnitTypeId);
                    int combatUnitCivId = ((JsonObject) tile.get("combatUnit")).get("civId").getAsInt();
                    plain[upLeftX + 3][centerY + 1] = getColoredStr(combatUnitStr,
                            getCivColorById(combatUnitCivId), true);
                }
                if (tile.get("nonCombatUnit") != null) {
                    int nonCombatUnitTypeId = ((JsonObject) tile.get("nonCombatUnit")).get("unitTypeId").getAsInt();
                    String nonCombatUnitStr = getUnitStrById(nonCombatUnitTypeId);
                    int nonCombatUnitCivId = ((JsonObject) tile.get("nonCombatUnit")).get("civId").getAsInt();
                    plain[upLeftX + 3][centerY - 1] = getColoredStr(nonCombatUnitStr,
                            getCivColorById(nonCombatUnitCivId), true);
                }

                if (tile.get("resource") != null) {
                    int resourceId = tile.get("resource").getAsJsonObject().get("id").getAsInt();
                    boolean isImproved = tile.get("resource").getAsJsonObject().get("improved").getAsBoolean();
                    String resourceStr = getResourceStrById(resourceId);
                    plain[upLeftX + 4][centerY - 2] = getColoredStr(resourceStr, (isImproved ? Color.BG_GREEN : tileColor), true);
                }
                if (tile.get("improvement") != null) {
                    int improvementId = tile.get("improvement").getAsJsonObject().get("id").getAsInt();
                    boolean isDead = tile.get("improvement").getAsJsonObject().get("dead").getAsBoolean();
                    String improvementStr = getImprovementStrById(improvementId);
                    plain[upLeftX + 4][centerY + 2] = getColoredStr(improvementStr, (isDead ? Color.BG_RED : tileColor), true);
                }

            }
        }
    }

    private String getColoredStr(String str, Color color) {
        return color.toString() + str + Color.ANSI_RESET.toString();
    }

    private String getColoredStr(String str, Color color, boolean setFGColor) {
        return getColoredStr(str, color, color.getCompatibleColor());
    }

    private String getColoredStr(String str, Color color1, Color color2) {
        return getColoredStr(getColoredStr(str, color1), color2);
    }

    private Color getTileColorByTerrainTypeId(int terrainTypeId) {
        switch (terrainTypeId) {
            case 2:
                return Color.BG_YELLOW;
            case 3:
                return Color.BG_GREEN;
            case 4:
                return Color.BG_PURPLE;
            case 5:
                return Color.BG_BLACK;
            case 6:
                return Color.BG_BLUE;
            case 7:
                return Color.BG_CYAN;
            case 8:
                return Color.BG_WHITE;
            case 9:
                return Color.BG_RED;
            default:
                return Color.BG_WHITE;
        }
    }

    private String getUnitStrById(int unitTypeId) {
        return Character.toString((char) ((int) 'A' + unitTypeId));
    }

    private String getResourceStrById(int rsrcId) {
        return Character.toString((char) ((int) 'A' + rsrcId));
    }

    private String getImprovementStrById(int imprId) {
        return Character.toString((char) ((int) 'A' + imprId));
    }

    private String getFeatureStrById(int featureId) {
        switch (featureId) {
            case 3:
                return "P";
            case 4:
                return "J";
            case 7:
                return "I";
            case 8:
                return "F";
            case 11:
                return "M";
            case 13:
                return "O";
            case 15:
                return getColoredStr("-", Color.BG_BLUE);
            default:
                throw new RuntimeException();
        }
    }

    private Color getCivColorById(int civId) {
        switch (civId) {
            case 0:
                return Color.BG_BLUE;
            case 1:
                return Color.BG_RED;
            case 2:
                return Color.BG_PURPLE;
            case 3:
                return Color.BG_YELLOW;
            case 4:
                return Color.BG_BLACK;
            case 5:
                return Color.BG_CYAN;
            case 6:
                return Color.BG_WHITE;
            case 7:
                return Color.BG_GREEN;
            default:
                return Color.BG_BLACK;
        }
    }

    private String getCivStrById(int civId) {
        return Character.toString((char) ((int) 'A' + civId));
    }

    private JsonObject getInfoResponse(String arg, boolean cheat) {
        switch (arg) {
            case "research":
                return GAME_CONTROLLER.infoResearch(currentPlayer);
            case "units":
                return GAME_CONTROLLER.infoUnits(currentPlayer);
            case "cities":
                return GAME_CONTROLLER.infoCities(currentPlayer, cheat);
            case "diplomacy":
                return GAME_CONTROLLER.infoDiplomacy(currentPlayer);
            case "victory":
                return GAME_CONTROLLER.infoVictory(currentPlayer);
            case "demographics":
                return GAME_CONTROLLER.infoDemographics(currentPlayer);
            case "notifications":
                return GAME_CONTROLLER.infoNotifications(currentPlayer);
            case "military":
                return GAME_CONTROLLER.infoMilitary(currentPlayer);
            case "economic":
                return GAME_CONTROLLER.infoEconomic(currentPlayer);
            case "diplomatic":
                return GAME_CONTROLLER.infoDiplomatic(currentPlayer);
            case "deals":
                return GAME_CONTROLLER.infoDeals(currentPlayer);
            default:
                return null;
        }
    }

    private JsonObject getUnitActionResponse(String[] args, boolean cheat) {
        int tileId;
        switch (args[0]) {
            case "moveto":
                try {
                    tileId = Integer.parseInt(args[1]);
                } catch (Exception ex) {
                    return null;
                }
                return GAME_CONTROLLER.unitMoveTo(currentPlayer, tileId, cheat);
            case "sleep":
                return GAME_CONTROLLER.unitSleep(currentPlayer);
            case "alert":
                return GAME_CONTROLLER.unitAlert(currentPlayer);
            case "fortify":
                if (args.length > 1 && args[1].equals("heal"))
                    return GAME_CONTROLLER.unitFortifyUntilHeal(currentPlayer);
                return GAME_CONTROLLER.unitFortify(currentPlayer);
            case "garrison":
                return GAME_CONTROLLER.unitGarrison(currentPlayer);
            case "setup":
                if (args.length > 1 && args[1].equals("ranged"))
                    return GAME_CONTROLLER.unitSetupRanged(currentPlayer);
                return null;
            case "attack":
                try {
                    tileId = Integer.parseInt(args[1]);
                } catch (Exception ex) {
                    return null;
                }
                return GAME_CONTROLLER.unitAttack(currentPlayer, tileId, cheat);
            case "found":
                if (args.length > 1 && args[1].equals("city"))
                    return GAME_CONTROLLER.unitFoundCity(currentPlayer, cheat);
                return null;
            case "cancel":
                if (args.length > 1 && args[1].equals("mission"))
                    return GAME_CONTROLLER.unitCancelMission(currentPlayer);
                return null;
            case "wake":
                return GAME_CONTROLLER.unitWake(currentPlayer);
            case "delete":
                return GAME_CONTROLLER.unitDelete(currentPlayer);
            case "build":
                return getUnitBuildResponse(args, cheat);
            case "remove":
                if (args.length == 1)
                    return null;
                switch (args[1]) {
                    case "jungle":
                        return GAME_CONTROLLER.unitRemoveJungle(currentPlayer, cheat);
                    case "forest":
                        return GAME_CONTROLLER.unitRemoveForest(currentPlayer, cheat);
                    case "marsh":
                        return GAME_CONTROLLER.unitRemoveMarsh(currentPlayer, cheat);
                    case "route":
                        return GAME_CONTROLLER.unitRemoveRoute(currentPlayer, cheat);
                    default:
                        return null;
                }
            case "repair":
                return GAME_CONTROLLER.unitRepair(currentPlayer, cheat);
            default:
                return null;
        }
    }

    private JsonObject getUnitBuildResponse(String[] args, boolean cheat) {
        if (args.length <= 1 || !args[0].equals("build"))
            return null;
        switch (args[1]) {
            case "road":
                return GAME_CONTROLLER.unitBuildRoad(currentPlayer, cheat);
            case "railroad":
                return GAME_CONTROLLER.unitBuildRailRoad(currentPlayer, cheat);
            case "camp":
                return GAME_CONTROLLER.unitBuildImprovement(currentPlayer, 1, cheat);
            case "farm":
                return GAME_CONTROLLER.unitBuildImprovement(currentPlayer, 2, cheat);
            case "lumbermill":
                return GAME_CONTROLLER.unitBuildImprovement(currentPlayer, 3, cheat);
            case "mine":
                return GAME_CONTROLLER.unitBuildImprovement(currentPlayer, 4, cheat);
            case "pasture":
                return GAME_CONTROLLER.unitBuildImprovement(currentPlayer, 5, cheat);
            case "plantation":
                return GAME_CONTROLLER.unitBuildImprovement(currentPlayer, 6, cheat);
            case "quarry":
                return GAME_CONTROLLER.unitBuildImprovement(currentPlayer, 7, cheat);
            case "tradingpost":
                return GAME_CONTROLLER.unitBuildImprovement(currentPlayer, 8, cheat);
            case "factory":
                return GAME_CONTROLLER.unitBuildImprovement(currentPlayer, 9, cheat);
            default:
                return null;
        }
    }

    private JsonObject getCityActionResponse(String[] args, boolean cheat) {
        int tileId;
        switch (args[0]) {
            case "attack":
                try {
                    tileId = Integer.parseInt(args[1]);
                    return GAME_CONTROLLER.cityAttack(currentPlayer, tileId, cheat);
                } catch (Exception ex) {
                    return null;
                }
            case "work":
                try {
                    tileId = Integer.parseInt(args[1]);
                    return GAME_CONTROLLER.cityAddCitizenToWorkOnTile(currentPlayer, tileId);
                } catch (Exception ex) {
                }
                if (args.length <= 1)
                    return null;
                if (args[1].equals("delete")) {
                    try {
                        tileId = Integer.parseInt(args[2]);
                    } catch (Exception ex) {
                        return null;
                    }
                    return GAME_CONTROLLER.cityRemoveCitizenFromWorkOnTile(currentPlayer, tileId);
                } else if (args[1].equals("all")) {
                    return GAME_CONTROLLER.cityGetWorkingTiles(currentPlayer);
                } else {
                    return null;
                }
            case "output":
                return GAME_CONTROLLER.cityGetOutput(currentPlayer);
            case "unemployed":
                return GAME_CONTROLLER.cityGetUnemployedCitizens(currentPlayer);
            case "buildings":
                return GAME_CONTROLLER.cityGetBuildings(currentPlayer);
            case "purchase":
                if (args.length <= 1)
                    return null;
                if (args[1].equals("tile")) {
                    try {
                        tileId = Integer.parseInt(args[2]);
                    } catch (Exception ex) {
                        return null;
                    }
                    return GAME_CONTROLLER.cityPurchaseTile(currentPlayer, tileId, cheat);
                } else if (args[1].equals("production")) {
                    int prodId;
                    try {
                        prodId = Integer.parseInt(args[2]);
                    } catch (Exception ex) {
                        return null;
                    }
                    return GAME_CONTROLLER.cityBuyProduction(currentPlayer, prodId, cheat);
                } else {
                    return null;
                }
            case "production":
                if (args.length == 1)
                    return GAME_CONTROLLER.cityGetCurrentProduction(currentPlayer);
                if (args[1].equals("show")) {
                    return GAME_CONTROLLER.cityGetCurrentProduction(currentPlayer);
                } else if (args[1].equals("set")) {
                    int prodId;
                    try {
                        prodId = Integer.parseInt(args[2]);
                    } catch (Exception ex) {
                        return null;
                    }
                    return GAME_CONTROLLER.citySetCurrentProduction(currentPlayer, prodId, cheat);
                } else if (args[1].equals("all")) {
                    return GAME_CONTROLLER.cityGetAllAvailableProductions(currentPlayer, cheat);
                } else {
                    return null;
                }
            case "destroy":
                try {
                    int cityId = Integer.parseInt(args[1]);
                    return GAME_CONTROLLER.cityDestroy(currentPlayer, cityId, cheat);
                } catch (Exception ex) {
                    return null;
                }
            case "annex":
                try {
                    int cityId = Integer.parseInt(args[1]);
                    return GAME_CONTROLLER.cityAnnex(currentPlayer, cityId, cheat);
                } catch (Exception ex) {
                    return null;
                }
            default:
                return null;
        }
    }
}
