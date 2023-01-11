package ir.ap.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import ir.ap.model.TerrainType.TerrainFeature;

public class Tile implements Serializable {

    public enum TileKnowledge {
        VISIBLE,
        REVEALED,
        FOG_OF_WAR;
    }

    private Tile[] neighbors;
    private boolean[] hasRiverOnSide;
    private int[] weight;

    private int index;
    private int mapX, mapY;

    private TerrainType terrainType;
    private TerrainFeature terrainFeature;
    private ArrayList<Resource> resources;

    private City city;
    private City ownerCity;
    private HashSet<Unit> visitingUnits;
    private Improvement improvement;

    private Unit combatUnit;
    private Unit nonCombatUnit;

    private boolean hasRoad;
    private boolean hasRailRoad;

    public Tile(int index, int mapX, int mapY, TerrainType terrainType, TerrainFeature terrainFeature, ArrayList<Resource> resources) {
        neighbors = new Tile[6];
        hasRiverOnSide = new boolean[6];
        weight = new int[6];

        this.index = index;
        this.mapX = mapX;
        this.mapY = mapY;

        this.terrainType = terrainType;
        this.terrainFeature = terrainFeature;

        this.resources = resources;

        city = null;
        ownerCity = null;
        visitingUnits = new HashSet<>();
        improvement = null;

        combatUnit = null;
        nonCombatUnit = null;

        hasRoad = false;
        hasRailRoad = false;
    }

    public void setResources(ArrayList<Resource> resources) {
        this.resources = resources;
    }

    public Tile[] getNeighbors() {
        return neighbors;
    }

    public boolean setNeighborOnSide(Direction dir, Tile other) {
        if (dir == null)
            return false;
        neighbors[dir.getId()] = other;
        return true;
    }

    public boolean setNeighborOnSide(int dirId, Tile other) {
        Direction dir = Direction.getDirectionById(dirId);
        return setNeighborOnSide(dir, other);
    }

    public Tile getNeighborOnSide(Direction dir) {
        if (dir == null)
            return null;
        return neighbors[dir.getId()];
    }

    public Tile getNeighborOnSide(int dirId) {
        Direction dir = Direction.getDirectionById(dirId);
        return getNeighborOnSide(dir);
    }

    public boolean setWeightOnSide(Direction dir, int weight) {
        if (dir == null || weight < 0)
            return false;
        this.weight[dir.getId()] = weight;
        return true;
    }

    public boolean setWeightOnSide(int dirId, int weight) {
        Direction dir = Direction.getDirectionById(dirId);
        return setWeightOnSide(dir, weight);
    }

    public boolean addWeightOnSide(int dirId, int weight){
        if( dirId<0 || dirId>5 )return false;
        if( this.weight[ dirId ]+weight < 0 )return false;
        this.weight[ dirId ] += weight;
        return true;
    }

    public boolean addWeightOnSide(Direction dir, int weight){
        return addWeightOnSide(dir.getId(), weight);
    }

    public int getWeightOnSide(Direction dir) {
        if (dir == null)
            return -1;
        return weight[dir.getId()];
    }

    public int getWeightOnSide(int dirId) {
        Direction dir = Direction.getDirectionById(dirId);
        return getWeightOnSide(dir);
    }

    public boolean setHasRiverOnSide(Direction dir, boolean value) {
        if (dir == null)
            return false;
        hasRiverOnSide[dir.getId()] = value;
        return true;
    }

    public boolean setHasRiverOnSide(int dirId, boolean value) {
        Direction dir = Direction.getDirectionById(dirId);
        return setHasRiverOnSide(dir, value);
    }

    public boolean getHasRiverOnSide(Direction dir) {
        if (dir == null)
            return false;
        return hasRiverOnSide[dir.getId()];
    }

