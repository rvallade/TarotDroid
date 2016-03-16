package fr.commun.game;

import org.json.*;
import fr.commun.utils.CardReferentiel;

public class Carte {
	private float valeurPoint = 0.0f;
	private int valeurAbstraite = 0;
	private int idCouleur = 0; //0 par defaut pas de couleur (exemple atout)
	private String valeurFaciale = "";
	protected int resource = -1;
	private int id = -1;

	
	public Carte (float valeurPoint, int valeurAbstraite, int idCouleur, String valeurFaciale){
		this.valeurPoint = valeurPoint;
		this.valeurAbstraite = valeurAbstraite;
		this.idCouleur = idCouleur;
		this.valeurFaciale = valeurFaciale;
	}
	
	public Carte (float valeurPoint, int valeurAbstraite, int idCouleur, String valeurFaciale, int resource){
		this.valeurPoint = valeurPoint;
		this.valeurAbstraite = valeurAbstraite;
		this.idCouleur = idCouleur;
		this.valeurFaciale = valeurFaciale;
		this.resource = resource;
	}
	
	public float getValeurPoint(){
		return valeurPoint;
	}
	
	public int getValeurAbstraite(){
		return valeurAbstraite;
	}

	public int getIdCouleur() {
		return idCouleur;
	}

	public String getValeurFaciale() {
		return valeurFaciale;
	}

	public int getResource() {
		return resource;
	}
	
	public String toString() {
		StringBuilder maChaine = new StringBuilder();
		maChaine.append(valeurFaciale);
		maChaine.append(" de ");
		maChaine.append(CardReferentiel.getCouleur(idCouleur));
		maChaine.append(", valeur abstraite = ");
		maChaine.append(valeurAbstraite);
		return maChaine.toString();
	}
	
	public String toShortString() {
		StringBuilder maChaine = new StringBuilder();
		maChaine.append(valeurFaciale);
		maChaine.append(" de ");
		maChaine.append(CardReferentiel.getCouleurShort(idCouleur));
		return maChaine.toString();
	}
	
    public void setId(int id) {
        this.id = id;
    }

    public JSONObject toJSON () throws JSONException {
        JSONObject o = new JSONObject();
        o.put("id", id);
        return o;
    }
}
