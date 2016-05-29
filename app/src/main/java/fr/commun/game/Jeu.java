package fr.commun.game;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.commun.utils.Utils;
import fr.tarot.utils.TarotReferentiel;

public class Jeu {
    protected List<Carte> hand = null;

    public Jeu() {
        hand = new ArrayList<Carte>();
    }

    public Jeu(JSONObject o) throws JSONException {
        JSONArray a = o.getJSONArray("hand");
        hand = new ArrayList<Carte>();
        for (int i = 0; i < o.length(); i++) {
            JSONObject obj = a.getJSONObject(i);
            hand.add(TarotReferentiel.getCarteFromMapWithID(obj.getInt("id")));
        }
    }

    public List<Carte> getHand() {
        return hand;
    }

    @Override
    public String toString() {
        StringBuilder sortie = new StringBuilder();
        int index = 0;
        for (Carte card : hand) {
            sortie.append("|");
            sortie.append(index);
            sortie.append("|");
            sortie.append(card.toShortString());
            sortie.append("   ");
            index++;
        }
        return sortie.toString();
    }

    public Carte getCard(int position) {
        return hand.get(position);
    }

    public int sizeOfGame() {
        return hand.size();
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject o = new JSONObject();
        JSONArray a = new JSONArray();
        if (!Utils.isListEmpty(hand)) {
            for (Carte carte : hand) {
                a.put(carte.toJSON());
            }

        }
        o.put("hand", a);
        return o;
    }

}
