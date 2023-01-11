package ir.ap.controller;

import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import ir.ap.model.*;
import ir.ap.model.TerrainType.TerrainFeature;
import ir.ap.model.Tile.TileKnowledge;
import ir.ap.model.UnitType.UnitAction;

public class GameController extends AbstractGameController implements JsonResponsor {
    public enum Message {
        GAME_STARTED("game started successfully"),
        USER_NOT_LOGGED_IN("user is not logged in"),
        USER_NOT_ON_GAME("user is not on this game"),

        INVALID_REQUEST("request is invalid"),
        E500("Server error");

        private final String msg;

        Message(String msg) {
            this.msg = msg;
        }

        @Override
        public String toString() {
            return msg;
        }
    }

    private static final String CITY_NAMES_FILE = "citynames.json";

    private ArrayList<String> invitedUsers = new ArrayList<>();
    private String creator;

    public GameController() {
        super();
    }

    public static void initAll() {
        Building.initAll();
        Improvement.initAll();
        Resource.initAll();
        Technology.initAll();
        TerrainType.initAll();
        TerrainFeature.initAll();
        UnitType.initAll();
        UnitAction.initAll();
    }

    public static void readCityNames() {
        try (Reader namesReader = new FileReader(CITY_NAMES_FILE)) {
            String[] curNames = GSON.fromJson(namesReader, String[].class);
            for (String name : curNames) {
                City.addCityName(name);
            }
        } catch (Exception ex) {
            System.out.println("Unable to read city names");
        }
    }

    public void setCreator(String username) {
        creator = username;
    }

    public String getCreator() {
        return creator;
    }

    public void addInvitedUser(String username) {
        invitedUsers.add(username);
    }

    public JsonObject removeInvitedUser(String remover, String username) {
        if (remover.equals(creator)) {
            invitedUsers.remove(username);
            User user = User.getUser(username);
            JsonObject response = new JsonObject();
            response.addProperty("type", "removed");
            response.addProperty("username", remover);
            try {
                user.setGameController(null);
                user.getInviteHandler().send(response);
            } catch (Exception e) {
                return messageToJsonObj("Could not remove player", false);
            }
            return messageToJsonObj("Player removed", true);
        } else {
            return messageToJsonObj("Only creator could remove players", false);
        }
    }

    public ArrayList<String> getArrayOfInvitedUsers() {
        return invitedUsers;
    }

    public JsonObject getInvitedUsers() {
        JsonObject result = new JsonObject();
        result.add("users", GSON.fromJson(GSON.toJson(invitedUsers), JsonArray.class));
        return setOk(result, true);
    }

    public JsonObject serializeTile(Tile tile, Civilization civ) {
        if (tile == null || civ == null)
            return null;
        JsonObject tileJsonObj = new JsonObject();
        tileJsonObj.addProperty("index", tile.getIndex());
        tileJsonObj.addProperty("x", tile.getMapX());
        tileJsonObj.addProperty("y", tile.getMapY());
        tileJsonObj.addProperty("knowledge", gameArea.getTileKnowledgeByCivilization(civ, tile).name());
        if (gameArea.getTileKnowledgeByCivilization(civ, tile) != TileKnowledge.FOG_OF_WAR) {
            tileJsonObj.addProperty("terrainTypeId", tile.getTerrainType().getId());
            if (tile.getTerrainFeature() != null) {
                tileJsonObj.addProperty("terrainFeatureId", tile.getTerrainFeature().getId());
            }
            JsonObject hasRiver = new JsonObject();
            hasRiver.addProperty("up", tile.getHasRiverOnSide(Direction.UP));
            hasRiver.addProperty("upRight", tile.getHasRiverOnSide(Direction.UP_RIGHT));
            hasRiver.addProperty("downRight", tile.getHasRiverOnSide(Direction.DOWN_RIGHT));
            hasRiver.addProperty("down", tile.getHasRiverOnSide(Direction.DOWN));
            hasRiver.addProperty("downLeft", tile.getHasRiverOnSide(Direction.DOWN_LEFT));
            hasRiver.addProperty("upLeft", tile.getHasRiverOnSide(Direction.UP_LEFT));
            tileJsonObj.add("hasRiver", hasRiver);
        }
        if (gameArea.getTileKnowledgeByCivilization(civ, tile) == TileKnowledge.VISIBLE) {
            if (tile.getImprovement() != null) {
                tileJsonObj.add("improvement", new JsonObject());
                tileJsonObj.get("improvement").getAsJsonObject().addProperty("id", tile.getImprovement().getId());
                tileJsonObj.get("improvement").getAsJsonObject().addProperty("name", tile.getImprovement().name());
                tileJsonObj.get("improvement").getAsJsonObject().addProperty("dead", tile.getImprovement().getIsDead());
            }
            if (tile.getResourceVisibleByCivilization(civ) != null) {
                tileJsonObj.add("resource", new JsonObject());
                Resource rsrc = tile.getResourceVisibleByCivilization(civ);
                tileJsonObj.get("resource").getAsJsonObject().addProperty("id", rsrc.getId());
                tileJsonObj.get("resource").getAsJsonObject().addProperty("name", rsrc.name());
                tileJsonObj.get("resource").getAsJsonObject().addProperty("improved", tile.resourceIsImproved(rsrc));
            }
            if (tile.getNonCombatUnit() != null) {
                JsonObject nonCombatUnit = serializeUnit(tile.getNonCombatUnit());
                tileJsonObj.add("nonCombatUnit", nonCombatUnit);
                // nonCombatUnit.addProperty("unitTypeId", tile.getNonCombatUnit().getUnitType().getId());
                // nonCombatUnit.addProperty("unitType", tile.getNonCombatUnit().getUnitType().getName());
                // // System.out.println("hey");
                // nonCombatUnit.addProperty("civId", tile.getNonCombatUnit().getCivilization().getIndex());
                // tileJsonObj.add("nonCombatUnit", serializeUnit(nonCombatUnit));
            }
            if (tile.getCombatUnit() != null) {
                JsonObject combatUnit = serializeUnit(tile.getCombatUnit());
                tileJsonObj.add("combatUnit", combatUnit);
                // JsonObject combatUnit = new JsonObject();
                // combatUnit.addProperty("unitTypeId", tile.getCombatUnit().getUnitType().getId());
                // combatUnit.addProperty("unitType", tile.getCombatUnit().getUnitType().getName());
                // // System.out.println(tile.getCombatUnit().getUnitType().getName());
                // combatUnit.addProperty("civId", tile.getCombatUnit().getCivilization().getIndex());
                // tileJsonObj.add("combatUnit", combatUnit);
            }
            if (tile.getOwnerCity() != null) {
                tileJsonObj.addProperty("ownerCivId", tile.getOwnerCity().getCivilization().getIndex());
            }
            if (tile.getCity() != null) {
                tileJsonObj.add("cityInTile", new JsonObject());
                tileJsonObj.get("cityInTile").getAsJsonObject().addProperty("name", tile.getCity().getName());
                tileJsonObj.get("cityInTile").getAsJsonObject().addProperty("dead", tile.getCity().isDead());
                tileJsonObj.get("cityInTile").getAsJsonObject().addProperty("civId", tile.getCity().getCivilization().getIndex());
                tileJsonObj.get("cityInTile").getAsJsonObject().addProperty("isCenter", tile.isCapitalCity());
            }
        }
        return tileJsonObj;
    }

    public JsonObject serializeCity(City city, Civilization civ, boolean fullDetails) {
        if (!mapController.civCanSee(civ, city))
            return null;
        JsonObject cityObj = new JsonObject();
        cityObj.addProperty("id", city.getId());
        cityObj.addProperty("name", city.getName());
        cityObj.addProperty("civName", city.getCivilization().getName());
        cityObj.addProperty("tileId", city.getTile().getIndex());
        cityObj.addProperty("dead", city.isDead());
        // cityObj.add("territory", new JsonArray());
        // for (Tile tile : city.getTerritory()) {
        //     ((JsonArray) cityObj.get("territory")).add(tile.getIndex());
        // }
        if( fullDetails == true ){
            cityObj.addProperty("population", city.getPopulation());
            cityObj.addProperty("defencePower", city.getCombatStrength());
            cityObj.addProperty("foodYield", city.getFoodYield());
            cityObj.addProperty("scienceYield", city.getScienceYield());
            cityObj.addProperty("goldYield", city.getGoldYield());
            cityObj.addProperty("productionYield", city.getProductionYield());
            cityObj.add("buildings", GSON.fromJson(GSON.toJson(city.getBuildingsInCity()), JsonArray.class));
            if (city.getCurrentProduction() != null)
                cityObj.add("currentProduction", serializeProduction(city.getCurrentProduction(), city));
            cityObj.addProperty("turnsNeededToReachProduction", city.getTurnsLeftForProductionConstruction());
        }
        return cityObj;
    }

