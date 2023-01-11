package ir.ap.controller;

import ir.ap.model.Building;
import ir.ap.model.City;
import ir.ap.model.Civilization;
import ir.ap.model.GameArea;
import ir.ap.model.Map;
import ir.ap.model.Production;
import ir.ap.model.Tile;
import ir.ap.model.Unit;
import ir.ap.model.UnitType;
import ir.ap.model.Tile.TileKnowledge;

public class CityController extends AbstractGameController {
    public CityController(GameArea gameArea) {
        super(gameArea);
    }

    public City getCityById(int cityId) {
        for (Civilization civ : civController.getAllCivilizations()) {
            for (City city : civ.getCities()) {
                if (city.getId() == cityId)
                    return city;
            }
        }
        return null;
    }

    public void nextTurn(City city) {
        if (city == null) return;
        city.setActionThisTurn(false);
        city.addToHp(1);
        city.addToFood(city.getExtraFood());
        int population = city.getPopulation();
        city.addToPopulation(city.getPopulationGrowth());
        int nxtPop = city.getPopulation();
        if (nxtPop > population) {
            city.getCivilization().addToMessageQueue("City " + city.getName() + " has grown to " + nxtPop + " citizens");
        } else if (nxtPop < population) {
            city.getCivilization().addToMessageQueue("City " + city.getName() + " has population decreased to " + nxtPop + " citizens");
        }
        if (city.getCurrentProduction() != null) {
            Production production = city.getCurrentProduction();
            city.addToProductionSpent(city.getProductionYield());
            if (city.getCostLeftForProductionConstruction() <= 0) {
                if (cityConstructProduction(city)) {
                    city.getCivilization().addToMessageQueue("City " + city.getName() + " constructed " + production.getName());
                } else {
                    city.getCivilization().addToMessageQueue("City " + city.getName() + " is unable to add production " + production.getName());
                }
            }
        }
        if( gameArea.getTurn() % City.TURN_NEEDED_TO_EXTEND_TILES == 0 ){
            Tile tile = addRandomTileToCity(city);
            city.getCivilization().addToMessageQueue("City " + city.getName() + " added tile " + tile.getIndex() + " to territory");
        }
    }
    
    public Tile addRandomTileToCity( City city ){
        if( city == null )return null;
        for(int i = 0 ; i < Map.MAX_H ; i ++){
            for(int j = 0; j < Map.MAX_W ; j ++){
                Tile tile = gameArea.getMap().getTiles()[ i ][ j ] ;
                if (cityPurchaseTile(city, tile))
                    return tile;
            }
        }
        return null;
    }

    public boolean addCityToMap(City city) {
        if (city == null)
            return false;
        Tile tile = city.getTile();
        if (tile == null || tile.hasImprovement() || tile.hasCity())
            return false;
        tile.setCity(city);
        for (Tile territoryTile : mapController.getTilesInRange(city, city.getTerritoryRange())) {
            if (!territoryTile.isDeepWaterTile())
                addTileToTerritoryOfCity(city, territoryTile);
        }
        return true;
    }

    public boolean removeCityFromMap(City city) {
        if (city == null)
            return false;
        Tile tile = city.getTile();
        if (tile == null)
            return false;
        tile.setCity(null);
        for (Tile territoryTile : mapController.getTilesInRange(city, city.getTerritoryRange())) {
            removeTileFromTerritoryOfCity(city, territoryTile);
        }
        city.resetTerritory();
        return true;
    }

    public boolean addCity(City city) {
        if (!City.addCity(city))
            return false;
        if (!addCityToMap(city)) {
            City.removeCity(city);
            return false;
        }
        city.getCivilization().addCity(city);
        city.getCivilization().addToMessageQueue("city " + city.getName() + " has been added to Civilization " + city.getCivilization().getName());
        return true;
    }

    public boolean removeCity(City city) {
        City.removeCity(city);
        Civilization civ = city.getCivilization();
        if (civ != null)
            civ.removeCity(city);
        city.getCivilization().addToMessageQueue("city " + city.getName() + " has been removed from Civilization " + city.getCivilization().getName());
        return removeCityFromMap(city);
    }

    public boolean changeCityOwner(City city, Civilization newCiv) {
        removeCity(city);
        city.setCivilization(newCiv);
        city.resetHp();
        city.getCivilization().addToMessageQueue("owner of city " + city.getName() + " has been changed to Civilization " + city.getCivilization().getName());
        return addCity(city);
    }

    public boolean tileIsNearTerritoryOfCity(City city, Tile tile) {
        if (city == null || tile == null)
            return false;
        for (Tile neighbor : tile.getNeighbors()) {
            if (neighbor != null && neighbor.getOwnerCity() != null && neighbor.getOwnerCity().equals(city))
                return true;
        }
        return false;
    }

    public boolean addTileToTerritoryOfCity(City city, Tile tile) {
        if (city == null || tile == null)
            return false;
        City other = tile.getOwnerCity();
        if (other != null)
            return false;
        Civilization civ = city.getCivilization();
        city.addToTerritory(tile);
        tile.setOwnerCity(city);
        gameArea.setTileKnowledgeByCivilization(civ, tile, TileKnowledge.VISIBLE);
        for (Tile neighbor : tile.getNeighbors())
            gameArea.setTileKnowledgeByCivilization(civ, neighbor, TileKnowledge.VISIBLE);
        return true;
    }

