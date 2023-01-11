package ir.ap.model;

import ir.ap.model.UnitType.CombatType;
import ir.ap.model.UnitType.UnitAction;

import java.io.Serializable;

public class Unit implements Serializable {
    private static final int MAX_HP = 10;
    private static final int DEFAULT_VISITING_RANGE = 2;

    private final UnitType unitType;
    private int id;
    private UnitAction unitAction;
    private int mp;
    private int hp;
    private Civilization civilization;
    private Tile tile;
    private Tile target;
    private int howManyTurnWeKeepAction;
    private boolean isSleep;
    private boolean hasAttacked;

    public Unit(int id, UnitType unitType, Civilization civilization, Tile tile) {
        this.id = id;
        this.unitType = unitType;
        unitAction = null;
        mp = unitType.getMovement();
        hp = (unitType.isCivilian() ? 0 : MAX_HP);
        this.civilization = civilization;
        this.tile = tile;
        this.target = null;
        this.isSleep = false;
        this.hasAttacked = false;
    }

    public static int getMaxHp() {
        return MAX_HP;
    }

    public int getId() {
        return id;
    }

    public int getHowManyTurnWeKeepAction() {
        return this.howManyTurnWeKeepAction;
    }

    public void setHowManyTurnWeKeepAction(int howManyTurnWeKeepAction) {
        this.howManyTurnWeKeepAction = howManyTurnWeKeepAction;
    }

    public UnitType getUnitType() {
        return unitType;
    }

    public UnitAction getUnitAction() {
        return unitAction;
    }

    public void setUnitAction(UnitAction unitAction) {
        this.unitAction = unitAction;
    }

    public int getMp() {
        return mp;
    }

    public void setMp(int mp) {
        this.mp = mp;
    }

    public void addToMp(int delta) {
        mp += delta;
    }

    public void resetMp() {
        mp = unitType.getMovement();
    }

    public boolean hasMovedThisTurn() {
        return this.mp != unitType.getMovement();
    }

    public boolean canMove() {
        return mp > 0;
    }

    public boolean canAttack() {
        return canMove() && !getHasAttacked();
    }

    public int getHp() {
        return hp;
    }

    public boolean isDead() {
        return hp <= 0;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void addToHp(int delta) {
        hp += delta;
    }

    public void resetHp() {
        hp = MAX_HP;
    }

    public void setSleep(boolean sleep) {
        this.isSleep = sleep;
    }

    public boolean getSleep() {
        return isSleep;
    }

    public Civilization getCivilization() {
        return civilization;
    }

    public void setCivilization(Civilization civilization) {
        this.civilization = civilization;
    }

    public Tile getTile() {
        return tile;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }

    public Tile getTarget() {
        return target;
    }

    public void setTarget(Tile target) {
        this.target = target;
    }

    public int getCost() {
        return unitType.getCost();
    }

    public CombatType getCombatType() {
        return unitType.getCombatType();
    }

    public int getCombatStrength() {
        int strength = unitType.getCombatStrength() +
                (unitAction == UnitAction.FORTIFY
                        || unitAction == UnitAction.FORTIFY_HEAL
                        || unitAction == UnitAction.ALERT ? 3 : 0);
        if (civilization.isUnhappy())
            strength = Math.max(1, 3 * strength / 4);
        return strength;
    }

    public int getRangedCombatStrength() {
        return unitType.getRangedCombatStrength();
    }

    public int getRange() {
        return unitType.getRange();
    }

    public int getVisitingRange() {
        return Math.max(DEFAULT_VISITING_RANGE, getRange());
    }

    public int getMovement() {
        return unitType.getMovement();
    }

    public Resource getResourceRequired() {
        return unitType.getResourceRequired();
    }

    public Technology getTechnologyRequired() {
        return unitType.getTechnologyRequired();
    }

    public Era getEra() {
        return unitType.getEra();
    }

    public boolean isCivilian() {
        return unitType.isCivilian();
    }

    public boolean isMelee(){
        return !(getCombatType() == UnitType.CombatType.ARCHERY || getCombatType() == UnitType.CombatType.SIEGE);
    }

    public void setHasAttacked(boolean value) {
        hasAttacked = value;
    }

    public boolean getHasAttacked() {
        return hasAttacked;
    }
}
