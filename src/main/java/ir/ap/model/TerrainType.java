package ir.ap.model;

import java.util.ArrayList;
import java.util.Arrays;

public enum TerrainType {

    DESERT,
    GRASSLAND,
    HILL,
    MOUNTAIN,
    OCEAN,
    PLAINS,
    SNOW,
    TUNDRA;

    public enum TerrainFeature{
        FLOOD_PLAINS,
        JUNGLE,
        ICE,
        FOREST,
        MARSH,
        OASIS,
        RIVERS;

        private int id;
        private int foodYield;
        private int productionYield;
        private int goldYield;
        private int combatModifier;
        private int movementCost;
        private ArrayList<Resource> resourcesPossible;

        public static void initAll() {
            FLOOD_PLAINS.init(3,2,0,0,-33,1,new ArrayList<>(Arrays.asList(new Resource[]{Resource.WHEAT,Resource.SUGAR})));
            JUNGLE.init(4,1,1,0,25,2,new ArrayList<>(Arrays.asList(new Resource[]{Resource.DEER,Resource.FURS,Resource.DYES,Resource.SILK})));
            ICE.init(7,0,0,0,0,1000000000,new ArrayList<>(Arrays.asList(new Resource[]{})));
            FOREST.init(8,1,-1,0,25,2,new ArrayList<>(Arrays.asList(new Resource[]{Resource.BANANAS,Resource.GEMS,Resource.DYES})));
            MARSH.init(11,-1,0,0,-33,2,new ArrayList<>(Arrays.asList(new Resource[]{Resource.SUGAR})));
            OASIS.init(13,3,0,1,-33,1,new ArrayList<>(Arrays.asList(new Resource[]{})));
            RIVERS.init(15,0,0,1,0,1,new ArrayList<>(Arrays.asList(new Resource[]{})));
        }

        void init(int id, int foodYield, int productionYield, int goldYield, int combatModifier, int movementCost, ArrayList<Resource> resourcesPossible) {
            this.id = id;
            this.foodYield = foodYield;
            this.productionYield = productionYield;
            this.goldYield = goldYield;
            this.combatModifier = combatModifier;
            this.movementCost = movementCost;
            this.resourcesPossible = resourcesPossible;
        }

        public int getId()
        {
            return this.id;
        }
        public int getFoodYield()
        {
            return this.foodYield;
        }
        public int getProductionYield()
        {
            return this.productionYield;
        }
        public int getGoldYield()
        {
            return this.goldYield;
        }
        public int getCombatModifier()
        {
            return this.combatModifier;
        }
        public int getMovementCost()
        {
            return this.movementCost;
        }

        public boolean isSourceOfWater() {
            return this == OASIS || this == RIVERS;
        }

        public ArrayList<Resource> getResourcesPossible()
        {
            return this.resourcesPossible;
        }
        public boolean isResourcePossible( Resource resource ){
            for(int i = 0 ; i < resourcesPossible.size() ; i ++){
                if( resourcesPossible.get( i ).equals( resource ) )return true;
            }
            return false;
        }
    }

    private int id;
    private int foodYield;
    private int productionYield;
    private int goldYield;
    private int combatModifier;
    private int movementCost;
    private ArrayList<TerrainFeature> featuresPossible;
    private ArrayList<Resource> resourcesPossible;

    public static void initAll() {
        DESERT.init(2,0,0,0,-33,1,new ArrayList<>(Arrays.asList(new TerrainFeature[]{TerrainFeature.OASIS,TerrainFeature.FLOOD_PLAINS})),new ArrayList<>(Arrays.asList(new Resource[]{Resource.IRON,Resource.GOLD,Resource.SILVER,Resource.GEMS,Resource.MARBLE,Resource.COTTON,Resource.INCENSE,Resource.SHEEP})));
        GRASSLAND.init(3,2,0,0,-33,1,new ArrayList<>(Arrays.asList(new TerrainFeature[]{TerrainFeature.FOREST,TerrainFeature.MARSH})),new ArrayList<>(Arrays.asList(new Resource[]{Resource.IRON,Resource.HORSES,Resource.COAL,Resource.CATTLE,Resource.GOLD,Resource.GEMS,Resource.MARBLE,Resource.COTTON,Resource.SHEEP})));
        HILL.init(4,0,2,0,25,2,new ArrayList<>(Arrays.asList(new TerrainFeature[]{TerrainFeature.JUNGLE,TerrainFeature.FOREST})),new ArrayList<>(Arrays.asList(new Resource[]{Resource.IRON,Resource.COAL,Resource.DEER,Resource.GOLD,Resource.SILVER,Resource.GEMS,Resource.MARBLE,Resource.SHEEP})));
        MOUNTAIN.init(5,0,0,0,25,1000000000,new ArrayList<>(Arrays.asList(new TerrainFeature[]{})),new ArrayList<>(Arrays.asList(new Resource[]{})));
        OCEAN.init(6,1,0,1,0,1000000000,new ArrayList<>(Arrays.asList(new TerrainFeature[]{TerrainFeature.ICE})),new ArrayList<>(Arrays.asList(new Resource[]{})));
        PLAINS.init(7,1,1,0,-33,1,new ArrayList<>(Arrays.asList(new TerrainFeature[]{TerrainFeature.JUNGLE, TerrainFeature.FOREST})),new ArrayList<>(Arrays.asList(new Resource[]{Resource.IRON,Resource.HORSES,Resource.COAL,Resource.WHEAT,Resource.GOLD,Resource.GEMS,Resource.MARBLE,Resource.IVORY,Resource.COTTON,Resource.INCENSE,Resource.SHEEP})));
        SNOW.init(8,0,0,0,-33,1,new ArrayList<>(Arrays.asList(new TerrainFeature[]{})),new ArrayList<>(Arrays.asList(new Resource[]{Resource.IRON})));
        TUNDRA.init(9,1,0,0,-33,1,new ArrayList<>(Arrays.asList(new TerrainFeature[]{TerrainFeature.FOREST})),new ArrayList<>(Arrays.asList(new Resource[]{Resource.IRON,Resource.HORSES,Resource.DEER,Resource.SILVER,Resource.GEMS,Resource.MARBLE,Resource.FURS})));
    }

    /// geters
    public int getId()
    {
        return this.id;
    }
    public int getFoodYield()
    {
        return this.foodYield;
    }
    public int getProductionYield()
    {
        return this.productionYield;
    }
    public int getGoldYield()
    {
        return this.goldYield;
    }
    public int getCombatModifier()
    {
        return this.combatModifier;
    }
    public int getMovementCost()
    {
        return this.movementCost;
    }
    public ArrayList<TerrainFeature> getFeaturesPossible()
    {
        return this.featuresPossible;
    }
    public ArrayList<Resource> getResourcesPossible()
    {
        return this.resourcesPossible;
    }
    public boolean isResourcePossible( Resource resource ){
        for(int i = 0 ; i < resourcesPossible.size() ; i ++){
            if( resourcesPossible.get( i ).equals( resource ) )return true;
        }
        return false;
    }

    void init(int id, int foodYield, int productionYield, int goldYield, int combatModifier, int movementCost, ArrayList<TerrainFeature> featuresPossible, ArrayList<Resource> resourcesPossible) {
        this.id = id;
        this.foodYield = foodYield;
        this.productionYield = productionYield;
        this.goldYield = goldYield;
        this.combatModifier = combatModifier;
        this.movementCost = movementCost;
        this.featuresPossible = featuresPossible;
        this.resourcesPossible = resourcesPossible;
    }

    public boolean isSourceOfWater() {
        return this == OCEAN;
    }
}
