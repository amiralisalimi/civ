package ir.ap.model;

public enum UnitType implements Production {
    // Fields that their value is N/A and their type is primitive(like int, ...) are -1
    ARCHER,
    CHARIOT_ARCHER,
    SCOUT,
    SETTLER,
    SPEARMAN,
    WARRIOR,
    WORKER,
    CATAPULT,
    HORSEMAN,
    SWORDSMAN,
    CROSSBOWMAN,
    KNIGHT,
    LONGSWORDSMAN,
    PIKEMAN,
    TREBUCHET,
    CANON,
    CAVALRY,
    LANCER,
    MUSKETMAN,
    RIFLEMAN,
    ANTI_TANK_GUN,
    ARTILLERY,
    INFANTRY,
    PANZER,
    TANK;

    public enum CombatType {
        ARCHERY,
        MOUNTED,
        RECON,
        CIVILIAN,
        MELEE,
        SIEGE,
        GUNPOWDER,
        ARMORED;
    }

    public enum UnitAction {
        MOVETO, 
        SLEEP, 
        ALERT, 
        FORTIFY, 
        FORTIFY_HEAL, 
        GARRISON, 
        SETUP_RANGED, 
        ATTACK, 
        FOUND_CITY, 
        CANCEL_MISSION, 
        WAKE, 
        DELETE, 
        BUILD_ROAD,
        BUILD_RAILROAD,
        BUILD_BRIDGE,
        BUILD_FARM, 
        BUILD_MINE, 
        BUILD_TRADINGPOST, 
        BUILD_LUMBERMILL, 
        BUILD_PASTURE, 
        BUILD_CAMP, 
        BUILD_PLANTATION, 
        BUILD_QUARRY, 
        BUILD_FACTORY,
        REMOVE_JUNGLE,
        REMOVE_FOREST,
        REMOVE_MARSH,
        REMOVE_ROUTE, 
        REPAIR; 

        private Technology technologyRequired;
        private int id;

        UnitAction() {
            technologyRequired = null;
        }

        public static void initAll() {
            BUILD_ROAD.init(1, Technology.THE_WHEEL);
            BUILD_RAILROAD.init(2, Technology.RAILROAD);
            BUILD_BRIDGE.init(3, Technology.CONSTRUCTION);
            REMOVE_JUNGLE.init(4, Technology.BRONZE_WORKING);
            REMOVE_FOREST.init(5, Technology.MINING);
            REMOVE_MARSH.init(6, Technology.MASONRY);
        }

        void init(int id,Technology technologyRequired) {
            this.technologyRequired = technologyRequired;
            this.id = id;
        }

        public Technology getTechnologyRequired() {
            return technologyRequired;
        }

        public int getId(){
            return id;
        }
    }

    public static final int MAX_MOVEMENT = 5;
    private int id;
    private int cost;
    private CombatType combatType; 
    private int combatStrength;
    private int rangedCombatStrength;
    private int range;
    private int movement;
    private Resource resourceRequired;
    private Technology technologyRequired;
    private Era era;

