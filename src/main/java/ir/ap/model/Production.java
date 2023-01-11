package ir.ap.model;

public abstract interface Production {

    public static Production[] getAllProductions() {
        Production[] buildings = Building.values();
        Production[] units = UnitType.values();
        int blen = buildings.length, ulen = units.length;
        Production[] productions = new Production[blen + ulen];
        for (int i = 0; i < blen + ulen; i++) {
            if (i < blen)
                productions[i] = buildings[i];
            else
                productions[i] = units[i - blen];
        }
        return productions;
    }

    public abstract int getId();

    public abstract int getCost();

    public abstract String getName();

    public Technology getTechnologyRequired();
}
