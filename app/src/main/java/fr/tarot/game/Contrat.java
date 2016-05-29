package fr.tarot.game;

public enum Contrat {
    PASSE(-1, 0, "Passe", ""),
    PRISE(0, 1, "Prise", "P"),
    GARDE(1, 2, "Garde", "G"),
    GARDE_SANS(2, 4, "Garde Sans", "GS"),
    GARDE_COMTRE(3, 6, "Garde Contre", "GC");

    private int id;
    private int multiplicateur;
    private String name;
    private String shortName;

    Contrat(int id, int multiplicateur, String name, String shortName) {
        this.id = id;
        this.multiplicateur = multiplicateur;
        this.name = name;
        this.shortName = shortName;
    }

    public int getMultiplicateur() {
        return multiplicateur;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public int getId() {
        return id;
    }

/*public Contrat(JSONObject o) throws JSONException {
        return getContrat(o.getInt("contratType"));
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject o = new JSONObject();
        o.put("contratType", this.getMultiplicateur());
        return o;
    }*/
}