    public boolean getHasRiverOnSide(int dirId) {
        Direction dir = Direction.getDirectionById(dirId);
        return getHasRiverOnSide(dir);
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setMapX(int mapX) {
        this.mapX = mapX;
    }

    public int getMapX() {
        return this.mapX;
    }

    public void setMapY(int mapY) {
        this.mapY = mapY;
    }

    public int getMapY() {
        return this.mapY;
    }

    public void setTerrainType(TerrainType terrainType) {
        this.terrainType = terrainType;
    }

    public TerrainType getTerrainType() {
        return terrainType;
    }

    public void setTerrainFeature(TerrainFeature terrainFeature) {
        this.terrainFeature = terrainFeature;
    }

    public TerrainFeature getTerrainFeature() {
        return terrainFeature;
    }

    public void addResource(Resource resource) {
        resources.add(resource);
    }

    public void addResource(Resource resource, int count) {
        for (int i = 0; i < count; i++) {
            addResource(resource);
        }
    }

    public int getResourceCount(Resource resource) {
        int count = 0;
        for (Resource curRsc : this.resources) {
            count += (curRsc == resource ? 1 : 0);
        }
        return count;
    }

    public int getImprovedResourceCount(Resource resource) {
        return (resourceIsImproved(resource) ? getResourceCount(resource) : 0);
    }

    public ArrayList<Resource> getResources() {
        return resources;
    }

    public Resource getResourceVisibleByCivilization(Civilization civ) {
        for (Resource rsrc : getImprovedResources()) {
            if (civ.getTechnologyReached(rsrc.getTechnologyRequired()))
                return rsrc;
        }
        for (Resource rsrc : getResources()) {
            if (civ.getTechnologyReached(rsrc.getTechnologyRequired()))
                return rsrc;
        }
        return null;
    }

    public ArrayList<Resource> getImprovedResources() {
        ArrayList<Resource> improvedRscs = new ArrayList<>();
        for (Resource curRsc : this.resources) {
            if (resourceIsImproved(curRsc))
                improvedRscs.add(curRsc);
        }
        return improvedRscs;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public City getCity() {
        return this.city;
    }

    public boolean hasCity() {
        return getCity() != null;
    }

    public City getOwnerCity() {
        return ownerCity;
    }

    public void setOwnerCity(City city) {
        ownerCity = city;
    }

    public boolean hasOwnerCity() {
        return getOwnerCity() != null;
    }

    public void addVisitingUnit(Unit unit) {
        visitingUnits.add(unit);
    }

    public void removeVisitingUnit(Unit unit) {
        visitingUnits.remove(unit);
    }

    public boolean isVisiting(Unit unit) {
        return visitingUnits.contains(unit);
    }

    public Set<Unit> getVisitingUnits() {
        return visitingUnits;
    }

    public boolean civilizationIsVisiting(Civilization civ) {
        if (civ == null)
            return false;
        if (ownerCity != null && ownerCity.getCivilization().equals(civ))
            return true;
        for (Unit unit : getVisitingUnits())
            if (unit.getCivilization().equals(civ))
                return true;
        return false;
    }

    public void setImprovement(Improvement improvement) {
        this.improvement = improvement;
    }

    public Improvement getImprovement() {
        return improvement;
    }

    public boolean hasImprovement() {
        return getImprovement() != null;
    }

    public boolean civCanBuildImprovement(Civilization civ, Improvement impr) {
        City ownerCity = getOwnerCity();
        if (ownerCity == null)
            return false;
        Civilization ownerCiv = ownerCity.getCivilization();
        if (ownerCiv != civ || !ownerCiv.getTechnologyReached(impr.getTechnologyRequired()))
            return false;
        if (!impr.getCanBeFoundOn().contains(getTerrainType()) && !impr.getCanBeFoundOn().contains(getTerrainFeature()))
            return false;
        return true;
    }

    public void setCombatUnit(Unit combatUnit) {
        this.combatUnit = combatUnit;
    }

    public Unit getCombatUnit() {
        return combatUnit;
    }

    public boolean hasCombatUnit() {
        return getCombatUnit() != null;
    }

    public void setNonCombatUnit(Unit nonCombatUnit) {
        this.nonCombatUnit = nonCombatUnit;
    }

    public Unit getNonCombatUnit() {
        return nonCombatUnit;
    }

    public boolean hasNonCombatUnit() {
        return getNonCombatUnit() != null;
    }

    public void setHasRoad(boolean hasRoad) {
        this.hasRoad = hasRoad;
    }

    public boolean getHasRoad() {
        return hasRoad;
    }

    public void setHasRailRoad(boolean hasRailRoad) {
        this.hasRailRoad = hasRailRoad;
    }

    public boolean getHasRailRoad() {
        return hasRailRoad;
    }

    public int getFoodYield() {
        int foodYield = (terrainType != null ? terrainType.getFoodYield() : 0) +
                (terrainFeature != null ? terrainFeature.getFoodYield() : 0) +
                (improvement != null ? improvement.getFoodYield() : 0);
        for (int dirId = 0; dirId < 6; dirId++) {
            Direction dir = Direction.getDirectionById(dirId);
            if (getHasRiverOnSide(dir)) {
                foodYield += TerrainFeature.RIVERS.getFoodYield();
            }
        }
        for (Resource rsrc : resources) {
            foodYield += rsrc.getFoodYield();
        }
        return foodYield;
    }

    public int getGoldYield() {
        int goldYield = (terrainType != null ? terrainType.getGoldYield() : 0) +
                (terrainFeature != null ? terrainFeature.getGoldYield() : 0) +
                (improvement != null ? improvement.getGoldYield() : 0);
        for (int dirId = 0; dirId < 6; dirId++) {
            Direction dir = Direction.getDirectionById(dirId);
            if (getHasRiverOnSide(dir)) {
                goldYield += TerrainFeature.RIVERS.getGoldYield();
            }
        }
        for (Resource rsrc : resources) {
            goldYield += rsrc.getGoldYield();
        }
        if (getHasRoad()) {
            goldYield -= 1;
        }
        return goldYield;
    }

    public int getProductionYield() {
        int productionYield = (terrainType != null ? terrainType.getProductionYield() : 0) +
                (terrainFeature != null ? terrainFeature.getProductionYield() : 0) +
                (improvement != null ? improvement.getProductionYield() : 0);
        for (int dirId = 0; dirId < 6; dirId++) {
            Direction dir = Direction.getDirectionById(dirId);
            if (getHasRiverOnSide(dir)) {
                productionYield += TerrainFeature.RIVERS.getProductionYield();
            }
        }
        for (Resource rsrc : resources) {
            productionYield += rsrc.getProductionYield();
        }
        return productionYield;
    }

    public int getCombatModifier() {
        return (terrainType != null ? terrainType.getCombatModifier() : 0) +
                (terrainFeature != null ? terrainFeature.getCombatModifier() : 0);
    }

    public int getMovementCost() {
        int cost = (terrainType != null ? terrainType.getMovementCost() : 0) +
                (terrainFeature != null ? terrainFeature.getMovementCost() : 0);
        if (getHasRoad() || getHasRailRoad())
            cost = (cost + 1) / 2;
        return cost;
    }

    public int getMovementCostWithoutRoadAndRailRoad() {
        int cost = (terrainType != null ? terrainType.getMovementCost() : 0) +
                (terrainFeature != null ? terrainFeature.getMovementCost() : 0);
        return cost;
    }

    public boolean resourceIsImproved(Resource resource) {
        return getImprovement() != null && !getImprovement().getIsDead() && getImprovement() == resource.getImprovementRequired();
    }

    public boolean hasRiverInBetween(Tile neighbor) {
        for (int i = 0; i < 6; i++) {
            if (neighbors[i] != null && neighbors[i] == neighbor) {
                return getHasRiverOnSide(Direction.getDirectionById(i));
            }
        }
        return false;
    }

    public boolean hasRiver() {
        return getHasRiverOnSide(Direction.UP) ||
                getHasRiverOnSide(Direction.UP_RIGHT) ||
                getHasRiverOnSide(Direction.DOWN_RIGHT) ||
                getHasRiverOnSide(Direction.DOWN) ||
                getHasRiverOnSide(Direction.DOWN_LEFT) ||
                getHasRiverOnSide(Direction.UP_LEFT);
    }

    public boolean isWaterTile() {
        return hasRiver() ||
                (terrainType != null && terrainType.isSourceOfWater()) ||
                (terrainFeature != null && terrainFeature.isSourceOfWater());
    }

    public boolean isFreshWaterTile() {
        return hasRiver() ||
                (terrainFeature != null && terrainFeature.isSourceOfWater());
    }

    public boolean isDeepWaterTile() {
        boolean result = terrainType.isSourceOfWater();
        for (int i = 0; i < 6; i++) {
            result &= (neighbors[i] != null && neighbors[i].getTerrainType().isSourceOfWater());
        }
        return result;
    }

    public boolean isBlock() {
        return terrainFeature == TerrainFeature.FOREST ||
                terrainType == TerrainType.MOUNTAIN ||
                terrainType == TerrainType.HILL;
    }

    public boolean isUnreachable() {
        return terrainType == TerrainType.MOUNTAIN || terrainType.isSourceOfWater();
    }

    public boolean isCapitalCity(){
        if( getCity() != null && getCity().equals(getOwnerCity()) )return true;
        else return false;
    }
}
