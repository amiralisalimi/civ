package ir.ap.client.components.map.serializers;

import ir.ap.client.components.Hexagon;

public class TileSerializer {

    private int index;
    private int x, y;

    private int ownerCivId;
    private String knowledge;

    private int terrainTypeId = -1;
    private int terrainFeatureId = -1;

    private RiverSerializer hasRiver;
    private ImprovementSerializer improvement;
    private ResourceSerializer resource;
    private UnitSerializer nonCombatUnit;
    private UnitSerializer combatUnit;
    private CitySerializer cityInTile;
    // private Hexagon tilHexagon;

    // public Hexagon getTilHexagon() {
    //     return tilHexagon;
    // }

    // public void setTilHexagon(Hexagon tilHexagon) {
    //     this.tilHexagon = tilHexagon;
    // }

    public int getIndex() {
        return index;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getOwnerCivId() {
        return ownerCivId;
    }

    public String getKnowledge() {
        return knowledge;
    }

    public int getTerrainTypeId() {
        return terrainTypeId;
    }

    public int getTerrainFeatureId() {
        return terrainFeatureId;
    }

    public RiverSerializer getHasRiver() {
        return hasRiver;
    }

    public ImprovementSerializer getImprovement() {
        return improvement;
    }

    public ResourceSerializer getResource() {
        return resource;
    }

    public UnitSerializer getNonCombatUnit() {
        return nonCombatUnit;
    }

    public UnitSerializer getCombatUnit() {
        return combatUnit;
    }

    public CitySerializer getCityInTile() {
        return cityInTile;
    }
}
