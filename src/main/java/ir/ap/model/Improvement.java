package ir.ap.model;

import java.util.ArrayList;
import java.util.Arrays;

public enum Improvement {
    CAMP,
    FARM,
    LUMBER_MILL,
    MINE,
    PASTURE,
    PLANTATION,
    QUARRY,
    TRADING_POST,
    FACTORY;

    public static Improvement getImprovementById(int id)
    {
        if(id == 1) return CAMP;
        if(id == 2) return FARM;
        if(id == 3) return LUMBER_MILL;
        if(id == 4) return MINE;
        if(id == 5) return PASTURE;
        if(id == 6) return PLANTATION;
        if(id == 7) return QUARRY;
        if(id == 8) return TRADING_POST;
        if(id == 9) return FACTORY;
        return null;
    }

    private int id;
    private boolean isDead;

    public void setIsDead(boolean isDead) {
        this.isDead = isDead;
    }

    public boolean getIsDead() {
        return isDead;
    }

    private int foodYield;
    private int productionYield;
    private int goldYield;
    private Technology technologyRequired;
    private ArrayList<Enum<?>> canBeFoundOn;

    public static void initAll() {
        CAMP.init(1,0,0,0,Technology.TRAPPING,new ArrayList<Enum<?>>(Arrays.asList(new Enum<?>[]{TerrainType.TerrainFeature.FOREST,TerrainType.TUNDRA,TerrainType.PLAINS,TerrainType.HILL})));
        FARM.init(2,1,0,0,Technology.AGRICULTURE,new ArrayList<Enum<?>>(Arrays.asList(new Enum<?>[]{TerrainType.PLAINS,TerrainType.DESERT,TerrainType.GRASSLAND})));
        LUMBER_MILL.init(3,0,1,0,Technology.CONSTRUCTION,new ArrayList<Enum<?>>(Arrays.asList(new Enum<?>[]{TerrainType.TerrainFeature.FOREST})));
        MINE.init(4,0,1,0,Technology.MINING,new ArrayList<Enum<?>>(Arrays.asList(new Enum<?>[]{TerrainType.PLAINS,TerrainType.DESERT,TerrainType.GRASSLAND,TerrainType.TUNDRA,TerrainType.SNOW,TerrainType.HILL,TerrainType.TerrainFeature.FOREST, TerrainType.TerrainFeature.JUNGLE,TerrainType.TerrainFeature.MARSH})));
        PASTURE.init(5,0,0,0,Technology.ANIMAL_HUSBANDRY,new ArrayList<Enum<?>>(Arrays.asList(new Enum<?>[]{TerrainType.PLAINS,TerrainType.DESERT,TerrainType.GRASSLAND,TerrainType.TUNDRA,TerrainType.HILL})));
        PLANTATION.init(6,0,0,0,Technology.CALENDAR,new ArrayList<Enum<?>>(Arrays.asList(new Enum<?>[]{TerrainType.PLAINS,TerrainType.DESERT,TerrainType.GRASSLAND,TerrainType.TerrainFeature.FOREST,TerrainType.TerrainFeature.JUNGLE,TerrainType.TerrainFeature.MARSH, TerrainType.TerrainFeature.FLOOD_PLAINS})));
        QUARRY.init(7,0,0,0,Technology.MASONRY,new ArrayList<Enum<?>>(Arrays.asList(new Enum<?>[]{TerrainType.PLAINS,TerrainType.DESERT,TerrainType.GRASSLAND,TerrainType.TUNDRA,TerrainType.HILL})));
        TRADING_POST.init(8,0,0,1,Technology.TRAPPING,new ArrayList<Enum<?>>(Arrays.asList(new Enum<?>[]{TerrainType.PLAINS,TerrainType.DESERT,TerrainType.GRASSLAND,TerrainType.TUNDRA})));
        FACTORY.init(9,0,2,0,Technology.ENGINEERING,new ArrayList<Enum<?>>(Arrays.asList(new Enum<?>[]{TerrainType.PLAINS,TerrainType.DESERT,TerrainType.GRASSLAND,TerrainType.TUNDRA,TerrainType.SNOW})));
    }

    void init(int id, int foodYield, int productionYield, int goldYield, Technology technologyRequired, ArrayList<Enum<?>> canBeFoundOn) {
        this.id = id;
        this.foodYield = foodYield;
        this.productionYield = productionYield;
        this.goldYield = goldYield;
        this.technologyRequired = technologyRequired;
        this.canBeFoundOn = canBeFoundOn;
    }
    public int getId() {
        return id;
    }
    public int getFoodYield() {
        if(isDead == true) return 0;
        return foodYield;
    }
    public int getProductionYield(){
        if(isDead == true) return 0;
        return productionYield;
    }
    public int getGoldYield() {
        if(isDead == true) return 0;
        return goldYield;
    }
    public Technology getTechnologyRequired() {
        return technologyRequired;
    }
    public ArrayList<Enum<?>> getCanBeFoundOn() {
        return canBeFoundOn;
    }
}
