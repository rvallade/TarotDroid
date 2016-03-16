package fr.tarot.utils;

import java.util.Comparator;

import fr.commun.game.Carte;

/**
 * permet d'ordonner les cartes selon la couleur et la valeur
 * Atout/coeur/trefle/carreau/pique
 * Atout<pique (suivant CardReferentiel)
 * du 1 au R et de l'excuse au 21
 * @author Romain
 *
 */
public class CarteComparator implements Comparator<Carte>  {

	public int compare(Carte carte1, Carte carte2) {
		// Atout/coeur/trefle/carreau/pique
		// Atout<pique (suivant CardReferentiel)
		// du 1 au R et de l'excuse au 21
		if (carte1.getIdCouleur()==carte2.getIdCouleur()){
			// meme couleur, pas d'egalite possible
			if (carte1.getValeurAbstraite()>carte2.getValeurAbstraite()){
				return 1;
			}
			return -1;
		} else {
			// deux couleur differentes
			if (carte1.getIdCouleur()>carte2.getIdCouleur()){
				return 1;
			}
			return -1;
		}
	}

}