    public static void initAll() {
        ARCHER.init(1, 70, CombatType.ARCHERY, 4, 6, 2, 2, null, Technology.ARCHERY, Era.ANCIENT);
        CHARIOT_ARCHER.init(2, 60, CombatType.MOUNTED, 3, 6, 2, 4, Resource.HORSES, Technology.THE_WHEEL, Era.ANCIENT);
        SCOUT.init(3, 25, CombatType.RECON, 4, -1, -1, 2, null, null, Era.ANCIENT);
        SETTLER.init(4, 89, CombatType.CIVILIAN, -1, -1, -1, 2, null, null, Era.ANCIENT);
        SPEARMAN.init(5, 50, CombatType.MELEE, 7, -1, -1, 2, null, Technology.BRONZE_WORKING, Era.ANCIENT);
        WARRIOR.init(6, 40, CombatType.MELEE, 6, -1, -1, 2, null, null, Era.ANCIENT);
        WORKER.init(7, 70, CombatType.CIVILIAN, -1, -1, -1, 2, null, null, Era.ANCIENT);
        CATAPULT.init(8, 100, CombatType.SIEGE, 4, 14, 2, 2, Resource.IRON, Technology.MATHEMATICS, Era.CLASSICAL);
        HORSEMAN.init(9, 80, CombatType.MOUNTED, 12, -1, -1, 4, Resource.HORSES, Technology.HORSEBACK_RIDING, Era.CLASSICAL);
        SWORDSMAN.init(10, 80, CombatType.MELEE, 11, -1, -1, 2, Resource.IRON, Technology.IRON_WORKING, Era.CLASSICAL);
        CROSSBOWMAN.init(11, 120, CombatType.ARCHERY, 6, 12, 2, 2, null, Technology.MACHINERY, Era.MEDIEVAL);
        KNIGHT.init(12, 150, CombatType.MOUNTED, 18, -1, -1, 3, Resource.HORSES, Technology.CHIVALRY, Era.MEDIEVAL);
        LONGSWORDSMAN.init(13, 150, CombatType.MELEE, 18, -1, -1, 3, Resource.IRON, Technology.STEEL, Era.MEDIEVAL);
        PIKEMAN.init(14, 100, CombatType.MELEE, 10, -1, -1, 2, null, Technology.CIVIL_SERVICE, Era.MEDIEVAL);
        TREBUCHET.init(15, 170, CombatType.SIEGE, 6, 20, 2, 2, Resource.IRON, Technology.PHYSICS, Era.MEDIEVAL);
        CANON.init(16, 250, CombatType.SIEGE, 10, 26, 2, 2, null, Technology.CHEMISTRY, Era.RENAISSANCE);
        CAVALRY.init(17, 260, CombatType.MOUNTED, 25, -1, -1, 3, Resource.HORSES, Technology.MILITARY_SCIENCE, Era.RENAISSANCE);
        LANCER.init(18, 220, CombatType.MOUNTED, 22, -1, -1, 4, Resource.HORSES, Technology.METALLURGY, Era.RENAISSANCE);
        MUSKETMAN.init(19, 120, CombatType.GUNPOWDER, 16, -1, -1, 2, null, Technology.GUNPOWDER, Era.RENAISSANCE);
        RIFLEMAN.init(20, 200, CombatType.GUNPOWDER, 25, -1, -1, 2, null, Technology.RIFLING, Era.RENAISSANCE);
        ANTI_TANK_GUN.init(21, 300, CombatType.GUNPOWDER, 32, -1, -1, 2, null, Technology.REPLACEABLE, Era.INDUSTRIAL);
        ARTILLERY.init(22, 420, CombatType.SIEGE, 16, 32, 3, 2, null, Technology.DYNAMITE, Era.INDUSTRIAL);
        INFANTRY.init(23, 300, CombatType.GUNPOWDER, 36, -1, -1, 2, null, Technology.REPLACEABLE, Era.INDUSTRIAL);
        PANZER.init(24, 450, CombatType.ARMORED, 60, -1, -1, 5, null, Technology.COMBUSTION, Era.INDUSTRIAL);
        TANK.init(25, 450, CombatType.ARMORED, 50, -1, -1, 4, null, Technology.COMBUSTION, Era.INDUSTRIAL);
    }

    void init(int id, int cost, CombatType combatType, int combatStrength, int rangedCombatStrength, int range,
            int movement, Resource resourceRequired, Technology technologyRequired, Era era) {
        this.id = id;
        this.cost = cost;
        this.combatType = combatType;
        this.combatStrength = combatStrength;
        this.rangedCombatStrength = rangedCombatStrength;
        this.range = range;
        this.movement = movement;
        this.resourceRequired = resourceRequired;
        this.technologyRequired = technologyRequired;
        this.era = era;
    }

    public int getId() {
        return id;
    }

    public int getCost() {
        return cost;
    }

    public String getName(){
       return this.name();
    }

    public CombatType getCombatType() {
        return combatType;
    }

    public int getCombatStrength() {
        return combatStrength;
    }

    public int getRangedCombatStrength() {
        return rangedCombatStrength;
    }

    public int getRange() {
        return range;
    }

    public int getMovement() {
        return movement;
    }

    public Resource getResourceRequired() {
        return resourceRequired;
    }

    public Technology getTechnologyRequired() {
        return technologyRequired;
    }

    public Era getEra() {
        return era;
    }

    public boolean isCivilian() {
        return combatType == CombatType.CIVILIAN;
    }
}
