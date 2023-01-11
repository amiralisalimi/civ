package ir.ap.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import ir.ap.model.*;
import ir.ap.model.TerrainType.TerrainFeature;
import ir.ap.model.Tile.TileKnowledge;

public class MapController extends AbstractGameController {
    
    public MapController(GameArea gameArea) {
        super(gameArea);
    }

    public void initCivilizationPositions(){
        ArrayList<Tile> khoshkiHas = new ArrayList<>()  ;
        for(int i = 0 ; i < gameArea.getMap().getKhoshkiHa().size(); i ++){
            Tile tile = gameArea.getMap().getKhoshkiHa().get( i );
            if (tile.getTerrainType() != TerrainType.MOUNTAIN &&
                tile.getTerrainType() != TerrainType.OCEAN &&
                tile.getTerrainFeature() != TerrainFeature.ICE) {
                khoshkiHas.add( tile );
            }
        }
        Collections.shuffle( khoshkiHas , new Random( gameArea.getSeed() ) );
        int i = 0;
        for( Civilization civilization : gameArea.getCiv2user().keySet() ){
            unitController.addUnit(civilization, khoshkiHas.get( i ), UnitType.SETTLER);
            unitController.addUnit(civilization, khoshkiHas.get( i ), UnitType.WARRIOR);
            i ++;
        }
    }

    public Tile getTileById(int tileId) {
        return gameArea.getTileById(tileId);
    }

    public Tile getTileByIndices(int x, int y) {
        return gameArea.getTileByIndices(x, y);
    }

    public boolean civCanSee(Civilization civ, Tile tile) {
        return gameArea.getTileKnowledgeByCivilization(civ, tile) != TileKnowledge.FOG_OF_WAR;
    }

    public boolean civCanSee(Civilization civ, City city) {
        if (city == null)
            return false;
        return civCanSee(civ, city.getTile());
    }

    public boolean addImprovement(Tile tile, Improvement impr) {
        if (tile == null || impr == null) return false;
        tile.setImprovement(impr);
        return true;
    }

    public boolean addRoad(Tile tile) {
        if (tile == null) return false;
        tile.setHasRoad(true);
        return true;
    }

    public boolean addRailRoad(Tile tile) {
        if (tile == null) return false;
        tile.setHasRailRoad(true);
        return true;
    }

    public boolean removeRoad(Tile tile) {
        if (tile == null) return false;
        tile.setHasRoad(false);
        gameArea.getMap().updateDistances();
        return true;
    }

    public boolean removeRailRoad(Tile tile) {
        if (tile == null) return false;
        tile.setHasRailRoad(false);
        gameArea.getMap().updateDistances();
        return true;
    }

    public boolean removeTerrainFeature(Tile tile) {
        if (tile == null) return false;
        tile.setTerrainFeature(null);
        gameArea.getMap().updateDistances();
        return true;
    }

    public int getDistanceInTiles(Tile tile1, Tile tile2) {
        return gameArea.getDistanceInTiles(tile1, tile2);
    }
    
    public int getWeightedDistance(Tile tile1, Tile tile2) {
        return gameArea.getWeightedDistance(tile1, tile2);
    }

    public ArrayList<Tile> getTilesInRange(Tile tile, int range, boolean checkIsNonBlock) {
        if (tile == null)
            return new ArrayList<>();
        ArrayList<Tile> retTiles = new ArrayList<>();
        HashMap<Tile, Integer> dist = new HashMap<>();
        Queue<Tile> queue = new LinkedList<>();
        queue.add(tile);
        dist.put(tile, 0);
        while (!queue.isEmpty()) {
            Tile curTile = queue.poll();
            retTiles.add(curTile);
            if (curTile != tile && checkIsNonBlock && curTile.isBlock())
                continue;
            for (Tile tileInDepth : curTile.getNeighbors()) {
                if (tileInDepth == null) continue;
                int curDist = dist.get(curTile);
                int depthDist = curDist + 1;
                if (dist.get(tileInDepth) == null && depthDist <= range) {
                    dist.put(tileInDepth, depthDist);
                    queue.add(tileInDepth);
                }
            }
        }
        return retTiles;
    }

    public ArrayList<Tile> getTilesInRange(Tile tile, int range) {
        return getTilesInRange(tile, range, false);
    }

    public ArrayList<Tile> getTilesInRange(Unit unit, int dist) {
        if (unit == null)
            return new ArrayList<>();
        return getTilesInRange(unit.getTile(), dist);
    }

    public ArrayList<Tile> getTilesInRange(City city, int dist) {
        if (city == null)
            return new ArrayList<>();
        return getTilesInRange(city.getTile(), dist);
    }

    public ArrayList<Tile> getTilesInRange(Unit unit, int dist, boolean checkIsNonBlock) {
        if (unit == null)
            return new ArrayList<>();
        return getTilesInRange(unit.getTile(), dist, checkIsNonBlock);
    }

    public ArrayList<Tile> getTilesInRange(City city, int dist, boolean checkIsNonBlock) {
        if (city == null)
            return new ArrayList<>();
        return getTilesInRange(city.getTile(), dist, checkIsNonBlock);
    }

    public ArrayList<Tile> getUnitVisitingTilesInRange(Unit unit, int range) {
        return getTilesInRange(unit, range, true);
    }

    public ArrayList<Tile> getUnitVisitingTiles(Unit unit) {
        return getUnitVisitingTilesInRange(unit, unit.getVisitingRange());
    }
}
