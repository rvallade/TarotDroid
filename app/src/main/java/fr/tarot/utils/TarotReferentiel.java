package fr.tarot.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.res.Resources;
import android.util.SparseArray;
import fr.commun.game.Carte;
import fr.commun.utils.CardReferentiel;
import fr.tarot.game.CarteTarot;
import fr.tarot.game.Contrat;
import fr.tarot.game.Poignee;

public class TarotReferentiel extends CardReferentiel {
	private static List<String> listeJoueurs = null;
	private static SparseArray<Integer> listePointsToDo = null;
	private static SparseArray<Contrat> listeContrats = null;
	private static SparseArray<Poignee> listePoignees = null;
	private static SparseArray<CarteTarot> mapCartes = null;

	public static String getJoueur(int idJoueur) {
		if (listeJoueurs == null) {
			listeJoueurs = new ArrayList<String>();
			listeJoueurs.add(0, "Mireille");
			listeJoueurs.add(1, "Lise");
			listeJoueurs.add(2, "Joueur");
			listeJoueurs.add(3, "Simon");
		}
		return listeJoueurs.get(idJoueur);
	}

	public static Contrat getContrat(int idContrat) {
		if (listeContrats == null) {
			listeContrats = new SparseArray<Contrat>();
			listeContrats.put(-1, new Contrat(-1,"Passe", "", 0));
			listeContrats.put(0, new Contrat(0,"Prise", "P", 1));
			listeContrats.put(1, new Contrat(1,"Garde", "G", 2));
			listeContrats.put(2, new Contrat(2,"Garde Sans", "GS", 4));
			listeContrats.put(3, new Contrat(3,"Garde Contre", "GC", 6));
		}
		return listeContrats.get(idContrat);
	}
    public static Contrat getContrat(String nameContrat) {
        if ("Passe".equalsIgnoreCase(nameContrat)) return getContrat(-1);
        if ("Prise".equalsIgnoreCase(nameContrat)) return getContrat(0);
        if ("Garde".equalsIgnoreCase(nameContrat)) return getContrat(1);
        if ("Garde Sans".equalsIgnoreCase(nameContrat)) return getContrat(2);
        if ("Garde Contre".equalsIgnoreCase(nameContrat)) return getContrat(3);
        return null;
    }
	public static int getIdPasse(){
		return -1;
	}
	public static int getIdPrise(){
		return 0;
	}
	public static int getIdGarde(){
		return 1;
	}
	public static int getIdGardeSans(){
		return 2;
	}
	public static int getIdGardeContre(){
		return 3;
	}
	
	public static int getPointsToDo(int idContrat) {
		if (listePointsToDo == null) {
			listePointsToDo = new SparseArray<Integer>();
			listePointsToDo.put(0, 56);
			listePointsToDo.put(1, 51);
			listePointsToDo.put(2, 46);
			listePointsToDo.put(3, 36);
		}
		return listePointsToDo.get(idContrat);
	}

	public static int getPointsPoignees(int idPoignee) {
		if (idPoignee==0 || idPoignee>3) return 0;
	    return getPoignee(idPoignee).getNbPoints();
	}
    public static Poignee getPoignee(int idPoignee) {
        if (listePoignees == null) {
            listePoignees = new SparseArray<Poignee>();
            listePoignees.put(1, new Poignee(1,"Simple", 20, 10));
            listePoignees.put(2, new Poignee(2,"Double", 30, 13));
            listePoignees.put(3, new Poignee(3,"Triple", 40, 15));
        }
        return listePoignees.get(idPoignee);
    }

	public static void createJeu(Resources resources, String packageName) {
		game = new ArrayList<Carte>();
		mapCartes = new SparseArray<CarteTarot>();
		int id = 0;
		CarteTarot carte;
		for (int i=1; i<=4; i++) {
			// pour toutes les couleurs
			for (int y = 1;y<11;y++) {
				carte = new CarteTarot(0f, y, i, Integer.toString(y), resources.getIdentifier("carte_"+y+getCouleurShort(i).toLowerCase(), "drawable", packageName), true);
			    game.add(carte);
			}
			carte = new CarteTarot(1.5f, 11, i, "V", resources.getIdentifier("carte_v"+getCouleurShort(i).toLowerCase(), "drawable", packageName), false);
			addCarteToBothMapandGame(carte, id++);
			carte = new CarteTarot(2.5f, 12, i, "C", resources.getIdentifier("carte_c"+getCouleurShort(i).toLowerCase(), "drawable", packageName), false);
			addCarteToBothMapandGame(carte, id++);
            carte = new CarteTarot(3.5f, 13, i, "D", resources.getIdentifier("carte_d"+getCouleurShort(i).toLowerCase(), "drawable", packageName), false);
            addCarteToBothMapandGame(carte, id++);
            carte = new CarteTarot(4.5f, 14, i, "R", resources.getIdentifier("carte_r"+getCouleurShort(i).toLowerCase(), "drawable", packageName), false);
            addCarteToBothMapandGame(carte, id++);
		}
		// pour tous les atouts
		for (int y = 1;y<22;y++) {
			if(y==1 || y==21){
				carte = new CarteTarot(4.5f, y+14, 0, Integer.toString(y), true, resources.getIdentifier("carte_"+y, "drawable", packageName));
				addCarteToBothMapandGame(carte, id++);
			} else {
			    carte = new CarteTarot(0f, y+14, 0, Integer.toString(y), true, resources.getIdentifier("carte_"+y, "drawable", packageName));
			    addCarteToBothMapandGame(carte, id++);
			}
		}
		carte = new CarteTarot(4.5f, -1, 0, "Excuse", true, true, resources.getIdentifier("carte_e", "drawable", packageName));
		addCarteToBothMapandGame(carte, id++);
	}
	
	private static void addCarteToBothMapandGame(CarteTarot carte, int id) {
	    game.add(carte);
	    mapCartes.put(id, carte);
	    carte.setId(id);
	}
	
	public static int getValeurAbstraitePetit(){
		return 15;
	}
	
	public static CarteTarot getCarteFromMaoWithID(int id) {
	    return mapCartes.get(id);
	}
}
