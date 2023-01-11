package ir.ap.controller;

import ir.ap.model.*;
import ir.ap.model.TerrainType.TerrainFeature;
import ir.ap.model.Tile.TileKnowledge;
import ir.ap.model.UnitType.UnitAction;

public class UnitController extends AbstractGameController {
    private int unitCount = 0;
    private int cityCount = 0;

    public UnitController(GameArea gameArea) {
        super(gameArea);
    }

    public Unit getUnitById(int unitId) {
        for (Civilization civ : civController.getAllCivilizations()) {
            for (Unit unit : civ.getUnits()) {
                if (unit.getId() == unitId)
                    return unit;
            }
        }
        return null;
    }

    public boolean addUnit(Civilization civilization, Tile tile, UnitType unitType){
        Unit unit = new Unit(unitCount++, unitType, civilization, tile);
        civilization.addUnit(unit);
        if (addUnitToMap(unit)) {
            civilization.addToMessageQueue("one unit with type " + unit.getUnitType() + " has been added to Civilization " + civilization.getName());
            return true;
        }
        civilization.addToMessageQueue("unable to add unit " + unit.getUnitType() + " to tile " + tile.getIndex());
        civilization.removeUnit(unit);
        return false;
    }

    public boolean removeUnit(Unit unit) {
        Civilization civ = unit.getCivilization();
        if (civ != null)
            civ.removeUnit(unit);
        civ.addToMessageQueue("one unit with type " + unit.getUnitType() + " removed from Civilization " + civ.getName());
        return removeUnitFromMap(unit);
    }

    public boolean changeUnitOwner(Unit unit, Civilization newCiv) {
        if (unit == null || newCiv == null) return false;
        unit.getCivilization().addToMessageQueue("new owner of unit " + unit.getUnitType() + " has been changed to Civilization " + newCiv.getName());
        newCiv.addToMessageQueue("new owner of unit " + unit.getUnitType() + " has been changed to Civilization " + newCiv.getName());
        removeUnit(unit);
        addUnit(newCiv, unit.getTile(), unit.getUnitType());
        return true;
    }

    public boolean addUnitToMap(Unit unit) {
        if (unit == null)
            return false;
        Tile tile = unit.getTile();
        if (tile == null)
            return false;
        if (unit.isCivilian()) {
            if (tile.hasNonCombatUnit())
                return false;
            tile.setNonCombatUnit(unit);
        } else {
            if (tile.hasCombatUnit())
                return false;
            tile.setCombatUnit(unit);
        }
        Civilization civilization = unit.getCivilization();
        for (Tile visitingTile : mapController.getUnitVisitingTiles(unit)) {
            visitingTile.addVisitingUnit(unit);
            gameArea.setTileKnowledgeByCivilization(civilization, visitingTile, TileKnowledge.VISIBLE);
        }
        return true;
    }

    public boolean removeUnitFromMap(Unit unit) {
        if (unit == null) return false;
        Tile tile = unit.getTile();
        if (tile == null) return false;
        if (unit.isCivilian()) {
            tile.setNonCombatUnit(null);
        } else {
            tile.setCombatUnit(null);
        }
        for (Tile visitingTile : mapController.getUnitVisitingTiles(unit)) {
            visitingTile.removeVisitingUnit(unit);
            if (!visitingTile.civilizationIsVisiting(unit.getCivilization())) {
                gameArea.setTileKnowledgeByCivilization(unit.getCivilization(), visitingTile, TileKnowledge.REVEALED);
            }
        }
        return true;
    }

