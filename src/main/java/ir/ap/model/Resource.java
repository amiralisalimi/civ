package ir.ap.model;

import java.util.ArrayList;
import java.util.Arrays;

public enum Resource {

    BANANAS,
    CATTLE,
    DEER,
    SHEEP,
    WHEAT,
    COAL,
    HORSES,
    IRON,
    COTTON,
    DYES,
    FURS,
    GEMS,
    GOLD,
    INCENSE,
    IVORY,
    MARBLE,
    SILK,
    SILVER,
    SUGAR;
    
    public enum ResourceType {
        BONUS,
        STRATEGIC,
        LUXURY;
    }

    public static void initAll() {
        BANANAS.init(1,1,0,0,new ArrayList<Enum<?>>(Arrays.asList(new Enum<?>[]{TerrainType.TerrainFeature.JUNGLE})),Improvement.PLANTATION,null, ResourceType.BONUS);
        CATTLE.init(2,1,0,0,new ArrayList<Enum<?>>(Arrays.asList(new Enum<?>[]{TerrainType.GRASSLAND})),Improvement.PASTURE,null, ResourceType.BONUS);
        DEER.init(3,1,0,0,new ArrayList<Enum<?>>(Arrays.asList(new Enum<?>[]{TerrainType.TerrainFeature.FOREST,TerrainType.TUNDRA,TerrainType.HILL})),Improvement.CAMP,null, ResourceType.BONUS);
        SHEEP.init(4,1,0,0,new ArrayList<Enum<?>>(Arrays.asList(new Enum<?>[]{TerrainType.PLAINS,TerrainType.GRASSLAND,TerrainType.DESERT,TerrainType.HILL})),Improvement.PASTURE,null, ResourceType.BONUS);
        WHEAT.init(6,1,0,0,new ArrayList<Enum<?>>(Arrays.asList(new Enum<?>[]{TerrainType.PLAINS,TerrainType.TerrainFeature.FLOOD_PLAINS})),Improvement.FARM,null, ResourceType.BONUS);
        COAL.init(8,0,1,0,new ArrayList<Enum<?>>(Arrays.asList(new Enum<?>[]{TerrainType.PLAINS, TerrainType.HILL, TerrainType.GRASSLAND})),Improvement.MINE,Technology.SCIENTIFIC_THEORY, ResourceType.STRATEGIC);
        HORSES.init(9,0,1,0,new ArrayList<Enum<?>>(Arrays.asList(new Enum<?>[]{TerrainType.TUNDRA,TerrainType.PLAINS,TerrainType.GRASSLAND})),Improvement.PASTURE,Technology.ANIMAL_HUSBANDRY, ResourceType.STRATEGIC);
        IRON.init(10,0,1,0,new ArrayList<Enum<?>>(Arrays.asList(new Enum<?>[]{TerrainType.TUNDRA,TerrainType.PLAINS, TerrainType.DESERT,TerrainType.HILL,TerrainType.GRASSLAND,TerrainType.SNOW})),Improvement.MINE,Technology.IRON_WORKING, ResourceType.STRATEGIC);
        COTTON.init(13,0,0,2,new ArrayList<Enum<?>>(Arrays.asList(new Enum<?>[]{TerrainType.PLAINS,TerrainType.DESERT,TerrainType.GRASSLAND})),Improvement.PLANTATION,null,ResourceType.LUXURY);
        DYES.init(14,0,0,2,new ArrayList<Enum<?>>(Arrays.asList(new Enum<?>[]{TerrainType.TerrainFeature.FOREST, TerrainType.TerrainFeature.JUNGLE})),Improvement.PLANTATION,null,ResourceType.LUXURY);
        FURS.init(15,0,0,2,new ArrayList<Enum<?>>(Arrays.asList(new Enum<?>[]{TerrainType.TerrainFeature.FOREST, TerrainType.TUNDRA})),Improvement.CAMP,null,ResourceType.LUXURY);
        GEMS.init(16,0,0,3,new ArrayList<Enum<?>>(Arrays.asList(new Enum<?>[]{TerrainType.TerrainFeature.JUNGLE,TerrainType.TUNDRA,TerrainType.PLAINS,TerrainType.DESERT,TerrainType.GRASSLAND,TerrainType.HILL})),Improvement.MINE,null,ResourceType.LUXURY);
        GOLD.init(17,0,0,2,new ArrayList<Enum<?>>(Arrays.asList(new Enum<?>[]{TerrainType.PLAINS,TerrainType.DESERT,TerrainType.GRASSLAND,TerrainType.HILL})),Improvement.MINE,null,ResourceType.LUXURY);
        INCENSE.init(18,0,0,2,new ArrayList<Enum<?>>(Arrays.asList(new Enum<?>[]{TerrainType.PLAINS,TerrainType.DESERT})),Improvement.PLANTATION,null,ResourceType.LUXURY);
        IVORY.init(19,0,0,2,new ArrayList<Enum<?>>(Arrays.asList(new Enum<?>[]{TerrainType.PLAINS})),Improvement.CAMP,null,ResourceType.LUXURY);
        MARBLE.init(20,0,0,2,new ArrayList<Enum<?>>(Arrays.asList(new Enum<?>[]{TerrainType.TUNDRA,TerrainType.PLAINS,TerrainType.DESERT,TerrainType.GRASSLAND,TerrainType.HILL})),Improvement.QUARRY,null,ResourceType.LUXURY);
        SILK.init(22,0,0,2,new ArrayList<Enum<?>>(Arrays.asList(new Enum<?>[]{TerrainType.TerrainFeature.FOREST})),Improvement.PLANTATION,null,ResourceType.LUXURY);
        SILVER.init(23,0,0,2,new ArrayList<Enum<?>>(Arrays.asList(new Enum<?>[]{TerrainType.TUNDRA,TerrainType.DESERT,TerrainType.HILL})),Improvement.MINE,null,ResourceType.LUXURY);
        SUGAR.init(25,0,0,2,new ArrayList<Enum<?>>(Arrays.asList(new Enum<?>[]{TerrainType.TerrainFeature.FLOOD_PLAINS, TerrainType.TerrainFeature.MARSH})),Improvement.PLANTATION,null,ResourceType.LUXURY);
    }

