package ir.ap.client.components.map.serializers;

import java.util.ArrayList;

public class CivilizationSerializer {
    private int index;
    private String name;
    private int gold;
    private int goldYield;
    private int science;
    private int scienceYield;
    private int happiness;
    private int population;
    private CitySerializer capital;
    private int citiesCount;
    private ArrayList<CitySerializer> cities;
    private int unitsCount;
    private ArrayList<UnitSerializer> units;

    public int getIndex() {
        return index;
    }
    public String getName() {
        return name;
    }
    public int getGold() {
        return gold;
    }
    public int getGoldYield() {
        return goldYield;
    }
    public int getScience() {
        return science;
    }
    public int getScienceYield() {
        return scienceYield;
    }
    public int getHappiness() {
        return happiness;
    }
    public int getPopulation() {
        return population;
    }
    public CitySerializer getCapital() {
        return capital;
    }
    public int getCitiesCount() {
        return citiesCount;
    }
    public ArrayList<CitySerializer> getCities() {
        return cities;
    }
    public int getUnitsCount() {
        return unitsCount;
    }
    public ArrayList<UnitSerializer> getUnits() {
        return units;
    }
}