    public boolean moveUnitTowardsTarget(Unit unit, boolean cheat) {
        if (unit == null || unit.getUnitAction() != UnitAction.MOVETO || unit.getTarget() == null) return false;
        Tile startingPos = unit.getTile();
        while (cheat || unit.canMove()) {
            Tile target = unit.getTarget();
            Tile curTile = unit.getTile();
            if (curTile == target) break;
            Tile nxtTile = gameArea.getMap().getNextTileInWeightedShortestPath(curTile, target);
            if (nxtTile == null || (!cheat && nxtTile.isUnreachable())) break;
            int dist = nxtTile.getMovementCost();
            if ((unit.isCivilian() && nxtTile.getNonCombatUnit() != null) ||
                (!unit.isCivilian() && nxtTile.getCombatUnit() != null) ||
                (!cheat && unit.hasMovedThisTurn() && unit.getMp() < dist))
                break;
            if (unit.isCivilian() && nxtTile.getCombatUnit() != null &&
                unit.getCivilization() != nxtTile.getCombatUnit().getCivilization())
                break;
            if (nxtTile.getCity() != null && !nxtTile.getCity().isDead() &&
                nxtTile.getCity().getCivilization() != unit.getCivilization())
                break;
            removeUnitFromMap(unit);
            if (!cheat)
                unit.addToMp(-dist);
            if (!cheat && curTile.hasRiverInBetween(nxtTile))
                unit.setMp(0);
            unit.setTile(nxtTile);
            addUnitToMap(unit);
        }
        Tile target = unit.getTarget();
        Tile curTile = unit.getTile();
        if (curTile == target) {
            unit.setTarget(null);
            unit.setUnitAction(null);
            return true;
        } else {
            return (!cheat || startingPos != curTile);
        }
    }

    public boolean moveUnitTowardsTarget(Unit unit) {
        return moveUnitTowardsTarget(unit, false);
    }

    public boolean unitMoveTo(Civilization civilization, Tile target, boolean cheat)
    {
        if (civilization == null || target == null) return false;
        Unit unit = civilization.getSelectedUnit();
        if(unit == null) return false;
        unit.setHowManyTurnWeKeepAction(0);
        Tile tile = unit.getTile();
        if (tile == null || target.isUnreachable()) return false;
        unit.setTarget(target);
        unit.setUnitAction(UnitType.UnitAction.MOVETO);
        return moveUnitTowardsTarget(unit, cheat);
    }

    public boolean unitSleep(Civilization civilization)
    {
        Unit unit = civilization.getSelectedUnit();
        if(unit == null) return false;
        unit.setHowManyTurnWeKeepAction(0);
        unit.setUnitAction(UnitType.UnitAction.SLEEP);
        unit.setSleep(true);
        return true;
    }

    public boolean unitFortify(Civilization civilization)
    {
        Unit unit = civilization.getSelectedUnit();
        if(unit == null) return false;
        unit.setHowManyTurnWeKeepAction(0);
        if(unit.getUnitType().getCombatType() == UnitType.CombatType.CIVILIAN) return false;
        if(unit.getUnitType().getCombatType() == UnitType.CombatType.MOUNTED)  return false;
        if(unit.getUnitType().getCombatType() == UnitType.CombatType.ARMORED) return false;
        unit.setUnitAction(UnitType.UnitAction.FORTIFY);
        // combat Strengh ziad mishe va lahaz shode to unit.getCombatStrengh
        return true;
    }
    public boolean unitFortifyHeal(Civilization civilization)
    {
        Unit unit = civilization.getSelectedUnit();
        if(unit == null) return false;
        if(unit.getCombatType() == UnitType.CombatType.CIVILIAN) return false;
        unit.setHowManyTurnWeKeepAction(0);
        unit.setUnitAction(UnitType.UnitAction.FORTIFY_HEAL);
        return true;
    }

