package ir.ap.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Map implements Serializable {
    public static final int MAX_H = 30, MAX_W = 30;
    private int mapH = MAX_H;
    private int mapW = MAX_W;
    private Tile[][] tiles = new Tile[ 35 ][ 35 ]; // tiles[ i ][ j ] => i: satr , j: sotoon . But be careful that tile.index/30: sotoon , tile.index%30: satr 
    private ArrayList<Tile> khoshkiHa = new ArrayList<>();
    private int[][] dist = new int[905][905] ;// INF if no path
    private int[][] distNonWeighted = new int[905][905] ;// INF if no path
    private int[][] nextWeightedDist = new int[905][905] ;// -1 if no path
    private int[][] nextNonWeightedDist = new int[905][905] ;// -1 if no path
    private final int INF = 1000000000; 
    
    private void generateRandomMap(long seed){
        Random randomobj = new Random();
        randomobj.setSeed(seed);
        // Set Tiles, khoshkiHa, initializing dist[][]
        // First Tiles in columns are has this pattern => down - up - down - up - ...

        for(int i = 0 ; i < 30 ; i ++){
            for(int j = 0 ; j < 30 ; j ++){
                if( (i >= 10 && i < 20) && (j >= 5 && j < 25) )continue ;
                Tile tile = new Tile( (i*30)+j, j, i, TerrainType.OCEAN, null, new ArrayList<>(Arrays.asList(new Resource[]{})));
                tiles[ j ][ i ] = tile;
            }
        }

        for(int i = 0 ; i < 30*30 ; i ++){
            for(int j = 0 ; j < 30*30 ; j ++){
                if (i == j) {
                    dist[i][i] = 0;
                    distNonWeighted[i][i] = 0;
                    nextWeightedDist[i][i] = i;
                    nextNonWeightedDist[i][i] = i;
                } 
                else {
                    dist[ i ][ j ] = INF;
                    distNonWeighted[ i ][ j ] = INF;
                    nextWeightedDist[ i ][ j ] = -1;
                    nextNonWeightedDist[ i ][ j ] = -1;
                }
            }
        }

        ArrayList<TerrainType> allTerrainTypes = new ArrayList<>(); // Ocean is not needed
        for(int i = 0 ; i < TerrainType.values().length ; i ++){
            if( TerrainType.values()[ i ] != TerrainType.OCEAN ) allTerrainTypes.add( TerrainType.values()[ i ]);
        }
        Resource[] allResources = Resource.values(); 

        for(int i = 10 ; i < 20 ; i ++){
            for(int j = 5 ; j < 25 ; j ++){
                TerrainType terrainType ;
                TerrainType.TerrainFeature terrainFeature = null;
                ArrayList<Resource> resources = new ArrayList<>() ;
                
                terrainType = allTerrainTypes.get( randomobj.nextInt(allTerrainTypes.size()) ) ;
                if (terrainType.getFeaturesPossible().size() > 0){
                    if( randomobj.nextInt( 3 ) == 0 )terrainFeature = terrainType.getFeaturesPossible().get( randomobj.nextInt( terrainType.getFeaturesPossible().size() ) );
                }
                for(int z = 0 ; z < allResources.length; z ++){
                    if( (terrainType.isResourcePossible( allResources[ z ] ) == true) ||
                        (terrainFeature != null && terrainFeature.isResourcePossible( allResources[ z ] ) == true) ){
                        if( randomobj.nextInt( 4 ) == 0 ){
                            resources.add( allResources[ z ] );
                        }
                    }
                }
                Tile tile = new Tile((i*30)+j, j, i, terrainType, terrainFeature, resources) ;
                tiles[ j ][ i ] = tile;
                khoshkiHa.add( tile );
            }
        }
        
        // add neighborhoods
        for(int i = 0 ; i < 30 ; i ++){
            for(int j = 0 ; j < 30 ; j ++){
                Tile tile = this.getTileByIndex( (i*30)+j );// i: sotoon , j: satr
                int index = tile.getIndex();
                setNeighborAndWeight(tile, this.getTileByIndex(index-1), Direction.UP);
                setNeighborAndWeight(tile, this.getTileByIndex(index+1), Direction.DOWN);
                if( i%2 == 0 )setNeighborAndWeight(tile, this.getTileByIndex(index-30), Direction.UP_LEFT);
                else setNeighborAndWeight(tile, this.getTileByIndex(index-31), Direction.UP_LEFT);
                if( i%2 == 0 )setNeighborAndWeight(tile, this.getTileByIndex(index-29), Direction.DOWN_LEFT);
                else setNeighborAndWeight(tile, this.getTileByIndex(index-30), Direction.DOWN_LEFT);
                if( i%2 == 0 )setNeighborAndWeight(tile, this.getTileByIndex(index+30), Direction.UP_RIGHT);
                else setNeighborAndWeight(tile, this.getTileByIndex(index+29), Direction.UP_RIGHT);
                if( i%2 == 0 )setNeighborAndWeight(tile, this.getTileByIndex(index+31), Direction.DOWN_RIGHT);
                else setNeighborAndWeight(tile, this.getTileByIndex(index+30), Direction.DOWN_RIGHT);
            }
        }
        // add rivers and its weight's changes
        for(int i = 10 ; i < 20 ; i ++){
            for(int j = 5 ; j < 25 ; j ++){                
                Tile tile = this.getTileByIndex( (i*30)+j );
                for(int z = 1 ; z <= 3 ; z ++){
                    boolean is_river = false;
                    if( randomobj.nextInt( 10 ) == 1 ){
                        is_river = true;
                    }
                    tile.setHasRiverOnSide(z, is_river);
                    Tile neighbor = tile.getNeighborOnSide( z );
                    if( neighbor == null )continue;
                    neighbor.setHasRiverOnSide((z+3)%6, is_river);
                    if( is_river == true ){
                        setRiverWeight(tile, neighbor, z);
                        setRiverWeight(neighbor, tile, (z+3)%6);
                    }
                }    
            }
        }
        updateDistances();
        updateNonWeightedDistances();
    }

    public Map(long seed) {            
        generateRandomMap(seed);
    }

    public int getMapH() { return this.mapH; }

    public int getMapW() { return this.mapW; }

    private void setNeighbor(Tile tile, Tile neighbor, Direction direction){
        if( tile == null || neighbor == null )return;       
        tile.setNeighborOnSide(direction, neighbor);        
        distNonWeighted[ tile.getIndex() ][ neighbor.getIndex() ] = 1;
        nextNonWeightedDist[ tile.getIndex() ][ neighbor.getIndex() ] = neighbor.getIndex();
    }   
    
    private void setWeight(Tile tile, Tile neighbor, Direction direction){
        if( tile == null || neighbor == null )return;       
        int weight1 = neighbor.getMovementCost();
        tile.setWeightOnSide(direction, weight1);
        dist[ tile.getIndex() ][ neighbor.getIndex() ] = weight1;
        nextWeightedDist[ tile.getIndex() ][ neighbor.getIndex() ] = neighbor.getIndex();
    }

    private void setNeighborAndWeight(Tile tile, Tile neighbor, Direction direction){
        setNeighbor(tile, neighbor, direction);
        setWeight(tile, neighbor, direction);
    }

    private void setRiverWeight(Tile tile, Tile neighbor, int dirId){
        if( tile == null || neighbor == null )return;
        if ( (tile.getHasRoad() && neighbor.getHasRoad()) || (tile.getHasRailRoad() && neighbor.getHasRailRoad()) ){
            tile.setWeightOnSide(dirId, neighbor.getMovementCostWithoutRoadAndRailRoad());
            dist[ tile.getIndex() ][ neighbor.getIndex() ] = neighbor.getMovementCostWithoutRoadAndRailRoad();
            nextWeightedDist[ tile.getIndex() ][ neighbor.getIndex() ] = neighbor.getIndex();    
        }
        else{
            tile.addWeightOnSide(dirId, 5);
            dist[ tile.getIndex() ][ neighbor.getIndex() ] = tile.getWeightOnSide(dirId);
            nextWeightedDist[ tile.getIndex() ][ neighbor.getIndex() ] = neighbor.getIndex();    
        }
    }
    
    public Tile getTileByIndex( int index ){
        if( index < 0 || index > 900 )return null;
        return tiles[ index%30 ][ index/30 ];
    }

    public Tile getTileByIndices(int x, int y) {
        if (x < 0 || x > MAX_H || y < 0 || y > MAX_W)
            return null;
        return tiles[x][y];
    }

    public void updateNonWeightedDistances(){
        for(int k = 0 ; k < khoshkiHa.size() ; k ++){
            for(int i = 0 ; i < khoshkiHa.size() ; i ++){
                for(int j = 0 ; j < khoshkiHa.size() ; j ++){
                    int i1 = khoshkiHa.get( i ).getIndex() ;
                    int j1 = khoshkiHa.get( j ).getIndex() ;
                    int k1 = khoshkiHa.get( k ).getIndex() ;
                    if( distNonWeighted[ i1 ][ j1 ] > (distNonWeighted[ i1 ][ k1 ] + distNonWeighted[ k1 ][ j1 ]) && (distNonWeighted[ k1 ][ j1 ] != INF && distNonWeighted[ i1 ][ k1 ] != INF) ){
                        distNonWeighted[ i1 ][ j1 ] = distNonWeighted[ i1 ][ k1 ] + distNonWeighted[ k1 ][ j1 ] ;
                        nextNonWeightedDist[ i1 ][ j1 ] = nextNonWeightedDist[ i1 ][ k1 ];
                    }
                }
            }
        }
    }

    void initDists(){
        for(int i = 0 ; i < 900 ; i ++){
            for(int j = 0 ; j < 900 ; j ++){
                if (i == j) {
                    dist[i][j] = 0;
                    nextWeightedDist[i][j] = i;
                }
                else {
                    dist[ i ][ j ] = INF;
                    nextWeightedDist[ i ][ j ] = -1;
                }
            }
        }
        for(int i = 0 ; i < khoshkiHa.size() ; i ++){
            Tile tile = khoshkiHa.get( i );
            for(int z = 0 ; z < 6 ; z ++){
                Direction direction = Direction.getDirectionById( z );
                Tile neighbor = tile.getNeighborOnSide( z );
                if( neighbor == null )continue;
                setWeight(tile, neighbor, direction);
                if( tile.getHasRiverOnSide( z ) == true )setRiverWeight(tile, neighbor, z);
            } 
        }
    }

    public void updateDistances(){
        initDists();
        for(int k = 0 ; k < khoshkiHa.size() ; k ++){
            for(int i = 0 ; i < khoshkiHa.size() ; i ++){
                for(int j = 0 ; j < khoshkiHa.size() ; j ++){
                    int i1 = khoshkiHa.get( i ).getIndex() ;
                    int j1 = khoshkiHa.get( j ).getIndex() ;
                    int k1 = khoshkiHa.get( k ).getIndex() ;
                    if( dist[ i1 ][ j1 ] > (dist[ i1 ][ k1 ] + dist[ k1 ][ j1 ]) && (dist[ k1 ][ j1 ] != INF && dist[ i1 ][ k1 ] != INF) ){
                        dist[ i1 ][ j1 ] = dist[ i1 ][ k1 ] + dist[ k1 ][ j1 ] ;
                        nextWeightedDist[ i1 ][ j1 ] = nextWeightedDist[ i1 ][ k1 ] ;
                    }
                }
            }
        }
    }

    public int getDistanceInTiles(Tile tile1, Tile tile2){
        return distNonWeighted[ tile1.getIndex() ][ tile2.getIndex() ];
    }

    public int getWeightedDistance(Tile tile1, Tile tile2){
        return dist[ tile1.getIndex() ][ tile2.getIndex() ];
    }

    public Tile getNextTileInWeightedShortestPath(Tile tile1, Tile tile2){
        return getTileByIndex( nextWeightedDist[ tile1.getIndex() ][ tile2.getIndex() ] );
    }

    public Tile getNextTileInNonWeightedShortestPath(Tile tile1, Tile tile2){
        return getTileByIndex( nextNonWeightedDist[ tile1.getIndex() ][ tile2.getIndex() ] );
    }
    
    public Tile[][] getTiles() {
        return tiles;
    }

    public ArrayList<Tile> getKhoshkiHa() {
        return khoshkiHa;
    }

    public int[][] getDist() {
        return dist;
    }
    
}
