package ir.ap.controller;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ir.ap.model.*;
import org.junit.Before;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;

public class AbstractControllerTest {
    protected static final UserController USER_CONTROLLER = new UserController();
    protected static final GameController GAME_CONTROLLER = new GameController();

    protected static final User player1 = new User("user11111", "nick11111", "pass1");
    protected static final User player2 = new User("user22222", "nick22222", "pass2");
    protected static Civilization civ1;
    protected static Civilization civ2;
    protected static Unit civ1CombatUnit, civ1NonCombatUnit;
    protected static Unit civ2CombatUnit, civ2NonCombatUnit;

    protected static String username1, username2;

    protected void foundCities() {
        assertOk(GAME_CONTROLLER.selectUnit(username1, civ1NonCombatUnit.getId()));
        outer:
        for (int i = 0; i < Map.MAX_H; i++) {
            for (int j = 0; j < Map.MAX_W; j++) {
                Tile tile = getMapController().getTileByIndices(i, j);
                if (tile.hasOwnerCity() || tile.isUnreachable())
                    continue;
                assertOk(GAME_CONTROLLER.unitMoveTo(username1, tile.getIndex(), true));
                assertOk(GAME_CONTROLLER.unitFoundCity(username1, false));
                break outer;
            }
        }
        assertOk(GAME_CONTROLLER.selectUnit(username2, civ2NonCombatUnit.getId()));
        outer:
        for (int i = 0; i < Map.MAX_H; i++) {
            for (int j = 0; j < Map.MAX_W; j++) {
                Tile tile = getMapController().getTileByIndices(i, j);
                if (tile.hasOwnerCity() || tile.isUnreachable())
                    continue;
                assertOk(GAME_CONTROLLER.unitMoveTo(username2, tile.getIndex(), true));
                assertOk(GAME_CONTROLLER.unitFoundCity(username2, false));
                break outer;
            }
        }
    }

    protected City getCity(int id) {
        return (id == 1 ? civ1.getCities().get(0) : civ2.getCities().get(0));
    }

    @Before
    public void init() {
        assertTrue(login(player1));
        assertTrue(login(player2));
        assertTrue(newGame());
        civ1 = getCivController().getCivilizationByUsername(player1.getUsername());
        civ2 = getCivController().getCivilizationByUsername(player2.getUsername());
        username1 = player1.getUsername();
        username2 = player2.getUsername();

        for (Unit unit : civ1.getUnits()) {
            if (unit.isCivilian())
                civ1NonCombatUnit = unit;
            else
                civ1CombatUnit = unit;
        }
        assertNotNull(civ1CombatUnit);
        assertNotNull(civ1NonCombatUnit);
        for (Unit unit : civ2.getUnits()) {
            if (unit.isCivilian())
                civ2NonCombatUnit = unit;
            else
                civ2CombatUnit = unit;
        }
        assertNotNull(civ2CombatUnit);
        assertNotNull(civ2NonCombatUnit);
    }

    protected static boolean login(User user) {
        USER_CONTROLLER.register(user.getUsername(), user.getNickname(), user.getPassword());
        JsonObject response = USER_CONTROLLER.login(user.getUsername(), user.getPassword(), null);
        boolean result = response.get("ok").getAsBoolean();
        return result;
    }

    protected static boolean newGame() {
        JsonObject response = GAME_CONTROLLER.newGame(new String[] { player1.getUsername(), player2.getUsername() });
        return response.get("ok").getAsBoolean();
    }

    protected static UnitController getUnitController() {
        return GAME_CONTROLLER.getUnitController();
    }

    protected static CivilizationController getCivController() {
        return GAME_CONTROLLER.getCivilizationController();
    }

    protected static CityController getCityController() {
        return GAME_CONTROLLER.getCityController();
    }

    protected static MapController getMapController() {
        return GAME_CONTROLLER.getMapController();
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

    protected static void assertOk(JsonObject response) {
        assertTrue(responseOk(response));
    }

    protected static void assertNotOk(JsonObject response) {
        assertFalse(responseOk(response));
    }
}
