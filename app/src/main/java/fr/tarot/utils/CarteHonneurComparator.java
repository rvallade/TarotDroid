package fr.tarot.utils;

import java.util.Comparator;

import fr.commun.game.Carte;
import fr.tarot.game.CarteTarot;

/**
 * permet d'ordonner les cartes selon les honneurs puis les cartes basses et les atouts
 * R/D/C/V/1/2/..../9/10 Puis les atouts
 * Les non atouts sont ordonnes suivant la valeur faciale et non par couleur
 */
public class CarteHonneurComparator implements Comparator<Carte>  {

	public int compare(Carte carte1, Carte carte2) {
		CarteTarot carteA = (CarteTarot) carte1;
		CarteTarot carteB = (CarteTarot) carte2;
		if (carteA.isBasse() && !carteA.isAtout()){
			// carteA basse seulement
			if (carteB.isAtout()) return -1;
			if (carteB.isBasse()){
				if (carteA.getValeurAbstraite()>carteB.getValeurAbstraite()){
					return 1;
				} else if (carteA.getValeurAbstraite()<carteB.getValeurAbstraite()){
					return -1;
				}
				return 0;
			} else {
				return 1;
			}
		} else if (carteA.isAtout()){
			// carteA atout seulement
			if (carteB.isAtout()){
				if (carteA.getValeurAbstraite()>carteB.getValeurAbstraite()){
					return 1;
				} else {
					return -1;
				}
			} else {
				return 1;
			}
		} else {
			// carteA est un honneur
			if (carteB.isAtout()) return -1;
			if (carteB.isBasse()) return -1;
			if (carteA.getValeurAbstraite()>carteB.getValeurAbstraite()){
				return -1;
			} else if (carteA.getValeurAbstraite()<carteB.getValeurAbstraite()){
				return 1;
			}
			return 0;
		}
	}

}
