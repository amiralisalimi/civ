package ir.ap.controller;

import ir.ap.model.City;
import org.junit.Test;

public class CityControllerTest extends AbstractControllerTest {

    @Test
    public void testCityAttack() {
        GAME_CONTROLLER.cityAttack(username1, 200, true);
        foundCities();
        City city1 = getCity(1);
        City city2 = getCity(2);
        GAME_CONTROLLER.selectCity(username1, city1.getTile().getIndex());
        GAME_CONTROLLER.cityAttack(username1, city2.getTile().getIndex(), true);
        GAME_CONTROLLER.selectUnit(username2, civ2CombatUnit.getId());
        GAME_CONTROLLER.unitMoveTo(username2, city1.getTile().getIndex(), true);
        GAME_CONTROLLER.cityAttack(username1, civ2CombatUnit.getTile().getIndex(), false);
        GAME_CONTROLLER.cityAttack(username1, civ2CombatUnit.getTile().getIndex(), true);
        GAME_CONTROLLER.cityAttack(username1, 200, true);
    }
}
