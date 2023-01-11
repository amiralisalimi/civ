package ir.ap.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Civilization implements Serializable {
    private static final int DEFAULT_STARTING_HAPPINESS = 7;

    private int index;
    private String name;
    private City capital;

    /* cities contains all settled, annexed and puppeted cities */
    private ArrayList<City> cities;
    private ArrayList<City> citiesDestroyed;
    private ArrayList<City> citiesAnnexed;
    private ArrayList<Unit> units;

    private City selectedCity;
    private Unit selectedUnit;

    private Technology currentResearch;
    private Technology latestResearch;
    private int[] scienceSpentForResearch;

    private int gold;
    private int science;
    private int extraHappiness;

    int[] accessibleResourceCount;
    boolean[] technologyReached;
    private ArrayList<String> messageQueue = new ArrayList<>();

    public ArrayList<String> getMessageQueue() {
        return new ArrayList<>(messageQueue);
    }

    public ArrayList<String> getMessageQueue(int x) {
        ArrayList<String> out = new ArrayList<String>();
        for(int i = Math.max(messageQueue.size()-x,0); i < messageQueue.size(); i++)
            out.add(messageQueue.get(i));
        return out;
    }

    public void addToMessageQueue(String message) {
        this.messageQueue.add(message);
    }

    public Civilization(int index) {
        this.index = index;
        name = null;
        capital = null;

        cities = new ArrayList<>();
        citiesDestroyed = new ArrayList<>();
        citiesAnnexed = new ArrayList<>();
        units = new ArrayList<>();

        selectedCity = null;
        selectedUnit = null;

        currentResearch = null;
        latestResearch = null;
        scienceSpentForResearch = new int[50];

        gold = 0;
        science = 0;
        extraHappiness = DEFAULT_STARTING_HAPPINESS;

        accessibleResourceCount = new int[40];
        technologyReached = new boolean[60];
    }

    public Civilization(int index, String name, City capital) {
        this.index = index;
        this.name = name;
        this.capital = capital;

        cities = new ArrayList<>();
        if (capital != null)
            addCity(capital);
        citiesDestroyed = new ArrayList<>();
        citiesAnnexed = new ArrayList<>();
        units = new ArrayList<>();

        selectedCity = null;
        selectedUnit = null;

        currentResearch = null;
        latestResearch = null;
        scienceSpentForResearch = new int[50];

        gold = 0;
        science = 0;
        extraHappiness = DEFAULT_STARTING_HAPPINESS;

        accessibleResourceCount = new int[40];
        technologyReached = new boolean[60];
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public City getCapital() {
        return capital;
    }

    public boolean hasCapital() {
        return getCapital() != null;
    }

    public ArrayList<City> getCities() {
        return cities;
    }

    public void addCity(City city) {
        if (cities.size() == 0)
            setCapital(city);
        cities.add(city);
    }

    public ArrayList<City> getCitiesDestroyed() {
        return citiesDestroyed;
    }

    public void addCityDestroyed(City city) {
        citiesDestroyed.add(city);
    }

    public ArrayList<City> getCitiesAnnexed() {
        return citiesAnnexed;
    }

    public void addCitiesAnnexed(City city) {
        citiesAnnexed.add(city);
    }

    public ArrayList<Unit> getUnits() {
        return units;
    }

    public void addUnit(Unit unit) {
        units.add(unit);
    }

    public City getSelectedCity() {
        return selectedCity;
    }

    public void setSelectedCity(City city) {
        this.selectedCity = city;
    }

    public Unit getSelectedUnit() {
        return this.selectedUnit;
    }

    public void setSelectedUnit(Unit selectedUnit) {
        this.selectedUnit = selectedUnit;
    }

    public Technology getCurrentResearch() {
        return this.currentResearch;
    }

    public Technology getLatestResearch() {
        return this.latestResearch;
    }

    public void setCurrentResearch(Technology currentResearch) {
        this.currentResearch = currentResearch;
        latestResearch = currentResearch;
        this.addToMessageQueue("Civilization " + this.getName() + " started research about Technology " + currentResearch);
    }

    public int getScienceSpentForCurrentResearch() {
        if (getCurrentResearch() == null)
            return 0;
        return this.scienceSpentForResearch[getCurrentResearch().getId()];
    }

    public void setScienceSpentForCurrentResearch(int value) {
        if (getCurrentResearch() == null)
            return;
        this.scienceSpentForResearch[getCurrentResearch().getId()] = value;
    }

    public void addToScienceSpentForCurrentResearch(int delta) {
        if (getCurrentResearch() == null)
            return;
        this.scienceSpentForResearch[getCurrentResearch().getId()] += delta;
    }

    public int getScienceLeftForResearchFinish() {
        if (currentResearch == null) return 0;
        return Math.max(0, currentResearch.getCost() - getScienceSpentForCurrentResearch());
    }

    public int getTurnsLeftForResearchFinish() {
        return (int) Math.ceil(1.0 * getScienceLeftForResearchFinish() / getScienceYield());
    }

    public int getGold() {
        return this.gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public void addToGold(int delta) {
        this.gold += delta;
    }

    public int getGoldYield() {
        int goldYield = 0;
        for (City city : cities) {
            goldYield += city.getGoldYield();
        }
        return goldYield;
    }

    public int getScience() {
        return this.science;
    }

    public void setScience(int science) {
        this.science = science;
    }

    public void addToScience(int delta) {
        this.science += delta;
    }

    public int getScienceYield() {
        int scienceYield = (hasCapital() ? 3 : 0);
        for (City city : cities) {
            scienceYield += city.getScienceYield();
        }
        return scienceYield;
    }

    public boolean getTechnologyReached(Technology tech) {
        if (tech == null) return true;
        return this.technologyReached[tech.getId()];
    }

    public boolean technologyIsReachable(Technology tech) {
        if (getCapital() == null)
            return false;
        for (Technology requiredTech : tech.getTechnologiesRequired())
            if (!getTechnologyReached(requiredTech))
                return false;
        return true;
    }

    public void setTechnologyReached(Technology tech, boolean value) {
        if (tech == null) return;
        if(value == true)
            this.addToMessageQueue("Civilization " + this.getName() + " reached to technology " + tech);
        this.technologyReached[tech.getId()] = value;
    }

    public int getCitiesPopulationSum() {
        int res = 0;
        for (City city : cities) {
            res += city.getPopulation();
        }
        return res;
    }
    public void removeUnit(Unit unit){
        if (selectedUnit == unit)
            selectedUnit = null;
        units.remove(unit);
    }
    public void removeCity(City city){
        if (selectedCity == city)
            selectedCity = null;
        cities.remove(city);
    }
    public void setCapital(City capital) {
        this.capital = capital;
    }

    public void setName(String name) {
        this.name = name;
    }

    // public int getRealPopulation() {
    //     int res = 0;
    //     for (City city : cities) {
    //         res += city.getRealPopulation();
    //     }
    //     return res;
    // }

    public int[] getAllResourcesCount() {
        int[] resourceCount = new int[40];
        for (City city : cities) {
            ArrayList<Resource> curRscs = city.getResourcesInCity();
            for (Resource curRsc : curRscs) {
                ++resourceCount[curRsc.getId()];
            }
        }
        return resourceCount;
    }

    public int[] getAllAccessibleResourcesCount() {
        return accessibleResourceCount;
    }

    public int getResourceCount(Resource rsc) {
        int rscCnt = 0;
        for (City city : cities) {
            rscCnt += city.getResourceCount(rsc);
        }
        return rscCnt;
    }

    public int getAccessibleResourceCount(Resource rsc) {
        return accessibleResourceCount[rsc.getId()];
    }

    public void addToHappiness(int delta) {
        this.extraHappiness += delta;
    }

    public int getHappiness() {
        int happiness = this.extraHappiness;
        happiness += -getCitiesPopulationSum();
        happiness += -3 * cities.size();
        happiness += -2 * citiesAnnexed.size();

        for (Resource luxuryRsc : Resource.getLuxuryResources()) {
            happiness += (getAccessibleResourceCount(luxuryRsc) != 0 ? 4 : 0);
        }
        for (City city : cities) {
            for (Building building : city.getBuildingsInCity()) {
                happiness += building.getHappinessYield();
            }
        }

        return happiness;
    }

    public boolean isUnhappy() {
        return getHappiness() < 0;
    }

    public void finishResearch() {
        if (currentResearch == null) return;
        setTechnologyReached(currentResearch, true);
        setScienceSpentForCurrentResearch(0);
        science = 0;
        currentResearch = null;
    }

    @Override
    public boolean equals(Object other) {
        return (other != null && other instanceof Civilization) &&
                getName().equals(((Civilization) other).getName());
    }
}
