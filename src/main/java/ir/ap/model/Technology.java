package ir.ap.model;

import ir.ap.model.UnitType.UnitAction;

import java.util.ArrayList;
import java.util.Arrays;

public enum Technology {
    AGRICULTURE,
    ANIMAL_HUSBANDRY,
    ARCHERY,
    POTTERY,
    CALENDAR,
    MINING,
    MASONRY,
    BRONZE_WORKING,
    THE_WHEEL,
    TRAPPING,
    WRITING,
    CONSTRUCTION,
    HORSEBACK_RIDING,
    IRON_WORKING,
    MATHEMATICS,
    PHILOSOPHY,
    CIVIL_SERVICE,
    CURRENCY,
    CHIVALRY,
    THEOLOGY,
    EDUCATION,
    ENGINEERING,
    MACHINERY,
    METAL_CASTING,
    PHYSICS,
    STEEL,
    ACOUSTICS,
    ARCHAEOLOGY,
    BANKING,
    GUNPOWDER,
    CHEMISTRY,
    PRINTING_PRESS,
    ECONOMICS,
    FERTILIZER,
    METALLURGY,
    MILITARY_SCIENCE,
    RIFLING,
    SCIENTIFIC_THEORY,
    BIOLOGY,
    DYNAMITE,
    STEAM_POWER,
    ELECTRICITY,
    RADIO,
    RAILROAD,
    REPLACEABLE,
    COMBUSTION,
    TELEGRAPH;

