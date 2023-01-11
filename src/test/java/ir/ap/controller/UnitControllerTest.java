package ir.ap.controller;

import org.junit.Test;

import ir.ap.model.City;
import ir.ap.model.Unit;

public class UnitControllerTest extends AbstractControllerTest {

    @Test
    public void testUnitAttackNull() {
        GAME_CONTROLLER.unitAttack("chert", 0, true);
        GAME_CONTROLLER.unitAttack(username1, 300, true);
        GAME_CONTROLLER.selectUnit(username1, civ1NonCombatUnit.getId());
        GAME_CONTROLLER.unitAttack(username1, -1, false);
        GAME_CONTROLLER.unitAttack(username1, 300, true);
        GAME_CONTROLLER.selectUnit(username1, civ1CombatUnit.getId());
        GAME_CONTROLLER.unitAttack(username1, 300, true);
        GAME_CONTROLLER.unitAttack(username1, 300, false);
    }

    @Test
    public void testUnitAttackCity() {
        assertOk(GAME_CONTROLLER.selectUnit(username1, civ1NonCombatUnit.getId()));
        assertOk(GAME_CONTROLLER.unitFoundCity(username1, false));
        City city1 = civ1.getCities().get(0);

        assertOk(GAME_CONTROLLER.selectUnit(username2, civ2CombatUnit.getId()));
        GAME_CONTROLLER.unitMoveTo(username1, 300, true);
        GAME_CONTROLLER.unitMoveTo(username1, 400, true);
        GAME_CONTROLLER.unitMoveTo(username1, 500, true);
        assertOk(GAME_CONTROLLER.unitMoveTo(username2, city1.getTile().getIndex(), true));

        assertOk(GAME_CONTROLLER.unitAttack(username2, city1.getTile().getIndex(), false));
        assertOk(GAME_CONTROLLER.unitAttack(username2, city1.getTile().getIndex(), true));
        assertOk(GAME_CONTROLLER.unitAttack(username2, city1.getTile().getIndex(), true));
        assertOk(GAME_CONTROLLER.cityAnnex(username2, city1.getId(), false));
    }

    @Test
    public void testUnitAttackUnit() {
        assertOk(GAME_CONTROLLER.selectUnit(username1, civ1CombatUnit.getId()));
        if (getMapController().getDistanceInTiles(civ1CombatUnit.getTile(), civ2CombatUnit.getTile()) > 1) {
            assertNotOk(GAME_CONTROLLER.unitAttack(username1, civ2CombatUnit.getTile().getIndex(), false));
        }
        foundCities();
        City city1 = getCity(1);
        City city2 = getCity(2);
        assertOk(GAME_CONTROLLER.selectCity(username1, city1.getTile().getIndex()));
        assertOk(GAME_CONTROLLER.citySetCurrentProduction(username1, 1, true));
        GAME_CONTROLLER.selectCombatUnit(username1, city1.getTile().getIndex());
        GAME_CONTROLLER.unitDelete(username1);
        assertOk(GAME_CONTROLLER.increaseTurn(username1, 20));
        Unit archer1 = city1.getTile().getCombatUnit();
        assertOk(GAME_CONTROLLER.selectUnit(username1, archer1.getId()));
        GAME_CONTROLLER.unitAttack(username1, archer1.getTile().getIndex(), true);
        GAME_CONTROLLER.unitAttack(username1, civ2CombatUnit.getTile().getIndex(), false);
        GAME_CONTROLLER.unitAttack(username1, civ2CombatUnit.getTile().getIndex(), true);
        GAME_CONTROLLER.unitMoveTo(username1, 300, true);
        GAME_CONTROLLER.unitMoveTo(username1, 400, true);
        GAME_CONTROLLER.unitMoveTo(username1, 500, true);
        GAME_CONTROLLER.unitMoveTo(username1, civ2CombatUnit.getTile().getIndex(), true);
        GAME_CONTROLLER.increaseTurn(username1, 1);
        GAME_CONTROLLER.unitAttack(username1, civ2CombatUnit.getTile().getIndex(), false);
        GAME_CONTROLLER.unitAttack(username1, civ2CombatUnit.getTile().getIndex(), true);
        GAME_CONTROLLER.unitAttack(username1, city2.getTile().getIndex(), false);
        GAME_CONTROLLER.increaseTurn(username1, 1);
        GAME_CONTROLLER.unitAttack(username1, city2.getTile().getIndex(), false);

        assertOk(GAME_CONTROLLER.selectCity(username1, city1.getTile().getIndex()));
        assertOk(GAME_CONTROLLER.citySetCurrentProduction(username1, 16, true));
        GAME_CONTROLLER.selectCombatUnit(username1, city1.getTile().getIndex());
        GAME_CONTROLLER.unitDelete(username1);
        GAME_CONTROLLER.increaseTurn(username1, 70);
        GAME_CONTROLLER.selectCombatUnit(username1, city1.getTile().getIndex());
        assertNotOk(GAME_CONTROLLER.unitAttack(username1, city1.getTile().getIndex(), true));
        assertNotOk(GAME_CONTROLLER.unitAttack(username1, 200, true));
        assertNotOk(GAME_CONTROLLER.unitAttack(username1, city2.getTile().getIndex(), true));
    }
}
