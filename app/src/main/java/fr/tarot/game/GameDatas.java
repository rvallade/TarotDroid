package fr.tarot.game;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.commun.game.Carte;
import fr.commun.utils.Utils;
import fr.tarot.utils.TarotReferentiel;

public class GameDatas {
    private int[] nbToursCouleur = {0, 0, 0, 0, 0};
    private int[] nbCouleursPlayed = {0, 0, 0, 0, 0};
    private int[] nbCarteMaitre = {35, 14, 14, 14, 14};
    private List<boolean[]> listCoupesJoueurs;
    private float[] nbPointsRestantsDansCouleur = {0f, 12f, 12f, 12f, 12f};
    private boolean[] coupesPreneur = {true, true, true, true, true};
    private boolean[] coupesInit = {false, false, false, false, false};
    private boolean petitPlayed = false;
    private static GameDatas game = null;
    private int valueMax = -1;
    private boolean preneurHasAtouts = true;

    private GameDatas() {
        listCoupesJoueurs = new ArrayList<boolean[]>();
        listCoupesJoueurs.add(0, coupesInit);
        listCoupesJoueurs.add(1, coupesInit);
        listCoupesJoueurs.add(2, coupesInit);
        listCoupesJoueurs.add(3, coupesInit);
        valueMax = 35;
    }

    public static GameDatas getGameDatas() {
        if (game == null)
            game = new GameDatas();
        return game;
    }

    public static void initGameDatasWithJSON(JSONObject o) throws JSONException {
        if (game == null)
            game = new GameDatas();
        game.setPetitPlayed(o.getBoolean("petitPlayed"));
        game.setValueMax(o.getInt("valueMax"));
        game.setPreneurHasAtouts(o.getBoolean("preneurHasAtouts"));
        {
            JSONArray a = o.getJSONArray("nbToursCouleur");
            int[] nbToursCouleurTemp = {0, 0, 0, 0, 0};
            for (int i = 0; i < a.length(); i++) {
                nbToursCouleurTemp[i] = a.getInt(i);
            }
            game.setNbToursCouleur(nbToursCouleurTemp);
        }
        {
            JSONArray a1 = o.getJSONArray("nbCouleursPlayed");
            int[] nbCouleursPlayedTemp = {0, 0, 0, 0, 0};
            for (int i = 0; i < a1.length(); i++) {
                nbCouleursPlayedTemp[i] = a1.getInt(i);
            }
            game.setNbCouleursPlayed(nbCouleursPlayedTemp);
        }
        {
            JSONArray a2 = o.getJSONArray("nbCarteMaitre");
            int[] nbCarteMaitreTemp = {35, 14, 14, 14, 14};
            for (int i = 0; i < a2.length(); i++) {
                nbCarteMaitreTemp[i] = a2.getInt(i);
            }
            game.setNbCarteMaitre(nbCarteMaitreTemp);
        }
        {
            JSONArray a3 = o.getJSONArray("nbPointsRestantsDansCouleur");
            float[] nbPointsRestantsDansCouleurTemp = {0f, 12f, 12f, 12f, 12f};
            for (int i = 0; i < a3.length(); i++) {
                nbPointsRestantsDansCouleurTemp[i] = (float) a3.getDouble(i);
            }
            game.setNbPointsRestantsDansCouleur(nbPointsRestantsDansCouleurTemp);
        }
        {
            JSONArray a4 = o.getJSONArray("coupesPreneur");
            boolean[] coupesPreneurTemp = {true, true, true, true, true};
            for (int i = 0; i < a4.length(); i++) {
                coupesPreneurTemp[i] = a4.getBoolean(i);
            }
            game.setCoupesPreneur(coupesPreneurTemp);
        }
        {
            JSONArray a5 = o.getJSONArray("coupesInit");
            boolean[] coupesInitTemp = {true, true, true, true, true};
            for (int i = 0; i < a5.length(); i++) {
                coupesInitTemp[i] = a5.getBoolean(i);
            }
            game.setCoupesInit(coupesInitTemp);
        }
        {
            List<boolean[]> listCoupesJoueursTemp = new ArrayList<boolean[]>();
            JSONArray a6 = o.getJSONArray("listCoupesJoueurs");
            for (int i = 0; i < a6.length(); i++) {
                boolean[] initCoupesJoueurs = {false, false, false, false, false};
                JSONObject o2 = a6.getJSONObject(i);
                JSONArray a7 = o2.getJSONArray("coupeJoueur");
                for (int j = 0; j < a7.length(); j++) {
                    JSONObject o3 = a7.getJSONObject(j);
                    initCoupesJoueurs[j] = o3.getBoolean("value");
                }
                listCoupesJoueursTemp.add(i, initCoupesJoueurs);
            }
            game.setListCoupesJoueurs(listCoupesJoueursTemp);
        }
    }