    public boolean unitGarrison(Civilization civilization)
    {
        Unit unit = civilization.getSelectedUnit();
        if(unit == null) return false;
        unit.setHowManyTurnWeKeepAction(0);
        City city = unit.getTile().getCity();
        if (city == null || city.getCivilization() != civilization || city.getCombatUnit() != unit) return false;
        // GARISSON to tabe city.getCombatStrength lahaz shode
        unit.setUnitAction(UnitType.UnitAction.GARRISON);
        return true;
    }
    public boolean unitSetupForRangedAttack(Civilization civilization)
    {
        Unit unit = civilization.getSelectedUnit();
        if(unit == null) return false;
        unit.setHowManyTurnWeKeepAction(0);
        unit.addToMp(-UnitType.MAX_MOVEMENT);
        unit.setUnitAction(UnitType.UnitAction.SETUP_RANGED);
        return true;
    }
    public boolean unitAttack(Civilization civilization, Tile target, boolean cheat)
    {
        if (civilization == null || target == null) return false;
        if (!target.civilizationIsVisiting(civilization)) return false;
        Unit unit = civilization.getSelectedUnit();
        if (unit == null || (!cheat && !unit.canAttack())) return false;
        Tile curTile = unit.getTile();
        Unit enemyUnit = target.getCombatUnit();
        City enemyCity = target.getCity();
        if (enemyUnit == null || enemyUnit.getCivilization() == civilization) enemyUnit = target.getNonCombatUnit();

        int dist = mapController.getDistanceInTiles(curTile, target);
        unit.setHowManyTurnWeKeepAction(0);
        int combatStrength = (cheat ? 1000 : unit.getCombatStrength());
        if(unit.getUnitType().getCombatType() == UnitType.CombatType.CIVILIAN) return false;
        if (unit.getCombatType() == UnitType.CombatType.SIEGE && unit.getUnitAction() != UnitType.UnitAction.SETUP_RANGED)
            return false;
        unit.setUnitAction(UnitType.UnitAction.ATTACK);
        if(enemyUnit != null && enemyUnit.getCivilization() != civilization) {
            if (unit.getCombatType() == UnitType.CombatType.ARCHERY || unit.getCombatType() == UnitType.CombatType.SIEGE) {
                if (dist > unit.getRange()) return false;
                enemyUnit.setHp(enemyUnit.getHp() - combatStrength);
                if (!cheat) {
                    if (unit.getUnitType().getCombatType() != UnitType.CombatType.ARMORED && unit.getUnitType().getCombatType() != UnitType.CombatType.MOUNTED)
                        unit.setMp(0);
                    else
                        unit.setMp(unit.getMp() - dist);
                }
                if (enemyUnit.isDead()) {
                    removeUnit(enemyUnit);
                }
                if (unit.isDead()) {
                    removeUnit(unit);
                }
                unit.setHasAttacked(true);
                return true;
                // in this type of attack we will kill worker
            }

            if (unit.getCombatType() == UnitType.CombatType.MOUNTED || unit.getCombatType() == UnitType.CombatType.MELEE || unit.getCombatType() == UnitType.CombatType.GUNPOWDER || unit.getCombatType() == UnitType.CombatType.ARMORED || unit.getCombatType() == UnitType.CombatType.RECON) {
                if (dist > 1) return false;
                if (enemyUnit.getUnitType().getCombatType() == UnitType.CombatType.CIVILIAN) {
                    if (enemyCity == null) {
                        changeUnitOwner(enemyUnit, civilization);
                        unit.setHasAttacked(true);
                        return true;
                    }
                } else {
                    enemyUnit.setHp(enemyUnit.getHp() - combatStrength);
                    if (!cheat) {
                        if (unit.getUnitType().getCombatType() != UnitType.CombatType.ARMORED && unit.getUnitType().getCombatType() != UnitType.CombatType.MOUNTED)
                            unit.setMp(0);
                        else
                            unit.setMp(unit.getMp() - dist);
                    }
                    if (!cheat) unit.setHp(unit.getHp() - enemyUnit.getCombatStrength());
                    if (enemyUnit.getHp() <= 0) {
                        removeUnit(enemyUnit);
                        unitMoveTo(civilization, target, cheat);
                        Unit nonCombatTarget = unit.getTile().getNonCombatUnit();
                        if (nonCombatTarget != null && !nonCombatTarget.getCivilization().equals(civilization)) {
                            changeUnitOwner(nonCombatTarget, civilization);
                        }
                    }
                    if (unit.getHp() <= 0) {
                        removeUnit(unit);
                    }
                    unit.setHasAttacked(true);
                    return true;
                }
                // in this type of attack we got worker if it is not city
            }
        }
        if (enemyCity != null && enemyCity.getCivilization() != civilization) {
            if (unit.getCombatType() == UnitType.CombatType.ARCHERY || unit.getCombatType() == UnitType.CombatType.SIEGE) {
                if (dist > unit.getRange()) return false;

                enemyCity.setHp(enemyCity.getHp() - combatStrength);
                if (!cheat) {
                    if (unit.getUnitType().getCombatType() != UnitType.CombatType.ARMORED && unit.getUnitType().getCombatType() != UnitType.CombatType.MOUNTED)
                        unit.setMp(0);
                    else
                        unit.setMp(unit.getMp() - dist);
                }
                if (!cheat) unit.setHp(unit.getHp() - enemyCity.getCombatStrength());

                if (unit.getHp() <= 0) {
                    removeUnit(unit);
                }
                unit.setHasAttacked(true);
                return true;
            }

            if (unit.getCombatType() == UnitType.CombatType.MOUNTED || unit.getCombatType() == UnitType.CombatType.MELEE || unit.getCombatType() == UnitType.CombatType.GUNPOWDER || unit.getCombatType() == UnitType.CombatType.ARMORED || unit.getCombatType() == UnitType.CombatType.RECON) {
                if (dist > 1) return false;
                enemyCity.setHp(enemyCity.getHp() - combatStrength);
                if (!cheat) {
                    if (unit.getUnitType().getCombatType() != UnitType.CombatType.ARMORED && unit.getUnitType().getCombatType() != UnitType.CombatType.MOUNTED)
                        unit.setMp(0);
                    else
                        unit.setMp(unit.getMp() - dist);
                }
                if (!cheat) unit.setHp(unit.getHp() - enemyCity.getCombatStrength());
                if (enemyCity.getHp() <= 0) {
                    unitMoveTo(civilization, target, cheat);
                    // cityController.changeCityOwner(enemyCity, civilization);
                    if(enemyUnit != null && enemyUnit.getUnitType().getCombatType() == UnitType.CombatType.CIVILIAN)
                    {
                        changeUnitOwner(enemyUnit, civilization);
                    }
                }
                if (unit.getHp() <= 0) {
                    civilization.removeUnit(unit);
                }
                unit.setHasAttacked(true);
                return true;
            }
        }
        if (unit.getUnitType().getCombatType() != UnitType.CombatType.ARMORED && unit.getUnitType().getCombatType() != UnitType.CombatType.MOUNTED)
            unit.setMp(0);
        else
            unit.setMp(unit.getMp() - dist);
        if(unit.isCivilian() && dist <= 1 || dist <= unit.getRange()) {
            if(target.hasImprovement()){
                Improvement improvement = target.getImprovement();
                improvement.setIsDead(true);
                target.setImprovement(improvement);
            }
        }
        unit.setHasAttacked(true);
        return true;
    }