    public static void initAll() {
        AGRICULTURE.init(1,20,new ArrayList<>(Arrays.asList(new Technology[]{})),Era.ANCIENT,new ArrayList<>(Arrays.asList(new Enum<?>[]{Improvement.FARM})));
        ANIMAL_HUSBANDRY.init(2,35, new ArrayList<>(Arrays.asList(new Technology[]{Technology.AGRICULTURE})),Era.ANCIENT,new ArrayList<>(Arrays.asList(new Enum<?>[]{Resource.HORSES, Improvement.PASTURE})));
        ARCHERY.init(3,35, new ArrayList<>(Arrays.asList(new Technology[]{Technology.AGRICULTURE})),Era.ANCIENT,new ArrayList<>(Arrays.asList(new Enum<?>[]{UnitType.ARCHER})));
        POTTERY.init(8,35, new ArrayList<>(Arrays.asList(new Technology[]{Technology.AGRICULTURE})),Era.ANCIENT,new ArrayList<>(Arrays.asList(new Enum<?>[]{Building.GRANARY})));
        CALENDAR.init(5,70, new ArrayList<>(Arrays.asList(new Technology[]{Technology.POTTERY})),Era.ANCIENT,new ArrayList<>(Arrays.asList(new Enum<?>[]{Improvement.PLANTATION})));
        MINING.init(7,20, new ArrayList<>(Arrays.asList(new Technology[]{Technology.AGRICULTURE})),Era.ANCIENT,new ArrayList<>(Arrays.asList(new Enum<?>[]{Improvement.MINE,UnitAction.REMOVE_FOREST})));
        MASONRY.init(6,55, new ArrayList<>(Arrays.asList(new Technology[]{Technology.MINING})),Era.ANCIENT,new ArrayList<>(Arrays.asList(new Enum<?>[]{Building.WALLS,Improvement.QUARRY,UnitAction.REMOVE_MARSH})));
        BRONZE_WORKING.init(4,55, new ArrayList<>(Arrays.asList(new Technology[]{Technology.MINING})),Era.ANCIENT,new ArrayList<>(Arrays.asList(new Enum<?>[]{UnitType.SPEARMAN, Building.BARRACKS,UnitAction.REMOVE_JUNGLE})));
        THE_WHEEL.init(9,55, new ArrayList<>(Arrays.asList(new Technology[]{Technology.ANIMAL_HUSBANDRY})),Era.ANCIENT,new ArrayList<>(Arrays.asList(new Enum<?>[]{UnitType.CHARIOT_ARCHER, Building.WATER_MILL,UnitAction.BUILD_ROAD})));
        TRAPPING.init(10,55, new ArrayList<>(Arrays.asList(new Technology[]{Technology.ANIMAL_HUSBANDRY})),Era.ANCIENT,new ArrayList<>(Arrays.asList(new Enum<?>[]{Improvement.TRADING_POST,Improvement.CAMP})));
        WRITING.init(11,55, new ArrayList<>(Arrays.asList(new Technology[]{Technology.POTTERY})),Era.ANCIENT,new ArrayList<>(Arrays.asList(new Enum<?>[]{Building.LIBRARY})));
        CONSTRUCTION.init(12,100, new ArrayList<>(Arrays.asList(new Technology[]{Technology.MASONRY})),Era.CLASSICAL,new ArrayList<>(Arrays.asList(new Enum<?>[]{Building.COLOSSEUM,UnitAction.BUILD_BRIDGE})));
        HORSEBACK_RIDING.init(13,100, new ArrayList<>(Arrays.asList(new Technology[]{Technology.THE_WHEEL})),Era.CLASSICAL,new ArrayList<>(Arrays.asList(new Enum<?>[]{UnitType.HORSEMAN, Building.STABLE, Building.CIRCUS})));
        IRON_WORKING.init(14,150, new ArrayList<>(Arrays.asList(new Technology[]{Technology.BRONZE_WORKING})),Era.CLASSICAL,new ArrayList<>(Arrays.asList(new Enum<?>[]{UnitType.SWORDSMAN, Building.ARMORY,Resource.IRON})));
        MATHEMATICS.init(15,100, new ArrayList<>(Arrays.asList(new Technology[]{Technology.THE_WHEEL,Technology.ARCHERY})),Era.CLASSICAL,new ArrayList<>(Arrays.asList(new Enum<?>[]{UnitType.CATAPULT, Building.COURTHOUSE})));
        PHILOSOPHY.init(16,100, new ArrayList<>(Arrays.asList(new Technology[]{Technology.WRITING})),Era.CLASSICAL,new ArrayList<>(Arrays.asList(new Enum<?>[]{Building.BURIAL_TOMB, Building.TEMPLE})));
        CIVIL_SERVICE.init(18,400, new ArrayList<>(Arrays.asList(new Technology[]{Technology.PHILOSOPHY,Technology.TRAPPING})),Era.MEDIEVAL,new ArrayList<>(Arrays.asList(new Enum<?>[]{UnitType.PIKEMAN})));
        CURRENCY.init(19,250, new ArrayList<>(Arrays.asList(new Technology[]{Technology.MATHEMATICS})),Era.MEDIEVAL,new ArrayList<>(Arrays.asList(new Enum<?>[]{Building.MARKET})));
        CHIVALRY.init(17,440, new ArrayList<>(Arrays.asList(new Technology[]{Technology.CIVIL_SERVICE,Technology.HORSEBACK_RIDING,Technology.CURRENCY})),Era.MEDIEVAL,new ArrayList<>(Arrays.asList(new Enum<?>[]{UnitType.KNIGHT, Building.CASTLE})));
        THEOLOGY.init(26,250, new ArrayList<>(Arrays.asList(new Technology[]{Technology.CALENDAR,Technology.PHILOSOPHY})),Era.MEDIEVAL,new ArrayList<>(Arrays.asList(new Enum<?>[]{Building.MONASTERY, Building.GARDEN})));
        EDUCATION.init(20,440, new ArrayList<>(Arrays.asList(new Technology[]{Technology.THEOLOGY})),Era.MEDIEVAL,new ArrayList<>(Arrays.asList(new Enum<?>[]{Building.UNIVERSITY})));
        ENGINEERING.init(21,250, new ArrayList<>(Arrays.asList(new Technology[]{Technology.MATHEMATICS,Technology.CONSTRUCTION})),Era.MEDIEVAL,new ArrayList<>(Arrays.asList(new Enum<?>[]{})));
        MACHINERY.init(22,440, new ArrayList<>(Arrays.asList(new Technology[]{Technology.ENGINEERING})),Era.MEDIEVAL,new ArrayList<>(Arrays.asList(new Enum<?>[]{UnitType.CROSSBOWMAN})));
        METAL_CASTING.init(23,240, new ArrayList<>(Arrays.asList(new Technology[]{Technology.IRON_WORKING})),Era.MEDIEVAL,new ArrayList<>(Arrays.asList(new Enum<?>[]{Building.FORGE, Building.WORKSHOP})));
        PHYSICS.init(24,440, new ArrayList<>(Arrays.asList(new Technology[]{Technology.ENGINEERING,Technology.METAL_CASTING})),Era.MEDIEVAL,new ArrayList<>(Arrays.asList(new Enum<?>[]{UnitType.TREBUCHET})));
        STEEL.init(25,440, new ArrayList<>(Arrays.asList(new Technology[]{Technology.METAL_CASTING})),Era.MEDIEVAL,new ArrayList<>(Arrays.asList(new Enum<?>[]{UnitType.LONGSWORDSMAN})));
        ACOUSTICS.init(27,650, new ArrayList<>(Arrays.asList(new Technology[]{Technology.EDUCATION})),Era.RENAISSANCE,new ArrayList<>(Arrays.asList(new Enum<?>[]{Building.OPERA_HOUSE})));
        ARCHAEOLOGY.init(28,1300, new ArrayList<>(Arrays.asList(new Technology[]{Technology.ACOUSTICS})),Era.RENAISSANCE,new ArrayList<>(Arrays.asList(new Enum<?>[]{Building.MUSEUM})));
        BANKING.init(29,650, new ArrayList<>(Arrays.asList(new Technology[]{Technology.EDUCATION,Technology.CHIVALRY})),Era.RENAISSANCE,new ArrayList<>(Arrays.asList(new Enum<?>[]{Building.SATRAP_S_COURT, Building.BANK})));
        GUNPOWDER.init(33,680, new ArrayList<>(Arrays.asList(new Technology[]{Technology.PHYSICS,Technology.STEEL})),Era.RENAISSANCE,new ArrayList<>(Arrays.asList(new Enum<?>[]{UnitType.MUSKETMAN})));
        CHEMISTRY.init(30,900, new ArrayList<>(Arrays.asList(new Technology[]{Technology.GUNPOWDER})),Era.RENAISSANCE,new ArrayList<>(Arrays.asList(new Enum<?>[]{Technology.IRON_WORKING})));
        PRINTING_PRESS.init(36,650, new ArrayList<>(Arrays.asList(new Technology[]{Technology.MACHINERY,Technology.PHYSICS})),Era.RENAISSANCE,new ArrayList<>(Arrays.asList(new Enum<?>[]{Building.THEATER})));
        ECONOMICS.init(31,900, new ArrayList<>(Arrays.asList(new Technology[]{Technology.BANKING,Technology.PRINTING_PRESS})),Era.RENAISSANCE,new ArrayList<>(Arrays.asList(new Enum<?>[]{Building.WINDMILL})));
        FERTILIZER.init(32,1300, new ArrayList<>(Arrays.asList(new Technology[]{Technology.CHEMISTRY})),Era.RENAISSANCE,new ArrayList<>(Arrays.asList(new Enum<?>[]{})));
        METALLURGY.init(34,900, new ArrayList<>(Arrays.asList(new Technology[]{Technology.GUNPOWDER})),Era.RENAISSANCE,new ArrayList<>(Arrays.asList(new Enum<?>[]{UnitType.LANCER})));
        MILITARY_SCIENCE.init(35,1300, new ArrayList<>(Arrays.asList(new Technology[]{Technology.ECONOMICS,Technology.CHEMISTRY})),Era.RENAISSANCE,new ArrayList<>(Arrays.asList(new Enum<?>[]{UnitType.CAVALRY, Building.MILITARY_ACADEMY})));
        RIFLING.init(37,1425, new ArrayList<>(Arrays.asList(new Technology[]{Technology.METALLURGY})),Era.RENAISSANCE,new ArrayList<>(Arrays.asList(new Enum<?>[]{UnitType.RIFLEMAN})));
        SCIENTIFIC_THEORY.init(38,1300, new ArrayList<>(Arrays.asList(new Technology[]{Technology.ACOUSTICS})),Era.RENAISSANCE,new ArrayList<>(Arrays.asList(new Enum<?>[]{Building.PUBLIC_SCHOOL,Resource.COAL})));
        BIOLOGY.init(39,1680, new ArrayList<>(Arrays.asList(new Technology[]{Technology.ARCHAEOLOGY,Technology.SCIENTIFIC_THEORY})),Era.INDUSTRIAL,new ArrayList<>(Arrays.asList(new Enum<?>[]{Building.HOSPITAL})));
        DYNAMITE.init(41,1900, new ArrayList<>(Arrays.asList(new Technology[]{Technology.FERTILIZER,Technology.RIFLING})),Era.INDUSTRIAL,new ArrayList<>(Arrays.asList(new Enum<?>[]{UnitType.ARTILLERY})));
        STEAM_POWER.init(46,1680, new ArrayList<>(Arrays.asList(new Technology[]{Technology.SCIENTIFIC_THEORY,Technology.MILITARY_SCIENCE})),Era.INDUSTRIAL,new ArrayList<>(Arrays.asList(new Enum<?>[]{Building.FACTORY})));
        ELECTRICITY.init(42,1900, new ArrayList<>(Arrays.asList(new Technology[]{Technology.BIOLOGY,Technology.STEAM_POWER})),Era.INDUSTRIAL,new ArrayList<>(Arrays.asList(new Enum<?>[]{Building.STOCK_EXCHANGE})));
        RADIO.init(43,2200, new ArrayList<>(Arrays.asList(new Technology[]{Technology.ELECTRICITY})),Era.INDUSTRIAL,new ArrayList<>(Arrays.asList(new Enum<?>[]{Building.BROADCAST_TOWER})));
        RAILROAD.init(44,1900, new ArrayList<>(Arrays.asList(new Technology[]{Technology.STEAM_POWER})),Era.INDUSTRIAL,new ArrayList<>(Arrays.asList(new Enum<?>[]{Building.ARSENAL, UnitAction.BUILD_RAILROAD})));
        REPLACEABLE.init(45,1900, new ArrayList<>(Arrays.asList(new Technology[]{Technology.STEAM_POWER})),Era.INDUSTRIAL,new ArrayList<>(Arrays.asList(new Enum<?>[]{UnitType.ANTI_TANK_GUN})));
        COMBUSTION.init(40,2200, new ArrayList<>(Arrays.asList(new Technology[]{Technology.REPLACEABLE,Technology.RAILROAD,Technology.DYNAMITE})),Era.INDUSTRIAL,new ArrayList<>(Arrays.asList(new Enum<?>[]{})));
        TELEGRAPH.init(47,2200, new ArrayList<>(Arrays.asList(new Technology[]{Technology.ELECTRICITY})),Era.INDUSTRIAL,new ArrayList<>(Arrays.asList(new Enum<?>[]{Building.MILITARY_BASE})));
    }

    void init(int id, int cost, ArrayList<Technology> technologiesRequired, Era era, ArrayList<Enum<?>> objectsUnlocks)
    {
        this.id = id;
        this.cost = cost;
        this.technologiesRequired = technologiesRequired;
        this.era = era;
        this.objectsUnlocks = objectsUnlocks;
    }

    private int id;
    private int cost;
    private ArrayList<Technology> technologiesRequired;
    private Era era;
    private ArrayList<Enum<?>> objectsUnlocks;

    public int getId()
    {
        return this.id;
    }
    public int getCost()
    {
        return this.cost;
    }
    public ArrayList<Technology> getTechnologiesRequired()
    {
        return this.technologiesRequired;
    }
    public Era getEra() {
        return era;
    }
    public ArrayList<Enum<?>> getObjectsUnlocks()
    {
        return this.objectsUnlocks;
    }
    public String getName(){
        return this.name();
    }
    public static Technology getTechnologyById(int techId){
        Technology[] technologies = Technology.values();
        for(int i = 0 ; i < technologies.length ; i ++){
            if( technologies[ i ].getId() == techId ){
                return technologies[ i ];
            }
        }
        return null;
    } 
    
}
