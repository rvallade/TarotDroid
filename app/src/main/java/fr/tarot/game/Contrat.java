package fr.tarot.game;

import org.json.JSONException;
import org.json.JSONObject;

public class Contrat {
	private int id = 0;
	private String name = null;
	private String shortName = null;
    private int multiplicateur = 0;
	
	public Contrat(int id, String name, String shortName, int multiplicateur) {
		this.id = id;
		this.name = name;
		this.shortName = shortName;
		this.multiplicateur = multiplicateur;
	}
	
	public Contrat(JSONObject o) throws JSONException {
	    id = o.getInt("id");
	    name = o.getString("name");
	    shortName = o.getString("shortName");
	    multiplicateur = o.getInt("multiplicateur");
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getMultiplicateur() {
		return multiplicateur;
	}

    public String getShortName() {
        return shortName;
    }
	
    public JSONObject toJSON() throws JSONException {
        JSONObject o = new JSONObject();
        o.put("id", id);
        o.put("name", (name != null? name : ""));
        o.put("shortName", (shortName != null? shortName : ""));
        o.put("multiplicateur", multiplicateur);
        return o;
    }
}