    private int id;
    private int foodYield;
    private int productionYield;
    private int goldYield;
    private ArrayList<Enum<?>> canBeFoundOn;
    private Improvement improvementRequired;
    private Technology technologyRequired;
    private ResourceType type;

    void init(int id, int foodYield, int productionYield, int goldYield, ArrayList<Enum<?>> canBeFoundOn,
            Improvement improvementRequired, Technology technologyRequired, ResourceType resourceType) {
        this.id = id;
        this.foodYield = foodYield;
        this.productionYield = productionYield;
        this.goldYield = goldYield;
        this.canBeFoundOn = canBeFoundOn;
        this.improvementRequired = improvementRequired;
        this.technologyRequired = technologyRequired;
        this.type = resourceType;
    }

    public static Resource[] getLuxuryResources() {
        return new Resource[] {COTTON, DYES, FURS, GEMS, GOLD, INCENSE, IVORY, MARBLE, SILK, SILVER, SUGAR};
    }

    public int getId() {
        return id;
    }

    public int getFoodYield() {
        return foodYield;
    }

    public int getProductionYield() {
        return productionYield;
    }

    public int getGoldYield() {
        return goldYield;
    }

    public ArrayList<Enum<?>> getCanBeFoundOn() {
        return canBeFoundOn;
    }

    public Improvement getImprovementRequired() {
        return improvementRequired;
    }

    public Technology getTechnologyRequired() {
        return technologyRequired;
    }

    public ResourceType getType() {
        return type;
    }
}
