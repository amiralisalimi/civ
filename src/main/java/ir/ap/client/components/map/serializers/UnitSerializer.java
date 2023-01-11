package ir.ap.client.components.map.serializers;

public class UnitSerializer{

    private int unitTypeId;
    private String unitType;
    private String combatType;
    private int civId;
    private String unitAction;
    private int hp;
    private int mp;
    private int combatStrenght;
    private int maxMp;
    private int tileId;
    private boolean isCombat;

    public int getUnitTypeId() {
        return unitTypeId;
    }

    public int getCivId() {
        return civId;
    }

    public String getUnitAction() {
        return unitAction;
    }

    public int getHp() {
        return hp;
    }

    public int getMp() {
        return mp;
    }
    
    public int getTileId() {
        return tileId;
    }

    public boolean isCombat() {
        return isCombat;
    }

    public String getUnitType() {
        return unitType;
    }

    public int getMaxMp() {
        return maxMp;
    }

    public String getCombatType() {
        return combatType;
    }

    public int getCombatStrenght() {
        return combatStrenght;
    }

}  
