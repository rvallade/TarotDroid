package fr.tarot.game;

import fr.commun.game.Carte;

public class CarteTarot extends Carte {
	private boolean bout = false;
	private boolean atout = false;
	private boolean excuse = false;
	private boolean petit = false;
	private boolean gros = false; //21 Atout
	private boolean basse = false;

	/**
	 * Constructeur normal, cartes basses
	 * @param valeurPoint
	 * @param valeurAbstraite
	 * @param idCouleur
	 * @param valeurFaciale
	 */
	public CarteTarot(float valeurPoint, int valeurAbstraite, int idCouleur, String valeurFaciale, int resource, boolean isBasse) {
		super(valeurPoint, valeurAbstraite, idCouleur, valeurFaciale, resource);
		basse = isBasse;
	}
	
	/**
	 * Constructeur pour les Atouts
	 * @param valeurPoint
	 * @param valeurAbstraite
	 * @param idCouleur
	 * @param valeurFaciale
	 * @param isAtout
	 */
	public CarteTarot(float valeurPoint, int valeurAbstraite, int idCouleur, String valeurFaciale, boolean isAtout, int resource){
		super(valeurPoint, valeurAbstraite, idCouleur, valeurFaciale, resource);
		basse = true;
		if ("1".equals(valeurFaciale)){
			petit = true;
			bout = true;
			basse = false;
		}
		if ("21".equals(valeurFaciale)){
			gros = true;
			bout = true;
			basse = false;
		}
		atout = isAtout;
	}
	
	/**
	 * Constructeur pour l'excuse
	 * @param valeurPoint
	 * @param valeurAbstraite
	 * @param idCouleur
	 * @param valeurFaciale
	 * @param isAtout
	 * @param isExcuse
	 */
	public CarteTarot(float valeurPoint, int valeurAbstraite, int idCouleur, String valeurFaciale, boolean isAtout, boolean isExcuse, int resource){
		super(valeurPoint, valeurAbstraite, idCouleur, valeurFaciale);
		atout = isAtout;
		bout = true;
		excuse = isExcuse;
		this.resource = resource;
	}

	public boolean isBout() {
		return bout;
	}

	public boolean isAtout() {
		return atout;
	}

	public boolean isExcuse() {
		return excuse;
	}

	public boolean isPetit() {
		return petit;
	}

	public boolean is21() {
		return gros;
	}

	public boolean isBasse() {
		return basse;
	}
}