    public int getNbAtoutRestant() {
        return 21 - getNbAtoutPlayed();
    }

    public void addCarte(CarteTarot carte) {
        nbCouleursPlayed[carte.getIdCouleur()]++;
        if (carte.getValeurPoint() > 0)
            nbPointsRestantsDansCouleur[carte.getIdCouleur()] -= carte.getValeurPoint();
        if (carte.isPetit())
            petitPlayed = true;
        if (carte.isAtout() && carte.getValeurAbstraite() == valueMax)
            valueMax--;
    }

    /**
     * Methode qui gere les fin de pli et notamment les comptes sur les cartes
     * maitresses
     *
     * @param pli
     */
    public void finDePli(PliTarot pli) {
        for (Carte carte : pli.getCards()) {
            if (carte.getValeurAbstraite() == nbCarteMaitre[carte.getIdCouleur()])
                nbCarteMaitre[carte.getIdCouleur()]--;
        }
    }

    public void addTourACouleur(int idCouleur) {
        nbToursCouleur[idCouleur]++;
    }

    public int getNbTourACouleur(int idCouleur) {
        return nbToursCouleur[idCouleur];
    }

    public static void reInitGameDatas() {
        game = new GameDatas();
    }

    public int getNbAtoutPlayed() {
        return nbCouleursPlayed[TarotReferentiel.getIdAtout()];
    }

    public int getNbCoeurPlayed() {
        return nbCouleursPlayed[TarotReferentiel.getIdCoeur()];
    }

    public int getNbTreflePlayed() {
        return nbCouleursPlayed[TarotReferentiel.getIdTrefle()];
    }

    public int getNbCarreauPlayed() {
        return nbCouleursPlayed[TarotReferentiel.getIdCarreau()];
    }

    public int getNbPiquePlayed() {
        return nbCouleursPlayed[TarotReferentiel.getIdPique()];
    }

    public boolean isPetitPlayed() {
        return petitPlayed;
    }

    public int[] getNbCouleursPlayed() {
        return nbCouleursPlayed;
    }

    public boolean isCouleurPlayed(int idCouleur) {
        switch (idCouleur) {
            case 0:
                return (getNbAtoutPlayed() != 0);
            case 1:
                return (getNbCoeurPlayed() != 0);
            case 2:
                return (getNbTreflePlayed() != 0);
            case 3:
                return (getNbCarreauPlayed() != 0);
            default:
                return (getNbPiquePlayed() != 0);
        }
    }

    public float[] getNbPointsRestantsDansCouleur() {
        return nbPointsRestantsDansCouleur;
    }

    public boolean[] getCoupesPreneur() {
        return coupesPreneur;
    }

    /*
     * Renvoit true ou false selon que la couleur passee en param est coupee par
     * l'attaquant
     */
    public boolean isCoupePreneur(int idCouleur) {
        return coupesPreneur[idCouleur];
    }

    public void preneurHasNoMoreAtouts() {
        Arrays.fill(coupesPreneur, false);
        preneurHasAtouts = false;
    }

    public boolean preneurHasAtouts() {
        return preneurHasAtouts;
    }

    public void setCoupe(int idPlayer, int idCouleur) {
        listCoupesJoueurs.get(idPlayer)[idCouleur] = true;
    }

    public void setPlayerHasNoMoreAtouts(int idPlayer) {
        Arrays.fill(listCoupesJoueurs.get(idPlayer), false);
    }

