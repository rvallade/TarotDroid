package fr.tarot.game;

import java.util.List;

/**
 * Created by Romain on 07/07/2016.
 */
public class HumanPlayerTarot extends PlayerTarot {
    public HumanPlayerTarot(int id, String name, int idNextPlayer, int placeInListeGlobale, int placeInListeOrdonnee) {
        super(id, name, idNextPlayer, placeInListeGlobale, placeInListeOrdonnee);
        human = true;
    }

    @Override
    public int takeDecision(int lastEnchere) {
        // never called for a human
        return -1;
    }

    @Override
    public CarteTarot play(PliTarot pli) {
        // never called for a human
        return null;
    }

    @Override
    public List<CarteTarot> gereEcart() {
        // never called for a human
        return null;
    }
}