    public City unitFoundCity(Civilization civilization, boolean cheat)
    {
        if (civilization == null) return null;
        Unit unit = civilization.getSelectedUnit();
        if (unit == null) return null;
        unit.setHowManyTurnWeKeepAction(0);
        if (unit == null || (!cheat && unit.getUnitType() != UnitType.SETTLER))
            return null;

        Tile target = unit.getTile();
        if (target == null || target.hasOwnerCity())
            return null;
        City city;
        int cnt = 10;
        do {
            city = new City(cityCount++, City.getCityName(RANDOM.nextInt()), civilization, target);
        } while (!cityController.addCity(city) && cnt --> 0);
        if (cnt < 0)
            return null;
        unit.setUnitAction(UnitType.UnitAction.FOUND_CITY);
        removeUnit(unit);
        return city;
    }

    public boolean unitCancelMission(Civilization civilization)
    {
        Unit unit = civilization.getSelectedUnit();
        if(unit == null) return false;
        unit.setHowManyTurnWeKeepAction(0);

        unit.setUnitAction(UnitType.UnitAction.CANCEL_MISSION);
        return true;
    }

    public boolean unitBuildRoad(Civilization civilization, boolean cheat)
    {
        if (civilization == null) return false;
        Unit unit = civilization.getSelectedUnit();
        if(unit == null) return false;
        Tile tile = unit.getTile();
        if (tile == null || tile.getHasRoad()) return false;
        unit.setHowManyTurnWeKeepAction((cheat ? 1000 : 0));
        if(unit.getUnitType() != UnitType.WORKER) return false;
        if(civilization.getTechnologyReached(Technology.THE_WHEEL) == false && cheat == false) return false;

        unit.setUnitAction(UnitType.UnitAction.BUILD_ROAD);
        civilization.addToMessageQueue("Started building ROAD on tile " + tile.getIndex());
        return true;
    }