    public boolean removeTileFromTerritoryOfCity(City city, Tile tile) {
        if (city == null || tile == null)
            return false;
        Civilization civ = city.getCivilization();
        if (civ == null)
            return false;
        tile.setOwnerCity(null);
        city.removeFromTerritory(tile);
        if (!tile.civilizationIsVisiting(civ)) {
            gameArea.setTileKnowledgeByCivilization(civ, tile, TileKnowledge.REVEALED);
        }
        for (Tile neighbor : tile.getNeighbors()) {
            if (neighbor != null && !neighbor.civilizationIsVisiting(civ))
                gameArea.setTileKnowledgeByCivilization(civ, neighbor, TileKnowledge.REVEALED);
        }
        return true;
    }

    public boolean cityAttack(City city, Tile target, boolean cheat) {
        if (city == null || (!cheat && !city.canAttack())) return false;
        Civilization civilization = city.getCivilization();
        Tile curTile = city.getTile();
        Unit enemyUnit = target.getCombatUnit();
        if (enemyUnit == null || enemyUnit.getCivilization() == civilization)
            enemyUnit = target.getNonCombatUnit();
        if(enemyUnit == null) return false;
        Civilization otherCiv = (enemyUnit == null ? null : enemyUnit.getCivilization());
        if (otherCiv == null) return false;
        int dist = mapController.getDistanceInTiles(curTile, target);
        if (dist > city.getTerritoryRange()) return false;
        int combatStrength = (cheat ? 1000 : city.getCombatStrength());
        if(enemyUnit != null && enemyUnit.getCivilization() != civilization){
            enemyUnit.setHp(enemyUnit.getHp() - combatStrength);
            city.setActionThisTurn(true);

            if (enemyUnit.getHp() <= 0) {
                unitController.removeUnit(enemyUnit);
            }

            return true;
        }
        return false;
    }

    public boolean cityAddCitizenToWorkOnTile(City city, Tile tile) {
        if (city == null || tile == null)
            return false;
        if (city.getWorkingTiles().size() >= city.getPopulation())
            return false;
        int dist = gameArea.getDistanceInTiles(city.getTile(), tile);
        if (tile.getOwnerCity() != city || dist < 1 || dist > 2)
            return false;
        return city.addToWorkingTiles(tile);
    }

    public boolean cityRemoveCitizenFromWork(City city, Tile tile) {
        if (city == null || tile == null)
            return false;
        return city.removeFromWorkingTiles(tile);
    }

    public boolean cityPurchaseTile(City city, Tile tile) {
        if (!tileIsNearTerritoryOfCity(city, tile))
            return false;
        return addTileToTerritoryOfCity(city, tile);
    }

    public boolean cityAddBuilding(City city, Building building, boolean cheat) {
        if (city == null || building == null || (!cheat && !city.getCivilization().getTechnologyReached(building.getTechnologyRequired())))
            return false;
        if (!cheat && building.getBuildingTypeRequired() != null &&
                !city.hasBuilding(building.getBuildingTypeRequired()))
            return false;
        city.addBuilding(building);
        city.getCivilization().addToMessageQueue("Building " + building.getName() + " constructed in city " + city.getName());
        return true;
    }

    public boolean cityChangeCurrentProduction(City city, Production production, boolean cheat) {
        if (city == null) return false;
        if (!cheat && !city.getCivilization().getTechnologyReached(production.getTechnologyRequired()))
            return false;
        if (!city.setCurrentProduction(production, !cheat)) {
            return false;
        }
        city.setProductionSpent(0);
        Civilization civ = city.getCivilization();
        civ.addToMessageQueue("production city " + city.getName() + " has been changed to " + production.getName());
        if( cheat == true ){
            cityConstructProduction(city, cheat);
        }
        return true;
    }

    public boolean cityConstructProduction(City city, Production production, boolean cheat) {
        if (city == null || (!cheat && !city.getCivilization().getTechnologyReached(production.getTechnologyRequired())))
            return false;
        if (production instanceof UnitType) {
            boolean res = unitController.addUnit(city.getCivilization(), city.getTile(), (UnitType) production);
            if (!res)return false;
        } else if (production instanceof Building) {
            boolean res = cityAddBuilding(city, (Building) production, cheat);
            if (!res)return false;
        }
        return true;
    }

    public boolean cityConstructProduction(City city, boolean cheat) {
        if (city == null || city.getCurrentProduction() == null)
            return false;
        Production production = city.getCurrentProduction();
        if (city.getCostLeftForProductionConstruction() > 0)
            return false;
        boolean res = cityConstructProduction(city, production, cheat);
        if (res) {
            city.setProductionSpent(0);
            city.setCurrentProduction(null);
        }
        return res;
    }

    public boolean cityConstructProduction(City city) {
        return cityConstructProduction(city, false);
    }

    public boolean cityDestroy(City city, Civilization civ) {
        if (removeCity(city)) {
            civ.addCityDestroyed(city);
            return true;
        }
        return false;
    }

    public boolean cityAnnex(City city, Civilization civ) {
        if (changeCityOwner(city, civ)) {
            civ.addCitiesAnnexed(city);
            return true;
        }
        return false;
    }
}