    public JsonObject serializeCiv(Civilization civ, Civilization otherCiv) {
        JsonObject civObj = new JsonObject();
        civObj.addProperty("index", civ.getIndex());
        civObj.addProperty("name", civ.getName());
        civObj.addProperty("gold", civ.getGold());
        civObj.addProperty("goldYield", civ.getGoldYield());
        civObj.addProperty("science", civ.getScience());
        civObj.addProperty("scienceYield", civ.getScienceYield());
        civObj.addProperty("happiness", civ.getHappiness());
        civObj.addProperty("population", civ.getCitiesPopulationSum());
        if (mapController.civCanSee(otherCiv, civ.getCapital())) {
            civObj.add("capital", serializeCity(civ.getCapital(), otherCiv, true));
        }
        civObj.addProperty("citiesCount", civ.getCities().size());
        civObj.add("cities", new JsonArray());
        for (City city : civ.getCities()) {
            if (mapController.civCanSee(otherCiv, city)) {
                ((JsonArray) civObj.get("cities")).add(serializeCity(city, otherCiv, true));
            }
        }
        civObj.addProperty("unitsCount", civ.getUnits().size());
        civObj.add("units", new JsonArray());
        for (Unit unit : civ.getUnits()){
            ((JsonArray) civObj.get("units")).add(serializeUnit(unit));
        }
        return civObj;
    }

    public JsonObject serializeProduction(Production production){
        JsonObject productionObj = new JsonObject();
        productionObj.addProperty("name", production.getName() );
        productionObj.addProperty("id", production.getId());
        productionObj.addProperty("cost", production.getCost());
        return productionObj;
    }

    public JsonObject serializeProduction(Production production, City city){
        JsonObject productionObj = new JsonObject();
        productionObj.addProperty("name", production.getName() );
        productionObj.addProperty("id", production.getId());
        productionObj.addProperty("cost", production.getCost());
        productionObj.addProperty("turnsForConstruction", city.getTurnsLeftForProductionConstruction(production));
        return productionObj;
    }

    public JsonObject serializeTechnology(Technology technology, Civilization civ){
        JsonObject technologyObject = new JsonObject();
        technologyObject.addProperty("name", technology.getName());
        technologyObject.addProperty("id", technology.getId());
        technologyObject.addProperty("cost", technology.getCost());
        /* technologyObject.add("technologiesRequired", new JsonArray());
        for(int i = 0 ; i < technology.getTechnologiesRequired().size() ; i ++){
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", technology.getTechnologiesRequired().get( i ).getId());
            ((JsonArray) technologyObject.get("technologiesRequired")).add(jsonObject);
        } */
        technologyObject.add("objectsUnlocks", GSON.fromJson(GSON.toJson(technology.getObjectsUnlocks()), JsonArray.class));
        if (civ != null) {
            technologyObject.addProperty("turnsLeftForFinish", civ.getTurnsLeftForResearchFinish());
        }
        return technologyObject;
    }

    public JsonObject serializeTechnology(Technology technology) {
        return serializeTechnology(technology, null);
    }

    public JsonObject serializeUnit(Unit unit){
        JsonObject unitObject = new JsonObject();
        unitObject.addProperty("id", unit.getId());
        // unitObject.addProperty("unitTypeId", unit.getUnitType().getId());
        unitObject.addProperty("combatType", unit.getUnitType().getCombatType().name());
        unitObject.addProperty("unitType", unit.getUnitType().name());
        unitObject.addProperty("civId", unit.getCivilization().getIndex());
        if (unit.getUnitAction() != null)
            unitObject.addProperty("unitAction", unit.getUnitAction().name());
        unitObject.addProperty("mp", unit.getMp());
        unitObject.addProperty("maxMp", unit.getUnitType().getMovement());
        unitObject.addProperty("hp", unit.getHp());
        unitObject.addProperty("combatStrenght", unit.getUnitType().getCombatStrength());
        unitObject.addProperty("tileId", unit.getTile().getIndex());
        boolean isCombat = unit.getUnitType().isCivilian();
        if( isCombat )isCombat = false;
        else isCombat = true;
        unitObject.addProperty("isCombat", isCombat);
        return unitObject;
    }

    public JsonObject getAllUsersInGame() {
        JsonArray users = GSON.fromJson(GSON.toJson(gameArea.getCiv2user().values()), JsonArray.class);
        JsonObject result = new JsonObject();
        result.add("users", users);
        return setOk(result, true);
    }

    public JsonObject getCivilizationByUsername(String username) {
        Civilization civ = civController.getCivilizationByUsername(username);
        if (civ == null)
            return messageToJsonObj(Message.USER_NOT_ON_GAME, false);
        JsonObject response = new JsonObject();
        response.add("civ", serializeCiv(civ, civ));
        return setOk(response, true);
    }

    public String getGameEndStr() {
        return "GAME ENDED!";
    }

    public JsonObject getSelectedCity(String username) {
        Civilization civ = civController.getCivilizationByUsername(username);
        if (civ == null)
            return messageToJsonObj(Message.USER_NOT_ON_GAME, false);
        City city = civ.getSelectedCity();
        if (city == null)
            return messageToJsonObj("no selected city", false);
        JsonObject response = new JsonObject();
        JsonObject cityJson = new JsonObject();
        cityJson.addProperty("name", city.getName());
        response.add("selectedCity", cityJson);
        return setOk(response, true);
    }

    public JsonObject getSelectedUnit(String username) {
        Civilization civ = civController.getCivilizationByUsername(username);
        if (civ == null)
            return messageToJsonObj(Message.USER_NOT_LOGGED_IN, false);
        Unit unit = civ.getSelectedUnit();
        if (unit == null)
            return messageToJsonObj("no selected unit", false);
        JsonObject response = new JsonObject();
        JsonObject unitJson = new JsonObject();
        unitJson.addProperty("type", unit.getUnitType().getName());
        unitJson.addProperty("tile", unit.getTile().getIndex());
        response.add("selectedUnit", unitJson);
        return setOk(response, true);
    }

    public JsonObject getAllCivilizations(String username) {
        Civilization civ = civController.getCivilizationByUsername(username);
        if (civ == null)
            return messageToJsonObj(Message.USER_NOT_ON_GAME, false);
        JsonObject response = new JsonObject();
        response.add("civs", new JsonArray());
        for (Civilization curCiv : civController.getAllCivilizations()) {
            ((JsonArray) response.get("civs")).add(serializeCiv(curCiv, civ));
        }
        return setOk(response, true);
    }

