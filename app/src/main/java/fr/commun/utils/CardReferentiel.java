package fr.commun.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.commun.game.Carte;

public class CardReferentiel {
    private static List<String> listeCouleurs = null;
    private static List<String> listeCouleursShort = null;
    protected static List<Carte> game = null;

    public static String getCouleur(int idCouleur) {
        if (listeCouleurs == null) {
            listeCouleurs = new ArrayList<String>();
            listeCouleurs.add(0, "Atout");
            listeCouleurs.add(1, "Coeur");
            listeCouleurs.add(2, "Trefle");
            listeCouleurs.add(3, "Carreau");
            listeCouleurs.add(4, "Pique");
        }
        return listeCouleurs.get(idCouleur);
    }

    public static String getCouleurShort(int idCouleur) {
        if (listeCouleursShort == null) {
            listeCouleursShort = new ArrayList<String>();
            listeCouleursShort.add(0, "A");
            listeCouleursShort.add(1, "C");
            listeCouleursShort.add(2, "T");
            listeCouleursShort.add(3, "K");
            listeCouleursShort.add(4, "P");
        }
        return listeCouleursShort.get(idCouleur);
    }

    public static int getIdAtout() {
        return 0;
    }

    public static int getIdCoeur() {
        return 1;
    }

    public static int getIdTrefle() {
        return 2;
    }

    public static int getIdCarreau() {
        return 3;
    }

    public static int getIdPique() {
        return 4;
    }

    public static List<Carte> getGame() {
        return game;
    }

    public static void shuffleGame() {
        Collections.shuffle(game);
    }

    public static void createJeu() {
    }
}
