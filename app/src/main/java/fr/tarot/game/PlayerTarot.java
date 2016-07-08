package fr.tarot.game;

import android.app.Activity;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.commun.game.Carte;
import fr.commun.game.Player;
import fr.commun.utils.Logs;
import fr.tarot.utils.TarotReferentiel;

public abstract class PlayerTarot extends Player {
    protected JeuTarot jeu = null;
    protected boolean preneur;
    protected int scoreJeu = 0;
    // par defaut il passe
    protected int enchere = TarotReferentiel.getIdPasse();
    protected int placeInListeGlobale = -1;
    protected int placeInListeOrdonnee = -1;
    protected double pointsRealises = 0;
    protected boolean premierTour = true;
    protected int poignee = 0;

    protected ImageView carteJoueeIV = null;
    protected TextView nameJoueurTV = null;
    protected CarteTarot lastCardPlayed = null;

    public PlayerTarot(int id, String name, int idNextPlayer, int placeInListeGlobale, int placeInListeOrdonnee) {
        super(id, name);
        this.idNextPlayer = idNextPlayer;
        this.placeInListeGlobale = placeInListeGlobale;
        this.placeInListeOrdonnee = placeInListeOrdonnee;
        jeu = new JeuTarot();
        lastCardPlayed = null;
    }

    public void initFromJson(JSONObject o, Activity activity) throws JSONException {
        super.initFromJson(o);
        jeu = new JeuTarot(o.getJSONObject("jeu"));
        preneur = o.getBoolean("preneur");
        scoreJeu = o.getInt("scoreJeu");
        enchere = o.getInt("enchere");
        placeInListeGlobale = o.getInt("placeInListeGlobale");
        placeInListeOrdonnee = o.getInt("placeInListeOrdonnee");
        pointsRealises = o.getDouble("pointsRealises");
        premierTour = o.getBoolean("premierTour");
        poignee = o.getInt("poignee");
        carteJoueeIV.setImageResource(Integer.parseInt(o.getString("carteJoueeIVImage")));
        nameJoueurTV.setText(name);
    }

    public void reInit() {
        preneur = false;
        scoreJeu = 0;
        // par defaut il passe
        enchere = TarotReferentiel.getIdPasse();
        donneur = false;
        jeu = new JeuTarot();
        premierTour = true;
        poignee = 0;
        if (nameJoueurTV != null) {
            nameJoueurTV.setText(getName());
        }
        lastCardPlayed = null;
    }

    public void addCartes(List<Carte> cartes) {
        jeu.addAll(cartes);
    }

    public boolean isPreneur() {
        return preneur;
    }

    public void setPreneur(boolean preneur) {
        this.preneur = preneur;
    }

    public abstract int takeDecision(int lastEnchere);

    public boolean hasPoignee() {
        return getPoigneeType() != 0;
    }
    public int getPoigneeType() {
        // presentation des poignees ou du petit sec
        if (jeu.getNbAtouts() >= 15) {
            // triple poignee
            poignee = 3;
        } else if (jeu.getNbAtouts() >= 13) {
            // double poignee
            poignee = 2;
        } else if (jeu.getNbAtouts() >= 10) {
            // simple poignee
            poignee = 1;
        }
        return poignee;
    }

    public List<Carte> getCartesPoignee() {
        List<Carte> cartesPoignee = new ArrayList<Carte>();
        int nbAtouts = 0;
        int index = 0;
        switch (getPoigneeType()) {
            case 1:
                nbAtouts = 10;
                break;
            case 2:
                nbAtouts = 13;
                break;
            case 3:
                nbAtouts = 15;
                break;
        }
        while (index != nbAtouts) {
            cartesPoignee.add(jeu.getJeuAtout().get(jeu.getJeuAtout().size() - 1 - index));
            index++;
        }
        Collections.reverse(cartesPoignee);
        return cartesPoignee;
    }

    // TODO Ne pas permettre au joueur de commencer par le petit
    // TODO Si l'excuse a ete jouee en premier alors le joueur peut jouer ce qu'il veut
    // si le joueur est le premier il doit choisir la couleur qu'il veut jouer
    // TODO voir si on ne peut pas travailler avec la carte r�cup�r�e au d�but et essayer de mutualiser le code.....
    public abstract CarteTarot play(PliTarot pli);

    public void playCard(PliTarot pli, CarteTarot carte) {
        // on ajoute la carte au pli
        pli.playCard(carte, this);
        Logs.info(getName() + " joue le " + carte.toString());
        played = true;
        jeu.remove(carte);
    }

    /**
     * Ajoute les points de la partie en cours
     *
     * @param points
     */
    public void addPoints(double points) {
        pointsRealises += points;
    }

    // TODO A pr�voir : ne pr�senter que les cartes qui peuvent �tre jouees, dans une version graphique, griser les cartes qui ne peuvent pas �tre jouees?