    public JsonObject newGame(String... players) {
        gameArea = new GameArea(System.currentTimeMillis());
        int cnt = 0;
        for (String username : players) {
            if (gameArea.getUserByUsername(username) != null)
                return messageToJsonObj("duplicate users", false);
            User curUser = User.getUser(username);
            if (curUser == null || !curUser.isLogin() || curUser.getGameController() != this)
                return messageToJsonObj(Message.USER_NOT_LOGGED_IN, false);
            Civilization curCiv = new Civilization(cnt++, curUser.getNickname() + ".civ", null);;
            gameArea.addUser(curUser, curCiv);
            try {
                JsonObject enterGameJson = new JsonObject();
                enterGameJson.addProperty("username", creator);
                enterGameJson.addProperty("type", "enterGame");
                curUser.getInviteHandler().send(enterGameJson);
            } catch (Exception e) {
                System.out.println("Player " + username + " could not start the game :(");
            }
        }
        if (/*gameArea.getUserCount() < 2 || */gameArea.getUserCount() > 8)
            return messageToJsonObj("at least 2 players and at most 8 should be in game", false);
        civController = new CivilizationController(gameArea);
        mapController = new MapController(gameArea);
        unitController = new UnitController(gameArea);
        cityController = new CityController(gameArea);
        AbstractGameController[] controllers = new AbstractGameController[] {
                this, civController, mapController, unitController, cityController };
        for (AbstractGameController controller : controllers) {
            controller.setGameController(this);
            controller.setCivilizationController(civController);
            controller.setMapController(mapController);
            controller.setUnitController(unitController);
            controller.setCityController(cityController);
        }
        mapController.initCivilizationPositions();
        JsonObject response = new JsonObject();
        response.addProperty("msg", Message.GAME_STARTED.toString());
        setOk(response, true);
        response.add("civs", new JsonArray());
        for (String username : players) {
            JsonArray civsJson = response.get("civs").getAsJsonArray();
            JsonObject curCivJson = new JsonObject();
            Civilization curCiv = civController.getCivilizationByUsername(username);
            if (curCiv.getUnits().size() > 0) {
                int initX = curCiv.getUnits().get(0).getTile().getMapX();
                int initY = curCiv.getUnits().get(0).getTile().getMapY();
                JsonObject initPosJson = new JsonObject();
                initPosJson.addProperty("x", initX);
                initPosJson.addProperty("y", initY);
                curCivJson.add("initPos", initPosJson);
            }
            civsJson.add(curCivJson);
        }
        return response;
    }

    public JsonObject nextTurn(String username) {
        Civilization civilization = civController.getCivilizationByUsername(username);
        if (civilization == null)
            return messageToJsonObj("invalid username", false);
        boolean end = civController.nextTurn(civilization);
        JsonObject response = new JsonObject();
        response.addProperty("end", end);
        if (end) {
            response.addProperty("msg", getGameEndStr());
            int numberOfCivilizationThatHasCity = 0;
            Civilization winnerCivilization = null;
            for(Civilization civ : (ArrayList<Civilization>) gameArea.getCiv2user().keySet())
                if(!civilization.getCities().isEmpty()) {
                    numberOfCivilizationThatHasCity++;
                    winnerCivilization = civ;
                }
            if(numberOfCivilizationThatHasCity == 1)
            {
                response.addProperty("winner", GSON.toJson(winnerCivilization,JsonObject.class));
                setOk(response, true);
                return response;
            }

            HashMap<Civilization,Integer> scores = new HashMap<Civilization,Integer>();
            int Max = -1;
            for(Civilization civ : (ArrayList<Civilization>) gameArea.getCiv2user().keySet())
                if(!civ.getCities().isEmpty())
                {
                    int score = 0;
                    for(City city : civ.getCities())
                    {
                        score += city.getTerritory().size();
                        score += city.getPopulation();
                    }
                    for(Technology technology : Technology.values())
                         if(civ.getTechnologyReached(technology))
                            score++;

                    scores.put(civ,score);
                    if(score > Max)
                    {
                        Max = score;
                        winnerCivilization = civ;
                    }
                }
            response.addProperty("winner", GSON.toJson(winnerCivilization,JsonObject.class));
            response.addProperty("scores", GSON.toJson(scores,JsonObject.class));
            setOk(response, true);
            return response;
        }
        setOk(response, true);
        return response;
    }

    public JsonObject infoResearch(String username) {
        Civilization civilization = civController.getCivilizationByUsername(username);
        if (civilization == null)
            return messageToJsonObj("invalid username", false);
        Technology technology = civilization.getCurrentResearch();
        if( technology == null )
            return messageToJsonObj("no technology is being research", false);
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("currentResearch", serializeTechnology(technology, civilization));
        jsonObject.add("objectsUnlocksSeparated", new JsonArray());
        JsonObject improvementObject = new JsonObject();
        JsonObject resourceObject = new JsonObject();
        JsonObject unitTypeObject = new JsonObject();
        JsonObject buildingTypeObject = new JsonObject();
        JsonObject unitActionObject = new JsonObject();
        JsonObject technologyObject = new JsonObject();
        improvementObject.add("improvements", new JsonArray());
        resourceObject.add("resources", new JsonArray());
        unitTypeObject.add("unitTypes", new JsonArray());
        buildingTypeObject.add("buildingTypes", new JsonArray());
        unitActionObject.add("unitActions", new JsonArray());
        technologyObject.add("technologies", new JsonArray());
        for(int i = 0 ; i < technology.getObjectsUnlocks().size() ; i ++){
            Object obj = technology.getObjectsUnlocks().get( i );
            JsonObject jsonObject2 = new JsonObject() ;
            if( obj instanceof Improvement ){
                jsonObject2.addProperty("name" , obj.toString());
                ((JsonArray) improvementObject.get("improvements")).add(jsonObject2);
            }
            else if( obj instanceof Resource ){
                jsonObject2.addProperty("name" , obj.toString());
                ((JsonArray) resourceObject.get("resources")).add(jsonObject2);
            }
            else if( obj instanceof UnitType ){
                jsonObject2.addProperty("name" , obj.toString());
                ((JsonArray) unitTypeObject.get("unitTypes")).add(jsonObject2);
            }
            else if( obj instanceof Building){
                jsonObject2.addProperty("name" , obj.toString());
                ((JsonArray) buildingTypeObject.get("buildingTypes")).add(jsonObject2);
            }
            else if( obj instanceof UnitAction ){
                jsonObject2.addProperty("name" , obj.toString());
                ((JsonArray) unitActionObject.get("unitActions")).add(jsonObject2);
            }
            else if( obj instanceof Technology ){
                jsonObject2.addProperty("name" , obj.toString());
                ((JsonArray) technologyObject.get("technologies")).add(jsonObject2);
            }
        }
        ((JsonArray) jsonObject.get("objectsUnlocksSeparated")).add(improvementObject);
        ((JsonArray) jsonObject.get("objectsUnlocksSeparated")).add(resourceObject);
        ((JsonArray) jsonObject.get("objectsUnlocksSeparated")).add(unitTypeObject);
        ((JsonArray) jsonObject.get("objectsUnlocksSeparated")).add(buildingTypeObject);
        ((JsonArray) jsonObject.get("objectsUnlocksSeparated")).add(unitActionObject);
        ((JsonArray) jsonObject.get("objectsUnlocksSeparated")).add(technologyObject);
        JsonObject response = new JsonObject();
        response.add("research", jsonObject);
        return setOk(response, true);
    }

    public JsonObject infoUnits(String username) {
        Civilization civilization = civController.getCivilizationByUsername(username);
        if (civilization == null)
            return messageToJsonObj("invalid username", false);
        ArrayList<Unit> units = civilization.getUnits();
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("units", new JsonArray());
        for(int i = 0 ; i < units.size() ; i ++){
            Unit unit = units.get( i );
            ((JsonArray) jsonObject.get("units")).add(serializeUnit(unit));
        }    
        return setOk(jsonObject, true);
    }

