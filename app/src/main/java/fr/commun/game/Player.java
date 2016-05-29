package fr.commun.game;

import org.json.JSONException;
import org.json.JSONObject;


public abstract class Player {
    protected int idPlayer = -1;
    protected String name;
    protected boolean donneur = false;
    protected boolean human = false;
    protected boolean played = false;
    protected int idNextPlayer = -1;

    public Player(int id, String name) {
        this.idPlayer = id;
        this.name = name;
    }

    public void initFromJson(JSONObject o) throws JSONException {
        donneur = o.getBoolean("donneur");
        played = o.getBoolean("played");
        idNextPlayer = o.getInt("idNextPlayer");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIdPlayer() {
        return idPlayer;
    }

    public boolean isDonneur() {
        return donneur;
    }

    public void setDonneur(boolean donneur) {
        this.donneur = donneur;
    }

    public abstract String toString();

    public boolean hasPlayed() {
        return played;
    }

    public boolean isHuman() {
        return human;
    }

    public void setHuman(boolean human) {
        this.human = human;
    }

    public int getIdNextPlayer() {
        return idNextPlayer;
    }

    public void setIdNextPlayer(int idNextPlayer) {
        this.idNextPlayer = idNextPlayer;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject o = new JSONObject();
        o.put("donneur", donneur);
        o.put("played", played);
        o.put("idNextPlayer", idNextPlayer);
        return o;
    }
}
