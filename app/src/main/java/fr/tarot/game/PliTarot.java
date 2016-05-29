package fr.tarot.game;

import org.json.JSONException;
import org.json.JSONObject;

import fr.commun.game.Pli;
import fr.tarot.utils.TarotReferentiel;

/**
 * Gestion du Pli dans le Jeu de Tarot
 * Quelques subtilites comme par exemple l'impossibilite d'avoir des ex aequo
 *
 * @author Romain
 */
public class PliTarot extends Pli {
    private int idCouleurDemandee = -1;
    private float valeur = 0f;
    private boolean preneurPlayed = false;
    private boolean preneurMaitre = false;
    private int whoPlayedExcuse = -1;
    private boolean excusePlayed = false;
    private boolean vingtEtUnPlayed = false;
    private boolean petitPlayed = false;
    private boolean petitMaitre = false;

    public PliTarot() {
        super();
    }

    public PliTarot(JSONObject o) throws JSONException {
        super(o);
        idCouleurDemandee = o.getInt("idCouleurDemandee");
        valeur = (float) o.getDouble("valeur");
        preneurPlayed = o.getBoolean("preneurPlayed");
        preneurMaitre = o.getBoolean("preneurMaitre");
        whoPlayedExcuse = o.getInt("whoPlayedExcuse");
        excusePlayed = o.getBoolean("excusePlayed");
        vingtEtUnPlayed = o.getBoolean("vingtEtUnPlayed");
        petitPlayed = o.getBoolean("petitPlayed");
        petitMaitre = o.getBoolean("petitMaitre");
    }

    public void playCard(CarteTarot carte, PlayerTarot player) {
        petitMaitre = false;
        // on ajoute la carte au pli et on regarde si la carte remporte le pli
        cards.add(carte);
        valeur += (carte.getValeurPoint() == 0f ? 0.5f : carte.getValeurPoint());
        if (valueMax != -1) {
            // ce n'est pas la premiere carte du pli
            // elle remporte ssi sa valeur est plus forte que la valueMax,
            // mais il faut que ce qoit une carte de la couleur demandee ou un atout
            if (valueMax < carte.getValeurAbstraite()
                    && (idCouleurDemandee == carte.getIdCouleur() || carte.getIdCouleur() == TarotReferentiel.getIdAtout())) {
                // la carte en cours gagne
                valueMax = carte.getValeurAbstraite();
                idVainqueur = player.getIdPlayer();
                preneurMaitre = player.isPreneur();
            }
            // on initialise la couleur demandee pour le cas ou la premiere carte etait l'excuse
            if (idCouleurDemandee == -1) idCouleurDemandee = carte.getIdCouleur();
        } else {
            // premiere carte du pli la valeur max est donc celle de la carte en cours,
            // le vainqueur est donc le joueur passe en parametre
            // la couleur demandee est initialisee si la carte n'est pas l'excuse
            valueMax = carte.getValeurAbstraite();
            idVainqueur = player.getIdPlayer();
            if (!carte.isExcuse()) {
                idCouleurDemandee = carte.getIdCouleur();
                GameDatas.getGameDatas().addTourACouleur(idCouleurDemandee);
            }
            if (player.isPreneur()) {
                preneurMaitre = true;
            }
        }
        if (valueMax == TarotReferentiel.getValeurAbstraitePetit()) {
            petitMaitre = true;
        }
        if (carte.isExcuse()) {
            excusePlayed = true;
            whoPlayedExcuse = player.getIdPlayer();
        }
        if (carte.isPetit()) {
            petitPlayed = true;
        }
        if (carte.is21()) {
            vingtEtUnPlayed = true;
        }
        if (player.isPreneur()) preneurPlayed = true;
        GameDatas.getGameDatas().addCarte(carte);
    }

    public float getRapportValueSurNbCartes() {
        return (valeur / getCards().size());
    }

    public boolean testCarte(int value) {
        return value > valueMax;
    }

    public int getIdCouleurDemandee() {
        return idCouleurDemandee;
    }


	/*public float getValeur() {
        return valeur;
	}*/

    public boolean isPreneurPlayed() {
        return preneurPlayed;
    }

    public int getWhoPlayedExcuse() {
        return whoPlayedExcuse;
    }

    public boolean isExcusePlayed() {
        return excusePlayed;
    }

    public boolean isPetitPlayed() {
        return petitPlayed;
    }

    public boolean isPetitMaitre() {
        return petitMaitre;
    }

    public boolean isPreneurMaitre() {
        return preneurMaitre;
    }

    public boolean isVingtEtUnPlayed() {
        return vingtEtUnPlayed;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject o = super.toJSON();
        o.put("idCouleurDemandee", idCouleurDemandee);
        o.put("valeur", valeur);
        o.put("preneurPlayed", preneurPlayed);
        o.put("preneurMaitre", preneurMaitre);
        o.put("whoPlayedExcuse", whoPlayedExcuse);
        o.put("excusePlayed", excusePlayed);
        o.put("vingtEtUnPlayed", vingtEtUnPlayed);
        o.put("petitPlayed", petitPlayed);
        o.put("petitMaitre", petitMaitre);
        return o;
    }
}
