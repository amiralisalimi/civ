package ir.ap.controller;

import java.util.Random;

import ir.ap.model.GameArea;

public abstract class AbstractGameController {
    protected static final Random RANDOM = new Random(System.currentTimeMillis());

    protected GameArea gameArea;

    protected GameController gameController;
    protected CivilizationController civController;
    protected MapController mapController;
    protected UnitController unitController;
    protected CityController cityController;

    public AbstractGameController() {
        gameArea = null;
        gameController = null;
        civController = null;
        mapController = null;
        unitController = null;
        cityController = null;
    }

    public AbstractGameController(GameArea gameArea) {
        this.gameArea = gameArea;
        gameController = null;
        civController = null;
        mapController = null;
        unitController = null;
        cityController = null;
    }

    public GameArea getGameArea() {
        return this.gameArea;
    }

    public void setGameArea(GameArea gameArea) {
        this.gameArea = gameArea;
    }

    public CivilizationController getCivilizationController() {
        return this.civController;
    }

    public void setCivilizationController(CivilizationController civController) {
        this.civController = civController;
    }

    public MapController getMapController() {
        return this.mapController;
    }

    public void setMapController(MapController mapController) {
        this.mapController = mapController;
    }

    public UnitController getUnitController() {
        return this.unitController;
    }

    public void setUnitController(UnitController unitController) {
        this.unitController = unitController;
    }

    public CityController getCityController() {
        return this.cityController;
    }

    public void setCityController(CityController cityController) {
        this.cityController = cityController;
    }

    public GameController getGameController() {
        return this.gameController;
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

}
