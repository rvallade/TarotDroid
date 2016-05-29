package fr.tarot.game;

import org.json.JSONException;
import org.json.JSONObject;

public class Poignee {
    private int id = -1;
    private String name = null;
    private int nbPoints = 0;
    private int nbAtoutsMin = 0;

    public Poignee(int id, String name, int nbPoints, int nbAtoutsMin) {
        super();
        this.id = id;
        this.name = name;
        this.nbPoints = nbPoints;
        this.nbAtoutsMin = nbAtoutsMin;
    }

    public Poignee(JSONObject o) throws JSONException {
        id = o.getInt("id");
        name = o.getString("name");
        nbPoints = o.getInt("nbPoints");
        nbAtoutsMin = o.getInt("nbAtoutsMin");
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getNbPoints() {
        return nbPoints;
    }

    public int getNbAtoutsMin() {
        return nbAtoutsMin;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject o = new JSONObject();
        o.put("id", id);
        o.put("name", name);
        o.put("nbPoints", nbPoints);
        o.put("nbAtoutsMin", nbAtoutsMin);
        return o;
    }
}
