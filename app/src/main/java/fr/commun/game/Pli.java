package fr.commun.game;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.commun.utils.Utils;
import fr.tarot.utils.TarotReferentiel;

public class Pli {
    protected List<Carte> cards = null;
    protected int valueMax = -1;
    protected int idVainqueur = -1;

    public Pli() {
        cards = new ArrayList<Carte>();
    }

    public Pli(JSONObject o) throws JSONException {
        valueMax = o.getInt("valueMax");
        idVainqueur = o.getInt("idVainqueur");
        cards = new ArrayList<Carte>();
        JSONArray a = o.getJSONArray("cards");
        for (int i = 0; i < a.length(); i++) {
            JSONObject obj = a.getJSONObject(i);
            cards.add(TarotReferentiel.getCarteFromMapWithID(obj.getInt("id")));
        }
    }

    public List<Carte> getCards() {
        return cards;
    }

    public void setCards(List<Carte> cards) {
        this.cards = cards;
    }

    public int getVainqueurPli() {
        return idVainqueur;
    }

    public int getValueMax() {
        return valueMax;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject o = new JSONObject();
        JSONArray a = new JSONArray();
        o.put("valueMax", valueMax);
        o.put("idVainqueur", idVainqueur);
        if (!Utils.isListEmpty(cards)) {
            for (Carte carte : cards) {
                a.put(carte.toJSON());
            }
        }
        o.put("cards", a);
        return o;
    }
}