    public JsonObject infoCities(String username, boolean cheat) {
        Civilization civilization = civController.getCivilizationByUsername(username);
        if (civilization == null)
            return messageToJsonObj("invalid username", false);
        ArrayList<City> cities;
        if (cheat) {
            cities = new ArrayList<>();
            for (Civilization civ : civController.getAllCivilizations()) {
                cities.addAll(civ.getCities());
            }
        } else {
            cities = civilization.getCities();
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("cities", new JsonArray());
        for(int i = 0 ; i < cities.size(); i ++){
            City city = cities.get( i );
            ((JsonArray) jsonObject.get("cities")).add(serializeCity(city, civilization, false));
        }        
        return setOk(jsonObject, true);
    }

    public JsonObject infoDiplomacy(String username) {
        // TODO: Phase 2 or 3
        Civilization civilization = civController.getCivilizationByUsername(username);
        if (civilization == null)
            return messageToJsonObj("invalid username", false);
        return JSON_FALSE;
    }

    public JsonObject infoVictory(String username) {
        // TODO: Phase 2 or 3
        return JSON_FALSE;
    }

    public JsonObject infoDemographics(String username) {
        Civilization civilization = civController.getCivilizationByUsername(username);
        if (civilization == null)
            return messageToJsonObj("invalid username", false);
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("demographics", serializeCiv(civilization, civilization));
        //TODO: emtiaz aslan pyade sazi nashode PHASE2
        return setOk(jsonObject, true);
    }

    public JsonObject infoNotifications(String username) {
        Civilization civilization = civController.getCivilizationByUsername(username);
        if (civilization == null)
            return messageToJsonObj("invalid username", false);
        JsonObject response = new JsonObject();
        response.add("notifications",
            GSON.fromJson(GSON.toJson(civilization.getMessageQueue()),
                JsonArray.class));
        return setOk(response, true);
    }

    public JsonObject infoMilitary(String username) {
        Civilization civilization = civController.getCivilizationByUsername(username);
        if (civilization == null)
            return messageToJsonObj("invalid username", false);
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("military", new JsonArray());
        for (Unit unit : civilization.getUnits()){
            ((JsonArray) jsonObject.get("military")).add(serializeUnit(unit));
        }
        return setOk(jsonObject, true);
    }

    public JsonObject infoEconomic(String username) {
        Civilization civilization = civController.getCivilizationByUsername(username);
        if (civilization == null)
            return messageToJsonObj("invalid username", false);
        ArrayList<City> cities = civilization.getCities();
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("economic", new JsonArray());
        for(int i = 0 ; i < cities.size(); i ++){
            City city = cities.get( i );
            ((JsonArray) jsonObject.get("economic")).add(serializeCity(city, civilization, true));
        }
        return setOk(jsonObject, true);
    }

    public JsonObject infoDiplomatic(String username) {
        // TODO: Phase 2 or 3
        Civilization civilization = civController.getCivilizationByUsername(username);
        if (civilization == null)
            return messageToJsonObj("invalid username", false);
        return JSON_FALSE;
    }

    public JsonObject infoDeals(String username) {
        // TODO: Phase 2 or 3
        return JSON_FALSE;
    }

    public JsonObject selectUnit(String username, int unitId) {
        Civilization civilization = civController.getCivilizationByUsername(username);
        Unit unit = unitController.getUnitById(unitId);
        if (unit == null)
            return messageToJsonObj("invalid unitId", false);
        if (civilization == null)
            return messageToJsonObj("invalid civUsername", false);
        if (!unit.getCivilization().equals(civilization))
            return messageToJsonObj("unit does not belong to your civ", false);
        // civilization.addToMessageQueue("one unit whit type " + unit.getUnitType() + " has been selected by Civilization " + civilization.getName());
        civilization.setSelectedUnit(unit);
        return messageToJsonObj("unit selected", true);
    }

    public JsonObject selectCombatUnit(String username, int tileId) {
        Civilization civilization = civController.getCivilizationByUsername(username);
        Tile tile = gameArea.getMap().getTileByIndex(tileId);
        if (tile == null)
            return messageToJsonObj("invalid tileID", false);
        Unit unit = tile.getCombatUnit();
        if (civilization == null)
            return messageToJsonObj("invalid civUsername", false);
        if (unit == null)
            return messageToJsonObj("selected tile doesn't have unit", false);
        if (!unit.getCivilization().equals(civilization))
            return messageToJsonObj("unit does not belong to your civ", false);
        // civilization.addToMessageQueue("one unit whit type " + unit.getUnitType() + " has been selected by Civilization " + civilization.getName());
        civilization.setSelectedUnit(unit);
        return messageToJsonObj("unit selected", true);
    }

    public JsonObject selectNonCombatUnit(String username, int tileId) {
        Civilization civilization = civController.getCivilizationByUsername(username);
        Tile tile = gameArea.getMap().getTileByIndex(tileId);
        if (tile == null)
            return messageToJsonObj("invalid tileID", false);
        Unit unit = tile.getNonCombatUnit();
        if (civilization == null)
            return messageToJsonObj("invalid civUsername", false);
        if (unit == null)
            return messageToJsonObj("selected tile doesn't have unit", false);
        if (!unit.getCivilization().equals(civilization))
            return messageToJsonObj("unit does not belong to your civ", false);
        // civilization.addToMessageQueue("one unit whit type " + unit.getUnitType() + " has been selected by Civilization " + civilization.getName());
        civilization.setSelectedUnit(unit);
        return messageToJsonObj("unit selected", true);
    }

    public JsonObject selectCity(String username, int tileId) {
        Civilization civilization = civController.getCivilizationByUsername(username);
        Tile tile = gameArea.getMap().getTileByIndex(tileId);
        if (tile == null)
            return messageToJsonObj("invalid tileID", false);
        City city = tile.getCity();
        if (civilization == null)
            return messageToJsonObj("invalid civUsername", false);
        if (city == null)
            return messageToJsonObj("selected tile doesn't have city", false);
        if (!city.getCivilization().equals(civilization))
            return messageToJsonObj("city does not belong to your civ", false);
        // civilization.addToMessageQueue("city " + city.getName() + " has been selected by Civilization " + civilization.getName());
        civilization.setSelectedCity(city);
        return messageToJsonObj("city selected", true);
    }

    public JsonObject selectCity(String username, String cityName) {
        Civilization civilization = civController.getCivilizationByUsername(username);
        City city = City.getCityByName(cityName);
        if (civilization == null)
            return messageToJsonObj("invalid civUsername", false);
        if (city == null)
            return messageToJsonObj("invalid cityName", false);
        if (!city.getCivilization().equals(civilization))
            return messageToJsonObj("city does not belong to your civ", false);
        // civilization.addToMessageQueue("city " + city.getName() + " has been selected by Civilization " + civilization.getName());
        civilization.setSelectedCity(city);
        return messageToJsonObj("city selected", true);
    }

    public JsonObject selectCityById(String username, int cityId) {
        Civilization civ = civController.getCivilizationByUsername(username);
        City city = cityController.getCityById(cityId);
        if (civ == null || city == null || !city.getCivilization().equals(civ))
            return messageToJsonObj(Message.INVALID_REQUEST, false);
        civ.setSelectedCity(city);
        return messageToJsonObj("city selected", true);
    }

    public JsonObject unitMoveTo(String username, int tileId, boolean cheat) {
        Civilization civilization = civController.getCivilizationByUsername(username);
        Tile tile = gameArea.getMap().getTileByIndex(tileId);
        if (civilization == null)
            return messageToJsonObj("invalid civUsername", false);
        if (tile == null)
            return messageToJsonObj("invalid tileId", false);
        if (unitController.unitMoveTo(civilization, tile, cheat) == false)
            return messageToJsonObj("something is invalid", false);
        // civilization.addToMessageQueue("one unit from civilization " + civilization.getName() + " moved to tile " + tile.getIndex());
        return messageToJsonObj("unit moved", true);
    }

    public JsonObject unitSleep(String username) {
        Civilization civilization = civController.getCivilizationByUsername(username);
        if (civilization == null)
            return messageToJsonObj("invalid civUsername", false);
        if (unitController.unitSleep(civilization) == false)
            return messageToJsonObj("something is invalid", false);
        return messageToJsonObj("unit slept", true);
    }

    public JsonObject unitAlert(String username) {

        Civilization civilization = civController.getCivilizationByUsername(username);
        if (civilization == null)
            return messageToJsonObj("invalid civUsername", false);
        if (unitController.unitAlert(civilization) == false)
            return messageToJsonObj("something is invalid", false);
        return messageToJsonObj("unit Alerted", true);
    }

    public JsonObject unitFortify(String username) {
        Civilization civilization = civController.getCivilizationByUsername(username);
        if (civilization == null)
            return messageToJsonObj("invalid civUsername", false);
        if (unitController.unitFortify(civilization) == false)
            return messageToJsonObj("something is invalid", false);
        return messageToJsonObj("unit fortified", true);
    }

    public JsonObject unitFortifyUntilHeal(String username) {
        Civilization civilization = civController.getCivilizationByUsername(username);
        if (civilization == null)
            return messageToJsonObj("invalid civUsername", false);
        if (unitController.unitFortifyHeal(civilization) == false)
            return messageToJsonObj("something is invalid", false);
        return messageToJsonObj("unit fortified to heal", true);
    }

    public JsonObject unitGarrison(String username) {
        Civilization civilization = civController.getCivilizationByUsername(username);
        if (civilization == null)
            return messageToJsonObj("invalid civUsername", false);
        if (unitController.unitGarrison(civilization) == false)
            return messageToJsonObj("something is invalid", false);
        return messageToJsonObj("unit has been garrison", true);
    }

    public JsonObject unitSetupRanged(String username) {
        Civilization civilization = civController.getCivilizationByUsername(username);
        if (civilization == null)
            return messageToJsonObj("invalid civUsername", false);
        if (unitController.unitSetupForRangedAttack(civilization) == false)
            return messageToJsonObj("something is invalid", false);
        return messageToJsonObj("unit has been set up ranged", true);
    }

    public JsonObject unitAttack(String username, int tileId, boolean cheat) {
        Civilization civilization = civController.getCivilizationByUsername(username);
        Tile tile = gameArea.getMap().getTileByIndex(tileId);
        if (civilization == null)
            return messageToJsonObj("invalid civUsername", false);
        if (tile == null)
            return messageToJsonObj("invalid tileId", false);
        if (unitController.unitAttack(civilization, tile, cheat) == false)
            return messageToJsonObj("something is invalid", false);
        civilization.addToMessageQueue("one unit from civilization " + civilization.getName() + " attacked to tile " + tile.getIndex());
        JsonObject response = new JsonObject();
        response.addProperty("msg", "Unit Attacked");
        if (tile.getCity() != null && tile.getCity().isDead() && civilization.getSelectedUnit() != null && civilization.getSelectedUnit().isMelee()) {
            response.addProperty("cityDead", tile.getCity().getId());
        }
        return setOk(response, true);
    }

    public JsonObject unitFoundCity(String username, boolean cheat) {
        Civilization civilization = civController.getCivilizationByUsername(username);
        if (civilization == null)
            return messageToJsonObj("invalid civUsername", false);
        City city = unitController.unitFoundCity(civilization, cheat);
        if (city == null)
            return messageToJsonObj("something is invalid", false);
        civilization.addToMessageQueue("one unit from civilization " + civilization.getName() + " founded city on tile " + city.getName());
        return messageToJsonObj("unit found city", true);
    }

    public JsonObject unitCancelMission(String username) {
        Civilization civilization = civController.getCivilizationByUsername(username);
        if (civilization == null)
            return messageToJsonObj("invalid civUsername", false);
        if (unitController.unitCancelMission(civilization) == false)
            return messageToJsonObj("something is invalid", false);
        return messageToJsonObj("unit mission canceled", true);
    }

    public JsonObject unitWake(String username) {
        Civilization civilization = civController.getCivilizationByUsername(username);
        if (civilization == null)
            return messageToJsonObj("invalid civUsername", false);
        if (unitController.unitWake(civilization) == false)
            return messageToJsonObj("something is invalid", false);
        return messageToJsonObj("unit has been set up ranged", true);
    }

    public JsonObject unitDelete(String username) {
        Civilization civilization = civController.getCivilizationByUsername(username);
        if (civilization == null)
            return messageToJsonObj("invalid civUsername", false);
        Unit unit = civilization.getSelectedUnit();
        if (unit == null)
            return messageToJsonObj("we don`t have selected unit", false);
        civilization.addToMessageQueue("one unit from civilization " + civilization.getName() + " of type " + civilization.getSelectedUnit().getUnitType() + " deleted");
        civilization.addToGold(unit.getCost() / 10);
        unitController.removeUnit(unit);
        return messageToJsonObj("has been deleted successfully", true);
    }

    public JsonObject unitBuildRoad(String username, boolean cheat) {
        Civilization civilization = civController.getCivilizationByUsername(username);
        if (civilization == null)
            return messageToJsonObj("invalid civUsername", false);
        if (unitController.unitBuildRoad(civilization, cheat) == false)
            return messageToJsonObj("something is invalid", false);
        return messageToJsonObj("unit started build road", true);
    }

    public JsonObject unitBuildRailRoad(String username, boolean cheat) {
        Civilization civilization = civController.getCivilizationByUsername(username);
        if (civilization == null)
            return messageToJsonObj("invalid civUsername", false);
        if (unitController.unitBuildRailRoad(civilization, cheat) == false)
            return messageToJsonObj("something is invalid", false);
        return messageToJsonObj("unit started build railroad", true);
    }

    public JsonObject unitBuildImprovement(String username, int imprId, boolean cheat) {
        Civilization civilization = civController.getCivilizationByUsername(username);
        Improvement improvement = Improvement.getImprovementById(imprId);
        if (civilization == null)
            return messageToJsonObj("invalid civUsername", false);
        if (improvement == null)
            return messageToJsonObj("invalid imprID", false);
        if (unitController.unitBuildImprovement(civilization, improvement, cheat) == false)
            return messageToJsonObj("something is invalid", false);
        return messageToJsonObj("unit started build improvement", true);
    }

    public JsonObject unitRemoveJungle(String username, boolean cheat) {
        Civilization civilization = civController.getCivilizationByUsername(username);
        if (civilization == null)
            return messageToJsonObj("invalid civUsername", false);
        if (unitController.unitRemoveJungle(civilization, cheat) == false)
            return messageToJsonObj("something is invalid", false);
        return messageToJsonObj("unit started remove jungle", true);
    }

    public JsonObject unitRemoveForest(String username, boolean cheat) {
        Civilization civilization = civController.getCivilizationByUsername(username);
        if (civilization == null)
            return messageToJsonObj("invalid civUsername", false);
        if (unitController.unitRemoveForest(civilization, cheat) == false)
            return messageToJsonObj("something is invalid", false);
        return messageToJsonObj("unit started remove forest", true);
    }

    public JsonObject unitRemoveMarsh(String username, boolean cheat) {
        Civilization civilization = civController.getCivilizationByUsername(username);
        if (civilization == null)
            return messageToJsonObj("invalid civUsername", false);
        if (unitController.unitRemoveMarsh(civilization, cheat) == false)
            return messageToJsonObj("something is invalid", false);
        return messageToJsonObj("unit started remove marsh", true);
    }

    public JsonObject unitRemoveRoute(String username, boolean cheat) {
        Civilization civilization = civController.getCivilizationByUsername(username);
        if (civilization == null)
            return messageToJsonObj("invalid civUsername", false);
        if (unitController.unitRemoveRoute(civilization, cheat) == false)
            return messageToJsonObj("something is invalid", false);
        return messageToJsonObj("unit started remove route", true);
    }

    public JsonObject unitRepair(String username, boolean cheat) {
        Civilization civilization = civController.getCivilizationByUsername(username);
        if (civilization == null)
            return messageToJsonObj("invalid civUsername", false);
        if (unitController.unitRepair(civilization, cheat) == false)
            return messageToJsonObj("something is invalid", false);
        return messageToJsonObj("unit started repair", true);
    }

    public JsonObject increaseGold(String username, int amount) {
        Civilization civilization = civController.getCivilizationByUsername(username);
        if (civilization == null)
            return messageToJsonObj("invalid civUsername", false);
        int last_gold = civilization.getGold();
        civilization.setGold(last_gold+amount);        
        return messageToJsonObj("gold added successfully", true);
    }

    public JsonObject increaseTurn(String username, int amount) {
        Civilization civ = civController.getCivilizationByUsername(username);
        if (civ == null)
            return messageToJsonObj(Message.USER_NOT_ON_GAME, false);
        boolean end = false;
        for (int i = 0; i < amount; i++) {
            end |= civController.nextTurn(civ);
            if (end) break;
        }
        JsonObject response = new JsonObject();
        response.addProperty("end", end);
        response.addProperty("msg", (end ? getGameEndStr() : "turns increased successfully"));
        return setOk(response, true);
    }

    public JsonObject increaseHappiness(String username, int amount) {
        Civilization civilization = civController.getCivilizationByUsername(username);
        if (civilization == null)
            return messageToJsonObj("invalid civUsername", false);
        if( amount < 0 )
            return messageToJsonObj("invalid amount", false);
        civilization.addToHappiness(amount);        
        return messageToJsonObj("Happiness added successfully", true);
        // Happiness in civilization
    }

    public JsonObject getTileIndexByXY(int x, int y) {
        Tile tile = mapController.getTileByIndices(x, y);
        if (tile == null)
            return messageToJsonObj("No such tile", false);
        JsonObject response = new JsonObject();
        response.addProperty("id", tile.getIndex());
        return setOk(response, true);
    }

    public JsonObject getTileById(String username, int tileId){
        Civilization civ = civController.getCivilizationByUsername(username);
        if( civ == null ){
            return messageToJsonObj("invalid civUsername", false);
        }
        Tile tile = mapController.getTileById(tileId);
        if( tile == null ){
            return messageToJsonObj("tileId is invalid", false);
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("tile", serializeTile(tile, civ));
        jsonObject.addProperty("ok", true);
        return jsonObject;
    }

    public JsonObject getTerrainTypeNameById(int id) {
        TerrainType[] terrainTypes = TerrainType.values();
        for (TerrainType terrainType : terrainTypes) {
            if (terrainType.getId() == id) {
                JsonObject response = new JsonObject();
                response.addProperty("terrainTypeId", terrainType.getId());
                response.addProperty("terrainTypeName", terrainType.name());
                return setOk(response, true);
            }
        }
        return JSON_FALSE;
    }

    public JsonObject getAllTerrainTypeIds() {
        TerrainType[] terrainTypes = TerrainType.values();
        ArrayList<Integer> ids = new ArrayList<>();
        for (TerrainType terrainType : terrainTypes) {
            ids.add(terrainType.getId());
        }
        JsonObject response = new JsonObject();
        response.add("terrainTypeIds", GSON.fromJson(GSON.toJson(ids), JsonArray.class));
        return setOk(response, true);
    }

    public JsonObject getTerrainFeatureNameById(int id) {
        TerrainFeature[] terrainFeatures = TerrainFeature.values();
        for (TerrainFeature terrainFeature : terrainFeatures) {
            if (terrainFeature.getId() == id) {
                JsonObject response = new JsonObject();
                response.addProperty("terrainFeatureId", terrainFeature.getId());
                response.addProperty("terrainFeatureName", terrainFeature.name());
                return setOk(response, true);
            }
        }
        return JSON_FALSE;
    }

    public JsonObject getAllTerrainFeatureIds() {
        TerrainFeature[] terrainFeatures = TerrainFeature.values();
        ArrayList<Integer> ids = new ArrayList<>();
        for (TerrainFeature terrainFeature : terrainFeatures) {
            ids.add(terrainFeature.getId());
        }
        JsonObject response = new JsonObject();
        response.add("terrainFeatureIds", GSON.fromJson(GSON.toJson(ids), JsonArray.class));
        return setOk(response, true);
    }

    public JsonObject getUnitTypeNameById(int id) {
        UnitType[] unitTypes = UnitType.values();
        for (UnitType unitType : unitTypes) {
            if (unitType.getId() == id) {
                JsonObject response = new JsonObject();
                response.addProperty("unitTypeId", unitType.getId());
                response.addProperty("unitTypeName", unitType.name());
                return setOk(response, true);
            }
        }
        return JSON_FALSE;
    }

    public JsonObject getAllUnitTypeIds() {
        UnitType[] unitTypes = UnitType.values();
        ArrayList<Integer> ids = new ArrayList<>();
        for (UnitType unitType : unitTypes) {
            ids.add(unitType.getId());
        }
        JsonObject response = new JsonObject();
        response.add("unitTypeIds", GSON.fromJson(GSON.toJson(ids), JsonArray.class));
        return setOk(response, true);
    }

    public JsonObject getResourceNameById(int id) {
        Resource[] resources = Resource.values();
        for (Resource resource : resources) {
            if (resource.getId() == id) {
                JsonObject response = new JsonObject();
                response.addProperty("resourceId", resource.getId());
                response.addProperty("resourceName", resource.name());
                return setOk(response, true);
            }
        }
        return JSON_FALSE;
    }

    public JsonObject getAllResourceIds() {
        Resource[] resources = Resource.values();
        ArrayList<Integer> ids = new ArrayList<>();
        for (Resource resource : resources) {
            ids.add(resource.getId());
        }
        JsonObject response = new JsonObject();
        response.add("resourceIds", GSON.fromJson(GSON.toJson(ids), JsonArray.class));
        return setOk(response, true);
    }

    public JsonObject getImprovementNameById(int id) {
        Improvement[] improvements = Improvement.values();
        for (Improvement improvement : improvements) {
            if (improvement.getId() == id) {
                JsonObject response = new JsonObject();
                response.addProperty("improvementId", improvement.getId());
                response.addProperty("improvementName", improvement.name());
                return setOk(response, true);
            }
        }
        return JSON_FALSE;
    }

    public JsonObject getAllImprovementIds() {
        Improvement[] improvements = Improvement.values();
        ArrayList<Integer> ids = new ArrayList<>();
        for (Improvement improvement : improvements) {
            ids.add(improvement.getId());
        }
        JsonObject response = new JsonObject();
        response.add("improvementIds", GSON.fromJson(GSON.toJson(ids), JsonArray.class));
        return setOk(response, true);
    }

    public JsonObject getCivilizationNameById(int id) {
        ArrayList<Civilization> civilizations = civController.getAllCivilizations();
        for (Civilization civilization : civilizations) {
            if (civilization.getIndex() == id) {
                JsonObject response = new JsonObject();
                response.addProperty("civId", civilization.getIndex());
                response.addProperty("civName", civilization.getName());
                return setOk(response, true);
            }
        }
        return JSON_FALSE;
    }

    public JsonObject getAllCivilizationIds() {
        ArrayList<Civilization> civs = civController.getAllCivilizations();
        ArrayList<Integer> ids = new ArrayList<>();
        for (Civilization civ : civs) {
            ids.add(civ.getIndex());
        }
        JsonObject response = new JsonObject();
        response.add("civIds", GSON.fromJson(GSON.toJson(ids), JsonArray.class));
        return setOk(response, true);
    }

    public JsonObject mapShow(String username, int tileId, int height, int width) {
        Civilization civ = civController.getCivilizationByUsername(username);
        if (civ == null)
            return messageToJsonObj(Message.USER_NOT_ON_GAME, false);
        Tile tile = mapController.getTileById(tileId);
        if (tile == null)
            return messageToJsonObj(Message.INVALID_REQUEST, false);
        JsonObject response = new JsonObject();
        height = Math.min(height, gameArea.getMap().getMapH());
        width = Math.min(width, gameArea.getMap().getMapW());
        response.addProperty("height", height);
        response.addProperty("width", width);
        response.add("map", new JsonArray());
        int tileX = tile.getMapX(), tileY = tile.getMapY();
        response.addProperty("focusId", tileId);
        response.addProperty("focusX", tileX);
        response.addProperty("focusY", tileY);
        int upLeftX = Math.max(0, tileX - height / 2);
        int upLeftY = Math.max(0, tileY - width / 2);
        upLeftX = Math.min(upLeftX, gameArea.getMap().getMapH() - height);
        upLeftY = Math.min(upLeftY, gameArea.getMap().getMapW() - width);
        for (int i = upLeftX; i < upLeftX + height; i++) {
            JsonArray row = new JsonArray();
            for (int j = upLeftY; j < upLeftY + width; j++) {
                row.add(serializeTile(mapController.getTileByIndices(i, j), civ));
            }
            ((JsonArray) response.get("map")).add(row);
        }
        return setOk(response, true);
    }

    public JsonObject mapShow(String username, int tileId) {
        return mapShow(username, tileId, 7, 9);
    }

    public JsonObject mapShow(String username, String cityName) {
        City city = City.getCityByName(cityName);
        if (city == null || city.getTile() == null)
            return null;
        return mapShow(username, city.getTile().getIndex());
    }

    public JsonObject cityAttack(String username, int tileId, boolean cheat) {
        Civilization civ = civController.getCivilizationByUsername(username);
        if (civ == null)
            return messageToJsonObj(Message.USER_NOT_ON_GAME, false);
        City city = civ.getSelectedCity();
        if (city == null)
            return messageToJsonObj(Message.INVALID_REQUEST, false);
        Tile target = mapController.getTileById(tileId);
        if (target == null)
            return messageToJsonObj(Message.INVALID_REQUEST, false);
        if (!cityController.cityAttack(city, target, cheat))
            return messageToJsonObj(Message.INVALID_REQUEST, false);
        civ.addToMessageQueue("city " + city.getName() + " from civilization " + civ.getName() + " attacked to tile " + target.getIndex());
        return messageToJsonObj("City attack done", true);
    }

    public JsonObject cityAddCitizenToWorkOnTile(String username, int tileId) {
        Civilization civilization = civController.getCivilizationByUsername(username);
        if (civilization == null)
            return messageToJsonObj("invalid civUsername", false);
        City city = civilization.getSelectedCity();
        if ( city == null )
            return messageToJsonObj("no city selected", false);
        Tile tile = mapController.getTileById(tileId);
        if ( tile == null )
            return messageToJsonObj("invalid tileId", false);
        if ( cityController.cityAddCitizenToWorkOnTile(city, tile) == false )
            return messageToJsonObj("something is invalid", false);
        return messageToJsonObj("citizen successfully added to work on tile", true);
    }

    public JsonObject cityRemoveCitizenFromWorkOnTile(String username, int tileId) {
        Civilization civilization = civController.getCivilizationByUsername(username);
        if (civilization == null)
            return messageToJsonObj("invalid civUsername", false);
        City city = civilization.getSelectedCity();
        if ( city == null )
            return messageToJsonObj("no city selected", false);
        Tile tile = mapController.getTileById(tileId);
        if ( tile == null )
            return messageToJsonObj("invalid tileId", false);
        if ( cityController.cityRemoveCitizenFromWork(city, tile) == false )
            return messageToJsonObj("something is invalid", false);        
        return messageToJsonObj("citizen successfully removed from working on tile", true);
    }

    public JsonObject cityGetOutput(String username) {
        Civilization civilization = civController.getCivilizationByUsername(username);
        if (civilization == null)
            return messageToJsonObj("invalid civUsername", false);
        City city = civilization.getSelectedCity();
        if ( city == null )
            return messageToJsonObj("no city selected", false);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("foodYield", city.getFoodYield());
        jsonObject.addProperty("productionYield", city.getProductionYield());
        jsonObject.addProperty("goldYield", city.getGoldYield());
        jsonObject.addProperty("scienceYield", city.getScienceYield());
        int constantCityTurn = City.TURN_NEEDED_TO_EXTEND_TILES;
        jsonObject.addProperty("turnsNeededToExtendCityTerritory", (constantCityTurn-(gameArea.getTurn()%constantCityTurn))%constantCityTurn +1);
        jsonObject.addProperty("turnsNeededToExtendCityPopulation", city.getTurnsLeftForNextCitizen());
        JsonObject response = new JsonObject();
        response.add("output", jsonObject);
        return setOk(response, true);
    }

    public JsonObject cityGetUnemployedCitizens(String username) {
        Civilization civilization = civController.getCivilizationByUsername(username);
        if (civilization == null)
            return messageToJsonObj("invalid civUsername", false);
        City city = civilization.getSelectedCity();
        if ( city == null )
            return messageToJsonObj("no city selected", false);
        if ( city.getPopulation() < city.getWorkingTiles().size() ) // Ehtemalan etefagh neofteh vali serfan baray etminan
            return messageToJsonObj("something is wrong in the city", false);
        JsonObject jsonObject =  new JsonObject();
        jsonObject.addProperty("unemployedCitizens", city.getPopulation()-city.getWorkingTiles().size());
        jsonObject.addProperty("ok", true);
        return jsonObject;
    }

    public JsonObject cityGetBuildings(String username) {
        Civilization civilization = civController.getCivilizationByUsername(username);
        if (civilization == null)
            return messageToJsonObj("invalid civUsername", false);
        City city = civilization.getSelectedCity();
        if ( city == null )
            return messageToJsonObj("no city selected", false);
        JsonObject response = new JsonObject();
        response.add("buildings", GSON.fromJson(GSON.toJson(city.getBuildingsInCity()), JsonArray.class));
        return setOk(response, true);
    } 
 
    public JsonObject cityPurchaseTile(String username, int tileId, boolean cheat) { 
        Civilization civilization = civController.getCivilizationByUsername(username);
        if (civilization == null)
            return messageToJsonObj("invalid civUsername", false);
        City city = civilization.getSelectedCity();
        if ( city == null )
            return messageToJsonObj("no city selected", false);        
        Tile tile = mapController.getTileById(tileId);
        if ( tile == null )
            return messageToJsonObj("invalid tileId", false);
        if( cheat == true ){
            if( cityController.cityPurchaseTile(city, tile) == false )
                return messageToJsonObj("something is invalid", false);
        }
        else{
            if( civilization.getGold() < City.GOLD_NEEDED_TO_PURCHASE_TILE )
                return messageToJsonObj("the gold isn't enough", false);
            if( cityController.cityPurchaseTile(city, tile) == false )
                return messageToJsonObj("something is invalid", false);
            civilization.addToGold(-City.GOLD_NEEDED_TO_PURCHASE_TILE);
        }
        return messageToJsonObj("Tile succesfully added to city", true);
    }

    public JsonObject cityGetCurrentProduction(String username) {
        Civilization civilization = civController.getCivilizationByUsername(username);
        if (civilization == null)
            return messageToJsonObj("invalid civUsername", false);
        City city = civilization.getSelectedCity();
        if ( city == null )
            return messageToJsonObj("no city selected", false);        
        Production production = city.getCurrentProduction();
        if( production == null )
            return messageToJsonObj("there is no production", false);
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("production", serializeProduction(production, city));
        jsonObject.addProperty("ok", true);        
        return jsonObject;
    }

    public JsonObject citySetCurrentProduction(String username, int prodId, boolean cheat) {
        Civilization civilization = civController.getCivilizationByUsername(username);
        if (civilization == null)
            return messageToJsonObj("invalid civUsername", false);
        City city = civilization.getSelectedCity();
        if ( city == null )
            return messageToJsonObj("no city selected", false);        
        Production[] allProductions = Production.getAllProductions();
        Production selectedProd = null;
        for (Production production : allProductions) {
            if (production.getId() == prodId) {
                selectedProd = production;
                break;
            }
        }
        if( cityController.cityChangeCurrentProduction(city, selectedProd, cheat) == false )
            return messageToJsonObj(Message.INVALID_REQUEST, false);
        return messageToJsonObj("current production changed successfully", true);
    }   

    public JsonObject cityBuyProduction(String username, int prodId, boolean cheat) {
        Civilization civilization = civController.getCivilizationByUsername(username);
        if (civilization == null)
            return messageToJsonObj("invalid civUsername", false);
        City city = civilization.getSelectedCity();
        if ( city == null )
            return messageToJsonObj("no city selected", false);        
        Production[] allProductions = Production.getAllProductions();
        Production production = null;
        for (Production availableProd : allProductions) {
            if (availableProd.getId() == prodId) {
                production = availableProd;
                break;
            }
        }
        if (production == null)
            return messageToJsonObj("invalid prodId", false);
        if (!cheat) {
            if (production.getCost() > civilization.getGold())
                return messageToJsonObj("not enough gold", false);
        }
        if (!cityController.cityConstructProduction(city, production, cheat))
            return messageToJsonObj(Message.INVALID_REQUEST, false);
        if (!cheat)
            civilization.addToGold(-production.getCost());
        civilization.addToMessageQueue("civilization " + civilization.getName() + " bought production " + production.getName());
        return messageToJsonObj("production successfully bought", true);
    }

    public JsonObject cityGetAllAvailableProductions(String username, boolean cheat) {
        Civilization civilization = civController.getCivilizationByUsername(username);
        if (civilization == null)
            return messageToJsonObj("invalid civUsername", false);
        City city = civilization.getSelectedCity();
        if ( city == null )
            return messageToJsonObj("no city selected", false);        
        JsonObject jsonObject = new JsonObject();
        JsonObject productionsJson = new JsonObject();
        productionsJson.add("buildings", new JsonArray());
        productionsJson.add("units", new JsonArray());
        JsonArray buildingsJsonArray = productionsJson.get("buildings").getAsJsonArray();
        JsonArray unitsJsonArray = productionsJson.get("units").getAsJsonArray();
        jsonObject.add("productions", productionsJson);
        for (Production production : Production.getAllProductions()){
            if (!cheat && !city.canProduce(production))
                continue;
            if (production instanceof Building) {
                buildingsJsonArray.add(serializeProduction(production));
            } else { 
                unitsJsonArray.add(serializeProduction(production, city)); 
            } 
        } 
        jsonObject.addProperty("ok", true);     
        return jsonObject;
    }

    public JsonObject cityGetWorkingTiles(String username) {
        Civilization civilization = civController.getCivilizationByUsername(username);
        if (civilization == null)
            return messageToJsonObj("invalid civUsername", false);
        City city = civilization.getSelectedCity();
        if ( city == null )
            return messageToJsonObj("no city selected", false);        
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("workingTiles", new JsonArray());    
        for ( Tile tile : city.getWorkingTiles() ){
            ((JsonArray) jsonObject.get("workingTiles")).add(serializeTile(tile, civilization));
        }
        jsonObject.addProperty("ok", true);
        return jsonObject;
    }

    public JsonObject cityDestroy(String username, int cityId, boolean cheat) {
        Civilization civilization = civController.getCivilizationByUsername(username);
        if (civilization == null)
            return messageToJsonObj("invalid civUsername", false);
        City city = cityController.getCityById(cityId);
        if ( city == null )
            return messageToJsonObj("no city selected", false);        
        if ( city.getCivilization() == civilization )
            return messageToJsonObj("city is invalid", false);
        if (!cheat && !city.isDead())
            return messageToJsonObj("City is not dead yet!", false);
        if ( cityController.cityDestroy(city, civilization) == false )
            return messageToJsonObj("something is invalid", false);
        civilization.addToMessageQueue("city " + city.getName() + " from civilization " + civilization.getName() + " destroyed");
        return messageToJsonObj("city destroyed successfully", true);
    }

    public JsonObject cityAnnex(String username, int cityId, boolean cheat) {
        Civilization civilization = civController.getCivilizationByUsername(username);
        if (civilization == null)
            return messageToJsonObj("invalid civUsername", false);
        City city = cityController.getCityById(cityId);
        if ( city == null )
            return messageToJsonObj("no city selected", false);
        if ( city.getCivilization() == civilization )        
            return messageToJsonObj("city is invalid", false);
        if (!cheat && !city.isDead())
            return messageToJsonObj("City is not dead yet!", false);
        if ( cityController.cityAnnex(city, civilization) == false )
            return messageToJsonObj("something is invalid", false);
        civilization.addToMessageQueue("city " + city.getName() + " from civilization " + civilization.getName() + " annexed");
        return messageToJsonObj("city Annexed successfully", true);
    }

    public JsonObject civGetLatestResearch(String username) {
        Civilization civ = civController.getCivilizationByUsername(username);
        if (civ == null)
            return messageToJsonObj(Message.INVALID_REQUEST, false);
        Technology technology = civ.getLatestResearch();
        JsonObject response = new JsonObject();
        if (technology != null)
            response.add("latestResearch", serializeTechnology(technology, civ));
        return setOk(response, true);
    }

    public JsonObject civGetAllAvailableResearches(String username, boolean cheat) {
        Civilization civ = civController.getCivilizationByUsername(username);
        if (civ == null)
            return messageToJsonObj(Message.INVALID_REQUEST, false);
        Technology[] allTechs = Technology.values();
        JsonObject response = new JsonObject();
        response.add("technologies", new JsonArray());
        for (Technology tech : allTechs) {
            if (cheat || (civ.technologyIsReachable(tech) && !civ.getTechnologyReached(tech) && civ.getCurrentResearch() != tech))
                response.get("technologies").getAsJsonArray().add(serializeTechnology(tech));
        }
        return setOk(response, true);
    }


    public JsonObject civSetCurrentResearch(String username, int techId, boolean cheat){
        Civilization civilization = civController.getCivilizationByUsername(username);
        if (civilization == null)
            return messageToJsonObj("invalid civUsername", false);
        Technology technology = Technology.getTechnologyById(techId);
        if( technology == null )
            return messageToJsonObj("invalid Technology", false);
        if (!cheat && !civilization.technologyIsReachable(technology))
            return messageToJsonObj("Technology unreachable", false);
        civilization.setCurrentResearch(technology);
        String message = "Technology is started being researched";
        if( cheat == true ){
            civilization.finishResearch();
            message = "Technology Researched successfully";
        }
        return messageToJsonObj(message, true);
    }

    public JsonObject civGetCurrentResearch(String username){
        Civilization civilization = civController.getCivilizationByUsername(username);
        if (civilization == null)
            return messageToJsonObj("invalid civUsername", false);
        Technology technology = civilization.getCurrentResearch();
        if( technology == null ){
            return messageToJsonObj("there is no thechnology researching", false);
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("technology", serializeTechnology(technology, civilization));
        jsonObject.addProperty("ok", true);
        return jsonObject;
    }

    public JsonObject getEra(){
        if( gameArea.getEra() == null ){
            return messageToJsonObj("there is no era", false);
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("era", gameArea.getEra().name());
        jsonObject.addProperty("ok", true);
        return jsonObject;
    }

    public JsonObject getYear(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("year", gameArea.getYear());
        jsonObject.addProperty("ok", true);
        return jsonObject;
    }

    public JsonObject getTurn(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("turn", gameArea.getTurn());
        jsonObject.addProperty("ok", true);
        return jsonObject;
    }

    public JsonObject canBuildImprovement(String username, int tileId, int impId){
        Civilization civ = civController.getCivilizationByUsername(username);
        if( civ == null ){
            return messageToJsonObj("invalid civUsername", false);
        }
        Tile tile = mapController.getTileById(tileId);
        if( tile == null ){
            return messageToJsonObj("tileId is invalid", false);
        }
        Improvement improvement = Improvement.getImprovementById(impId);
        if( improvement == null ){
            return messageToJsonObj("impId is invalid", false);
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("canBuild", tile.civCanBuildImprovement(civ, improvement));
        jsonObject.addProperty("ok", true);
        return jsonObject;
    }

    public JsonObject hasRoadOrRailRoad(String username, int tileId){
        Civilization civ = civController.getCivilizationByUsername(username);
        if( civ == null ){
            return messageToJsonObj("invalid civUsername", false);
        }
        Tile tile = mapController.getTileById(tileId);
        if( tile == null ){
            return messageToJsonObj("tileId is invalid", false);
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("hasRoad", (tile.getHasRoad() || tile.getHasRailRoad()) );
        jsonObject.addProperty("ok", true);
        return jsonObject;
    }

    public JsonObject getTerrainFeatureByTile(String username, int tileId){
        Civilization civ = civController.getCivilizationByUsername(username);
        if( civ == null ){
            return messageToJsonObj("invalid civUsername", false);
        }
        Tile tile = mapController.getTileById(tileId);
        if( tile == null ){
            return messageToJsonObj("tileId is invalid", false);
        }
        if( tile.getTerrainFeature() == null ){
            return messageToJsonObj("there is no terrain feature", false);
        }        
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("terrainFeature", tile.getTerrainFeature().getId());
        jsonObject.addProperty("ok", true);
        return jsonObject;
    }

    public JsonObject getTechnologyByName(String name){
        Technology technology = null;
        String name1 = name.toUpperCase();
        for(int i = 0 ; i < Technology.values().length ;i ++){
            if(Technology.values()[ i ].name().equals(name)){
                technology = Technology.values()[ i ];
                break;
            }
        }
        if( technology == null ){
            return messageToJsonObj("wronge Name", false);
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("tech", serializeTechnology(technology));
        jsonObject.addProperty("ok", true);
        return jsonObject;
    }

}