    public boolean unitBuildRailRoad(Civilization civilization, boolean cheat)
    {
        Unit unit = civilization.getSelectedUnit();
        if(unit == null) return false;
        Tile tile = unit.getTile();
        if (tile == null || tile.getHasRailRoad()) return false;
        unit.setHowManyTurnWeKeepAction((cheat ? 1000 : 0));
        if(unit.getUnitType() != UnitType.WORKER) return false;
        if(civilization.getTechnologyReached(Technology.RAILROAD) == false && cheat == false) return false;

        unit.setUnitAction(UnitType.UnitAction.BUILD_RAILROAD);
        civilization.addToMessageQueue("Started building RAILROAD on tile " + tile.getIndex());
        return true;
    }

    public boolean unitBuildImprovement(Civilization civilization, Improvement improvement, boolean cheat)
    {
        Unit unit = civilization.getSelectedUnit();
        if(unit == null) return false;
        unit.setHowManyTurnWeKeepAction((cheat ? 1000 : 0));
        Tile tile = unit.getTile();
        if (tile == null || tile.hasImprovement() || tile.hasCity()) return false;

        if(tile.getTerrainFeature() == TerrainFeature.FOREST) return false;
        if(tile.getTerrainFeature() == TerrainFeature.JUNGLE) return false;
        if(tile.getTerrainFeature() == TerrainFeature.MARSH) return false;

        if (!cheat && !tile.civCanBuildImprovement(civilization, improvement))
            return false;
        if(improvement == Improvement.FARM) {
            unit.setUnitAction(UnitType.UnitAction.BUILD_FARM);
            civilization.addToMessageQueue("Started building FARM on tile " + tile.getIndex());
        }
        if(improvement == Improvement.MINE){
            unit.setUnitAction(UnitType.UnitAction.BUILD_MINE);
            civilization.addToMessageQueue("Started building MINE on tile " + tile.getIndex());
        }
        if(improvement == Improvement.TRADING_POST) {
            unit.setUnitAction(UnitType.UnitAction.BUILD_TRADINGPOST);
            civilization.addToMessageQueue("Started building TRADING_POST on tile " + tile.getIndex());
        }
        if(improvement == Improvement.LUMBER_MILL) {
            unit.setUnitAction(UnitType.UnitAction.BUILD_LUMBERMILL);
            civilization.addToMessageQueue("Started building LUMBER_MILL on tile " + tile.getIndex());
        }
        if(improvement == Improvement.PASTURE) {
            unit.setUnitAction(UnitType.UnitAction.BUILD_PASTURE);
            civilization.addToMessageQueue("Started building PASTURE on tile " + tile.getIndex());
        }
        if(improvement == Improvement.CAMP) {
            unit.setUnitAction(UnitType.UnitAction.BUILD_CAMP);
            civilization.addToMessageQueue("Started building CAMP on tile " + tile.getIndex());
        }
        if(improvement == Improvement.PLANTATION) {
            unit.setUnitAction(UnitType.UnitAction.BUILD_PLANTATION);
            civilization.addToMessageQueue("Started building PLANTATION on tile " + tile.getIndex());
        }
        if(improvement == Improvement.QUARRY) {
            unit.setUnitAction(UnitType.UnitAction.BUILD_QUARRY);
            civilization.addToMessageQueue("Started building QUARRY on tile " + tile.getIndex());
        }
        return true;
    }
    public boolean unitRemoveJungle(Civilization civilization, boolean cheat)
    {
        Unit unit = civilization.getSelectedUnit();
        if(unit == null) return false;
        Tile tile = unit.getTile();
        if (tile == null || tile.getTerrainFeature() != TerrainFeature.JUNGLE) return false;
        unit.setHowManyTurnWeKeepAction((cheat ? 1000 : 0));
        if(civilization.getTechnologyReached(Technology.BRONZE_WORKING) == false && cheat == false) return false;

        unit.setUnitAction(UnitType.UnitAction.REMOVE_JUNGLE);
        civilization.addToMessageQueue("Started removing JUNGLE on tile " + tile.getIndex());
        return true;
    }
    public boolean unitRemoveForest(Civilization civilization, boolean cheat)
    {
        Unit unit = civilization.getSelectedUnit();
        if(unit == null) return false;
        Tile tile = unit.getTile();
        if (tile == null || tile.getTerrainFeature() != TerrainFeature.FOREST) return false;
        unit.setHowManyTurnWeKeepAction((cheat ? 1000 : 0));
        if(civilization.getTechnologyReached(Technology.MINING) == false && cheat == false) return false;

        unit.setUnitAction(UnitType.UnitAction.REMOVE_FOREST);
        civilization.addToMessageQueue("Started removing FOREST on tile " + tile.getIndex());
        return true;
    }

