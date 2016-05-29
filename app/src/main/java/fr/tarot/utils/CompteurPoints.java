package fr.tarot.utils;

import java.util.List;

import fr.commun.game.Carte;
import fr.commun.utils.Logs;
import fr.tarot.game.CarteTarot;

public class CompteurPoints {
    int nbBouts = 0;
    List<Carte> plisRealises = null;

    public CompteurPoints(List<Carte> plisRealises) {
        this.plisRealises = plisRealises;
    }

    public float comptePointsPlis() {
        float valeurTotale = 0;
        for (int i = 0; i < plisRealises.size(); i++) {
            CarteTarot carte = (CarteTarot) plisRealises.get(i);
            valeurTotale += (carte.getValeurPoint() == 0f ? 0.5f : carte.getValeurPoint());
            if (carte.isBout()) nbBouts++;
        }
        Logs.info("Nb Bouts : " + nbBouts);
        Logs.info("Valeur des plis : " + valeurTotale);
        return valeurTotale;
    }

    public int getNbBouts() {
        return nbBouts;
    }
}
