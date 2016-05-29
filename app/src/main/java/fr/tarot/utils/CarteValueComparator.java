package fr.tarot.utils;

import java.util.Comparator;

import fr.commun.game.Carte;

/**
 * permet d'ordonner les cartes selon la valeur seule
 * Ainsi on aura tous les 1 ensembles, puis tous les 2, etc.... et enfin les atouts ranges dans l'ordre croissant
 */
public class CarteValueComparator implements Comparator<Carte> {

    public int compare(Carte carte1, Carte carte2) {
        if (carte1.getValeurAbstraite() < 0) return 1;
        if (carte2.getValeurAbstraite() < 0) return -1;
        if (carte1.getValeurAbstraite() > carte2.getValeurAbstraite()) {
            return 1;
        } else if (carte1.getValeurAbstraite() < carte2.getValeurAbstraite()) {
            return -1;
        }
        return 0;
    }

}