    public boolean unitRemoveMarsh(Civilization civilization, boolean cheat) {
        Unit unit = civilization.getSelectedUnit();
        if (unit == null) return false;
        unit.setHowManyTurnWeKeepAction((cheat ? 1000 : 0));
        Tile tile = unit.getTile();
        if (tile == null || tile.getTerrainFeature() != TerrainFeature.MARSH) return false;
        if(civilization.getTechnologyReached(Technology.MASONRY) == false && cheat == false) return false;

        unit.setUnitAction(UnitType.UnitAction.REMOVE_MARSH);
        civilization.addToMessageQueue("Started removing MARSH on tile " + tile.getIndex());
        return true;
    }

    public boolean unitRemoveRoute(Civilization civilization, boolean cheat)
    {
        Unit unit = civilization.getSelectedUnit();
        if(unit == null) return false;
        Tile tile = unit.getTile();
        if (tile == null || (!tile.getHasRoad() && !tile.getHasRailRoad())) return false;
        unit.setHowManyTurnWeKeepAction((cheat ? 1000 : 0));

        unit.setUnitAction(UnitType.UnitAction.REMOVE_ROUTE);
        civilization.addToMessageQueue("Started removing routes on tile " + tile.getIndex());
        return true;
    }

    public boolean unitRepair(Civilization civilization, boolean cheat)
    {
        Unit unit = civilization.getSelectedUnit();
        if(unit == null) return false;
        Tile tile = unit.getTile();
        if (tile == null || (!tile.hasCity() && !tile.hasImprovement())) return false;
        unit.setHowManyTurnWeKeepAction((cheat ? 1000 : 0));

        unit.setUnitAction(UnitType.UnitAction.REPAIR);
        civilization.addToMessageQueue("Started repairing on tile " + tile.getIndex());
        return true;
    }

    public boolean unitWake(Civilization civilization)
    {
        Unit unit = civilization.getSelectedUnit();
        if(unit == null) return false;
        unit.setHowManyTurnWeKeepAction(0);
        unit.setUnitAction(UnitType.UnitAction.WAKE);
        unit.setSleep(false);
        return true;
    }

    public boolean unitAlert(Civilization civilization)
    {
        Unit unit = civilization.getSelectedUnit();
        if(unit == null) return false;
        unit.setHowManyTurnWeKeepAction(0);
        unit.setUnitAction(UnitType.UnitAction.ALERT);
        return true;
    }

}