    /**
     * M�thode qui va tester la carte jouee par l'humain
     * Se r�f�rer au fichier Models.pptx dans le r�pertoire "docs" pour trouver les sch�mas
     *
     * @param carte
     * @param pli
     * @return
     */
    public boolean testCarte(CarteTarot carte, PliTarot pli) {
        // l'excuse peut etre jouee a tout moment
        if (carte.isExcuse()) return true;
        // si il n'y a pas de couleur en cours, on peut jouer ce qu'on veut
        if (pli.getIdCouleurDemandee() == -1) return true;
        if (carte.getIdCouleur() != pli.getIdCouleurDemandee()) {
            if (jeu.getJeuCouleur(pli.getIdCouleurDemandee()).size() > 0) {
                // il lui reste de la couleur il doit la jouer.
                return false;
            } else {
                // il n'a pas de la couleur demandee
                if (pli.getIdCouleurDemandee() != TarotReferentiel.getIdAtout()) {
                    // pour un pli non atouts
                    if (carte.getIdCouleur() != TarotReferentiel.getIdAtout()) {
                        // il pisse...
                        if (jeu.getNbAtouts() > 0
                                && !(jeu.getNbAtouts() == 1
                                && jeu.getExcuse() != null))
                            return false;
                        GameDatas.getGameDatas().setPlayerHasNoMoreAtouts(idPlayer);
                        if (preneur) {
                            GameDatas.getGameDatas().preneurHasNoMoreAtouts();
                        }
                    } else {
                        // il coupe
                        return testAtout(carte, pli);
                    }
                }
            }
        } else {
            if (preneur) {
                GameDatas.getGameDatas().getCoupesPreneur()[pli.getIdCouleurDemandee()] = false;
            }
            if (pli.getIdCouleurDemandee() == TarotReferentiel.getIdAtout()) {
                return testAtout(carte, pli);
            }
        }
        return true;
    }

    /**
     * Methode qui va tester si l'atout joue est valable :
     * _ est-ce qu'il est plus fort que le plus fort du pli?
     * _ dans le cas contraire est-ce qu'il dispose d'un atout plus fort que le plus fort du pli?
     *
     * @param carte
     * @param pli
     * @return
     */
    private boolean testAtout(CarteTarot carte, PliTarot pli) {
        CarteTarot bestAtout = jeu.getJeuAtout().get(jeu.getNbAtouts() - 1);
        if (carte.getValeurAbstraite() < pli.getValueMax()
                && pli.getValueMax() < bestAtout.getValeurAbstraite()) {
            return false;
        }
        GameDatas.getGameDatas().setCoupe(idPlayer, pli.getIdCouleurDemandee());
        if (preneur) {
            GameDatas.getGameDatas().getCoupesPreneur()[pli.getIdCouleurDemandee()] = true;
        }
        return true;
    }

    public abstract List<CarteTarot> gereEcart();

    public int getPlaceInListeGlobale() {
        return placeInListeGlobale;
    }

    public void setPlaceInListeGlobale(int placeInListeGlobale) {
        this.placeInListeGlobale = placeInListeGlobale;
    }

    public int getPlaceInListeOrdonnee() {
        return placeInListeOrdonnee;
    }

    /**
     * Renvoit l'id de l'ench�re
     *
     * @return
     */
    public int getEnchere() {
        return enchere;
    }

    public int getPoignee() {
        return poignee;
    }

    public boolean hasNoMoreCard() {
        return jeu.getSizeOfGame() == 0;
    }

    public int getSizeOfGame() {
        return jeu.getSizeOfGame();
    }

    public JeuTarot getJeu() {
        return jeu;
    }

    @Override
    public String toString() {
        StringBuilder maChaine = new StringBuilder();
        maChaine.append(getName());
        maChaine.append(", ");
        maChaine.append(jeu.getSizeOfGame());
        maChaine.append(" cartes.");
        return maChaine.toString();
    }

    public ImageView getCarteJoueeIV() {
        return carteJoueeIV;
    }

    public void setCarteJoueeIV(ImageView carteJoueeIV) {
        this.carteJoueeIV = carteJoueeIV;
    }

    public TextView getNameJoueurTV() {
        return nameJoueurTV;
    }

    public void setNameJoueurTV(TextView nameJoueurTV) {
        this.nameJoueurTV = nameJoueurTV;
    }

    public double getPointsRealises() {
        return pointsRealises;
    }

    public void setEnchere(int enchere) {
        this.enchere = enchere;
    }

    public void setPointsRealises(double pointsRealises) {
        this.pointsRealises = pointsRealises;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject o = super.toJSON();
        o.put("jeu", jeu.toJSON());
        o.put("preneur", preneur);
        o.put("scoreJeu", scoreJeu);
        o.put("enchere", enchere);
        o.put("placeInListeGlobale", placeInListeGlobale);
        o.put("placeInListeOrdonnee", placeInListeOrdonnee);
        o.put("pointsRealises", pointsRealises);
        o.put("premierTour", premierTour);
        o.put("poignee", poignee);
        o.put("carteJoueeIV", carteJoueeIV.getId());
        o.put("carteJoueeIVImage", carteJoueeIV.getTag().toString());
        o.put("nameJoueurTV", nameJoueurTV.getId());
        return o;
    }

    public CarteTarot getLastCardPlayed() {
        return lastCardPlayed;
    }
}