    /**
     * Renvoit true si la carte passee en parametre est la carte la plus forte
     * de la couleur. Atouts et couleurs. Attention : prends en compte les
     * cartes posees sur le pli (un 20 sera maitre si le 21 est joue au meme pli
     * mais avant.)
     *
     * @param carte
     * @return
     */
    public boolean isCarteMaitre(CarteTarot carte) {
        return carte.getValeurAbstraite() == nbCarteMaitre[carte.getIdCouleur()];
    }

    /**
     * Methode a n'utiliser que pour savoir si la carte actuellement maitresse
     * sur le pli est la carte la plus forte du jeu. Ne fonctionne que pour les
     * atouts.
     *
     * @param carte
     * @return
     */
    public boolean isCarteLaPlusForteDuJeu(int valueMaxOfPli) {
        return valueMaxOfPli > valueMax;
    }

    public void setNbToursCouleur(int[] nbToursCouleur) {
        this.nbToursCouleur = nbToursCouleur;
    }

    public void setNbCouleursPlayed(int[] nbCouleursPlayed) {
        this.nbCouleursPlayed = nbCouleursPlayed;
    }

    public void setNbCarteMaitre(int[] nbCarteMaitre) {
        this.nbCarteMaitre = nbCarteMaitre;
    }

    public void setListCoupesJoueurs(List<boolean[]> listCoupesJoueurs) {
        this.listCoupesJoueurs = listCoupesJoueurs;
    }

    public void setNbPointsRestantsDansCouleur(float[] nbPointsRestantsDansCouleur) {
        this.nbPointsRestantsDansCouleur = nbPointsRestantsDansCouleur;
    }

    public void setCoupesPreneur(boolean[] coupesPreneur) {
        this.coupesPreneur = coupesPreneur;
    }

    public void setCoupesInit(boolean[] coupesInit) {
        this.coupesInit = coupesInit;
    }

    public void setPetitPlayed(boolean petitPlayed) {
        this.petitPlayed = petitPlayed;
    }

    private void setValueMax(int valueMax) {
        this.valueMax = valueMax;
    }

    private void setPreneurHasAtouts(boolean preneurHasAtouts) {
        this.preneurHasAtouts = preneurHasAtouts;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject o = new JSONObject();
        o.put("petitPlayed", petitPlayed);
        o.put("valueMax", valueMax);
        o.put("preneurHasAtouts", preneurHasAtouts);

        JSONArray a = new JSONArray();
        for (int i = 0; i < nbToursCouleur.length; i++) {
            a.put(nbToursCouleur[i]);
        }
        o.put("nbToursCouleur", a);

        a = new JSONArray();
        for (int i = 0; i < nbCouleursPlayed.length; i++) {
            a.put(nbCouleursPlayed[i]);
        }
        o.put("nbCouleursPlayed", a);

        a = new JSONArray();
        for (int i = 0; i < nbCarteMaitre.length; i++) {
            a.put(nbCarteMaitre[i]);
        }
        o.put("nbCarteMaitre", a);

        a = new JSONArray();
        for (int i = 0; i < nbPointsRestantsDansCouleur.length; i++) {
            a.put(nbPointsRestantsDansCouleur[i]);
        }
        o.put("nbPointsRestantsDansCouleur", a);

        a = new JSONArray();
        for (int i = 0; i < coupesPreneur.length; i++) {
            a.put(coupesPreneur[i]);
        }
        o.put("coupesPreneur", a);

        a = new JSONArray();
        for (int i = 0; i < coupesInit.length; i++) {
            a.put(coupesInit[i]);
        }
        o.put("coupesInit", a);

        a = new JSONArray();
        JSONArray a2;
        JSONObject o2;
        JSONObject o3;
        if (!Utils.isListEmpty(listCoupesJoueurs)) {
            for (int i = 0; i < listCoupesJoueurs.size(); i++) {
                boolean[] coupeJoueur = listCoupesJoueurs.get(i);
                a2 = new JSONArray();
                o2 = new JSONObject();
                for (int j = 0; j < coupeJoueur.length; j++) {
                    o3 = new JSONObject();
                    o3.put("value", coupeJoueur[j]);
                    a2.put(o3);
                }
                o2.put("coupeJoueur", a2);
                a.put(o2);
            }
        }
        o.put("listCoupesJoueurs", a);

        return o;
    }
}
