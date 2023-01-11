package ir.ap.model;

public enum Building implements Production {
    BARRACKS,
    GRANARY,
    LIBRARY,
    MONUMENT,
    WALLS,
    WATER_MILL,
    ARMORY,
    BURIAL_TOMB,
    CIRCUS,
    COLOSSEUM,
    COURTHOUSE,
    STABLE,
    TEMPLE,
    CASTLE,
    FORGE,
    GARDEN,
    MARKET,
    MINT,
    MONASTERY,
    UNIVERSITY,
    WORKSHOP,
    BANK,
    MILITARY_ACADEMY,
    MUSEUM,
    OPERA_HOUSE,
    PUBLIC_SCHOOL,
    SATRAP_S_COURT,
    THEATER,
    WINDMILL,
    ARSENAL,
    BROADCAST_TOWER,
    FACTORY,
    HOSPITAL,
    MILITARY_BASE,
    STOCK_EXCHANGE;

    private int id;
    private int cost;
    private Era era;

    private int foodYield;
    private int productionYield;
    private int goldYield;
    private int happinessYield;
    private Technology technologyRequired;
    private Building buildingTypeRequired;
    private int maintenanceCost;

    public static void initAll() {
        BARRACKS.init(50, 80, 1, 0, 0, 0, 0, Technology.BRONZE_WORKING, null, Era.ANCIENT);
        GRANARY.init(51, 100, 1, 2, 0, 0, 0, Technology.POTTERY, null, Era.ANCIENT);
        LIBRARY.init(52, 80, 1, 0, 0, 0, 0, Technology.WRITING, null, Era.ANCIENT);
        MONUMENT.init(53, 60, 1, 0, 0, 0, 0, null, null, Era.ANCIENT);
        WALLS.init(54, 100, 1, 0, 0, 0, 0, Technology.MASONRY, null, Era.ANCIENT);
        WATER_MILL.init(55, 120, 2, 2, 0, 0, 0, Technology.THE_WHEEL, null, Era.ANCIENT);
        ARMORY.init(56, 130, 3, 0, 0, 0, 0, Technology.IRON_WORKING, Building.BARRACKS, Era.CLASSICAL);
        BURIAL_TOMB.init(57, 120, 0, 0, 0, 0, 2, Technology.PHILOSOPHY, null, Era.CLASSICAL);
        CIRCUS.init(58, 150, 3, 0, 0, 0, 3, Technology.HORSEBACK_RIDING, null, Era.CLASSICAL);
        COLOSSEUM.init(59, 150, 3, 0, 0, 0, 4, null, null, Era.CLASSICAL);
        COURTHOUSE.init(60, 200, 5, 0, 0, 0, 0, Technology.MATHEMATICS, null, Era.CLASSICAL);
        STABLE.init(61, 100, 1, 0, 2, 0, 0, Technology.HORSEBACK_RIDING, null, Era.CLASSICAL);
        TEMPLE.init(62, 120, 2, 0, 0, 0, 0, Technology.PHILOSOPHY, Building.MONUMENT, Era.CLASSICAL);
        CASTLE.init(63, 200, 3, 0, 0, 0, 0, Technology.CHIVALRY, Building.WALLS, Era.MEDIEVAL);
        FORGE.init(64, 150, 2, 0, 2, 0, 0, Technology.METAL_CASTING, null, Era.MEDIEVAL);
        GARDEN.init(65, 120, 2, 0, 0, 0, 0, Technology.THEOLOGY, null, Era.MEDIEVAL);
        MARKET.init(66, 120, 0, 0, 0, 5, 0, Technology.CURRENCY, null, Era.MEDIEVAL);
        MINT.init(67, 120, 0, 0, 0, 3, 0, Technology.CURRENCY, null, Era.MEDIEVAL);
        MONASTERY.init(68, 120, 2, 0, 0, 0, 0, Technology.THEOLOGY,null, Era.MEDIEVAL );
        UNIVERSITY.init(69, 200, 3, 0, 0, 0, 0, Technology.EDUCATION, null, Era.MEDIEVAL);
        WORKSHOP.init(70, 100, 2, 0, 2, 0, 0, Technology.METAL_CASTING, null, Era.MEDIEVAL);
        BANK.init(71, 220, 0, 0, 0, 5, 0, Technology.BANKING, Building.MARKET, Era.RENAISSANCE);
        MILITARY_ACADEMY.init(72, 350, 3, 0, 0, 0, 0, Technology.MILITARY_SCIENCE, Building.BARRACKS, Era.RENAISSANCE);
        MUSEUM.init(73, 350, 3, 0, 0, 0, 0, Technology.ARCHAEOLOGY, Building.OPERA_HOUSE, Era.RENAISSANCE);
        OPERA_HOUSE.init(74, 220, 3, 0, 0, 0, 0, Technology.ACOUSTICS, Building.TEMPLE, Era.RENAISSANCE);
        PUBLIC_SCHOOL.init(75, 350, 3, 0, 0, 0, 0, Technology.SCIENTIFIC_THEORY, Building.UNIVERSITY, Era.RENAISSANCE);
        SATRAP_S_COURT.init(76, 220, 0, 0, 0, 5, 2, Technology.BANKING, Building.MARKET, Era.RENAISSANCE);
        THEATER.init(77, 300, 5, 0, 0, 0, 4, Technology.PRINTING_PRESS, Building.COLOSSEUM, Era.RENAISSANCE);
        WINDMILL.init(78, 180, 2, 0, 4, 0, 0, Technology.ECONOMICS, null, Era.RENAISSANCE);
        ARSENAL.init(79, 350, 3, 0, 3, 0, 0,  Technology.RAILROAD, Building.MILITARY_ACADEMY, Era.INDUSTRIAL);
        BROADCAST_TOWER.init(80, 600, 3, 0, 0, 0, 0, Technology.RADIO, Building.MUSEUM, Era.INDUSTRIAL);
        FACTORY.init(81, 300, 3, 0, 5, 0, 0, Technology.STEAM_POWER, null, Era.INDUSTRIAL);
        HOSPITAL.init(82, 400, 2, 0, 0, 0, 0, Technology.BIOLOGY, null, Era.INDUSTRIAL);
        MILITARY_BASE.init(83, 450, 4, 0, 0, 0, 0, Technology.TELEGRAPH, Building.CASTLE, Era.INDUSTRIAL);
        STOCK_EXCHANGE.init(84, 650, 0, 0, 0, 4, 0, Technology.ELECTRICITY, Building.BANK, Era.INDUSTRIAL);
    }

    void init(int id, int cost, int maintenanceCost, int foodYield, int productionYield, int goldYield, int happinessYield, Technology technologyRequired, Building buildingTypeRequired, Era era) {
        this.id = id;
        this.cost = cost;
        this.maintenanceCost = maintenanceCost;
        this.foodYield = foodYield;
        this.productionYield = productionYield;
        this.goldYield = goldYield;
        this.happinessYield = happinessYield;
        this.technologyRequired = technologyRequired;
        this.buildingTypeRequired = buildingTypeRequired;
        this.era = era;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return this.name();
    }

    public Technology getTechnologyRequired() {
        return technologyRequired;
    }

    public Building getBuildingTypeRequired() {
        return buildingTypeRequired;
    }

    public int getHappinessYield() {
        return happinessYield;
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

    public int getCost() {
        return cost;
    }

    public int getMaintenanceCost() {
        return maintenanceCost;
    }
}
