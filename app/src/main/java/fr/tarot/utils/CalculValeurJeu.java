package fr.tarot.utils;

import fr.tarot.game.CarteTarot;
import fr.tarot.game.JeuTarot;

public class CalculValeurJeu {

	/**
	 * Methode qui va calculer le score du jeu en vue d'un contrat type Prise ou Garde.
	 * regles de NC pour evaluer le jeu (voir docs/Evaluer le jeu de tarot.pdf)
	 * @param 
	 * @return int
	 */
	public static int getValeurJeu(JeuTarot jeu){
		int valeur = 0;
		// les bouts
		if (jeu.get21() != null) valeur+=10;
		if (jeu.getExcuse() != null) valeur+=8;
		// pour le petit ca depend de sa position. Points 5, 7 ou 9
		if (jeu.getPetit() != null){
			if (jeu.getNbAtouts()>=5) valeur+=5;
			if (jeu.getNbAtouts()>=6) valeur+=2;
			if (jeu.getNbAtouts()>=7) valeur+=2;
		}
		// les longues 5, 7 ou 9 points
		valeur +=5*jeu.getLongue5();
		valeur +=7*jeu.getLongue6();
		valeur +=9*jeu.getLongue7();
		
		// pour chaque atout si plus de 4
		valeur +=2*(jeu.getNbAtouts()>4?jeu.getNbAtouts():0);
		// les atouts majeurs
		valeur +=2*jeu.getNbAtoutsMajeurs();
		// les suites d'atouts majeurs, 1 par atout dans la suite
		int previous = 0;
		boolean firstPaire = true;
		for (CarteTarot carte:jeu.getJeuAtoutsMajeurs()){
			if (carte.getValeurAbstraite() == previous+1){
				valeur+=(firstPaire?2:1);
				firstPaire = false;
			} else {
				firstPaire = true;
			}
			previous = carte.getValeurAbstraite();
		}
		// 10 par mariage R/D
		valeur+=10*jeu.getCouplesRoiDame();
		// 6 par roi seul
		valeur+=6*jeu.getNbRoiSeuls();
		// 3 par dame seule
		valeur+=3*jeu.getNbDameSeules();
		// 2 par cavaliers
		valeur+=2*jeu.getNbCavalier();
		// 1 par valets
		valeur+=jeu.getNbValet();
		return valeur;
	}
	
	/**
	 * Methode qui va calculer le score du jeu en vue d'un contrat type Garde Sans ou Contre (on tient compte des coupes et singlettes).
	 * r�gles de NC pour �valuer le jeu (voir docs/Evaluer le jeu de tarot.pdf)
	 * @param JeuTarot
	 * @return int
	 */
	public static int getValeurJeuForGSOrGC(JeuTarot jeu){
		int valeur = 0;
		valeur = getValeurJeu(jeu);
		// une coupe 6 points
		valeur+=6*jeu.getCoupes();
		
		//une singlette 3 points
		valeur+=3*jeu.getCoupes();
		

		return valeur;
	}
}
