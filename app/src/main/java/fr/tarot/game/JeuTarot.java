package fr.tarot.game;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import fr.commun.game.Carte;
import fr.commun.game.Jeu;
import fr.commun.utils.Logs;
import fr.commun.utils.Utils;
import fr.tarot.utils.CarteComparator;
import fr.tarot.utils.CarteHonneurComparator;
import fr.tarot.utils.CarteValueComparator;
import fr.tarot.utils.TarotReferentiel;

public class JeuTarot extends Jeu {
    private List<CarteTarot> jeuAtout = null;
    private List<CarteTarot> jeuAtoutsMajeurs = null;
    private List<CarteTarot> jeuCoeur = null;
    private List<CarteTarot> jeuTrefle = null;
    private List<CarteTarot> jeuCarreau = null;
    private List<CarteTarot> jeuPique = null;
    private List<CarteTarot> jeuLongue = null;
    private CarteTarot excuse;
    private CarteTarot vingtEtUn;
    private CarteTarot petit;
    // pour le comptage de la valeur du jeu
    private int nbAtoutMajeur = 0;
    private int nbCoeur = 0;
    private int nbTrefle = 0;
    private int nbCarreau = 0;
    private int nbPique = 0;
    private int longue5 = 0;
    private int longue6 = 0;
    private int longue7 = 0;
    private int coupes = 4;
    private int singletons = 4;
    private int nbValet = 0;
    private int nbCavalier = 0;
    private boolean dameCoeur = false;
    private boolean dameTrefle = false;
    private boolean dameCarreau = false;
    private boolean damePique = false;
    private boolean roiCoeur = false;
    private boolean roiTrefle = false;
    private boolean roiCarreau = false;
    private boolean roiPique = false;
    private int nbDameSeules = 0;
    private int couplesRoiDame = 0;
    private int nbRoiSeuls = 0;

    public JeuTarot() {
        super();
        jeuAtout = new ArrayList<CarteTarot>();
        jeuAtoutsMajeurs = new ArrayList<CarteTarot>();
        jeuCoeur = new ArrayList<CarteTarot>();
        jeuTrefle = new ArrayList<CarteTarot>();
        jeuCarreau = new ArrayList<CarteTarot>();
        jeuPique = new ArrayList<CarteTarot>();
        jeuLongue = new ArrayList<CarteTarot>();
    }

    public JeuTarot(JSONObject o) throws JSONException {
        super(o);
        nbAtoutMajeur = o.getInt("nbAtoutMajeur");
        nbCoeur = o.getInt("nbCoeur");
        nbTrefle = o.getInt("nbTrefle");
        nbCarreau = o.getInt("nbCarreau");
        nbPique = o.getInt("nbPique");
        longue5 = o.getInt("longue5");
        longue6 = o.getInt("longue6");
        longue7 = o.getInt("longue7");
        coupes = o.getInt("coupes");
        singletons = o.getInt("singletons");
        nbValet = o.getInt("nbValet");
        nbCavalier = o.getInt("nbCavalier");
        dameCoeur = o.getBoolean("dameCoeur");
        dameTrefle = o.getBoolean("dameTrefle");
        dameCarreau = o.getBoolean("dameCarreau");
        damePique = o.getBoolean("damePique");
        roiCoeur = o.getBoolean("roiCoeur");
        roiTrefle = o.getBoolean("roiTrefle");
        roiCarreau = o.getBoolean("roiCarreau");
        roiPique = o.getBoolean("roiPique");
        nbDameSeules = o.getInt("nbDameSeules");
        couplesRoiDame = o.getInt("couplesRoiDame");
        nbRoiSeuls = o.getInt("nbRoiSeuls");
        excuse = TarotReferentiel.getCarteFromMapWithID(o.getInt("excuse"));
        petit = TarotReferentiel.getCarteFromMapWithID(o.getInt("petit"));
        vingtEtUn = TarotReferentiel.getCarteFromMapWithID(o.getInt("vingtEtUn"));
        JSONObject obj;
        JSONArray array = o.getJSONArray("jeuAtout");
        jeuAtout = new ArrayList<CarteTarot>();
        for (int i = 0; i < array.length(); i++) {
            obj = array.getJSONObject(i);
            jeuAtout.add(TarotReferentiel.getCarteFromMapWithID(obj.getInt("id")));
        }
        jeuAtoutsMajeurs = new ArrayList<CarteTarot>();
        array = o.getJSONArray("jeuAtoutsMajeurs");
        jeuAtoutsMajeurs = new ArrayList<CarteTarot>();
        for (int i = 0; i < array.length(); i++) {
            obj = array.getJSONObject(i);
            jeuAtoutsMajeurs.add(TarotReferentiel.getCarteFromMapWithID(obj.getInt("id")));
        }
        jeuCoeur = new ArrayList<CarteTarot>();
        array = o.getJSONArray("jeuCoeur");
        jeuCoeur = new ArrayList<CarteTarot>();
        for (int i = 0; i < array.length(); i++) {
            obj = array.getJSONObject(i);
            jeuCoeur.add(TarotReferentiel.getCarteFromMapWithID(obj.getInt("id")));
        }
        jeuTrefle = new ArrayList<CarteTarot>();
        array = o.getJSONArray("jeuTrefle");
        jeuTrefle = new ArrayList<CarteTarot>();
        for (int i = 0; i < array.length(); i++) {
            obj = array.getJSONObject(i);
            jeuTrefle.add(TarotReferentiel.getCarteFromMapWithID(obj.getInt("id")));
        }
        jeuCarreau = new ArrayList<CarteTarot>();
        array = o.getJSONArray("jeuCarreau");
        jeuCarreau = new ArrayList<CarteTarot>();
        for (int i = 0; i < array.length(); i++) {
            obj = array.getJSONObject(i);
            jeuCarreau.add(TarotReferentiel.getCarteFromMapWithID(obj.getInt("id")));
        }
        jeuPique = new ArrayList<CarteTarot>();
        array = o.getJSONArray("jeuPique");
        jeuPique = new ArrayList<CarteTarot>();
        for (int i = 0; i < array.length(); i++) {
            obj = array.getJSONObject(i);
            jeuPique.add(TarotReferentiel.getCarteFromMapWithID(obj.getInt("id")));
        }
        jeuLongue = new ArrayList<CarteTarot>();
        array = o.getJSONArray("jeuLongue");
        jeuLongue = new ArrayList<CarteTarot>();
        for (int i = 0; i < array.length(); i++) {
            obj = array.getJSONObject(i);
            jeuLongue.add(TarotReferentiel.getCarteFromMapWithID(obj.getInt("id")));
        }
    }

    /**
     * Retire la carte passee en param.
     *
     * @param carte
     */
    public void remove(CarteTarot carte) {
        if (carte.isExcuse())
            excuse = null;
        if (carte.is21())
            vingtEtUn = null;
        if (carte.isPetit())
            petit = null;
        getJeuCouleur(carte.getIdCouleur()).remove(carte);
        hand.remove(carte);
        Collections.sort(hand, new CarteComparator());
        Collections.sort(getJeuCouleur(carte.getIdCouleur()), new CarteComparator());
    }

    /**
     * Retire les cartes passees en param.
     *
     * @param carte
     */
    public void removeAll(Collection<CarteTarot> cartes) {
        for (CarteTarot carte : cartes) {
            remove(carte);
        }
    }

    /**
     * Methode qui va renvoyer la carte la plus basse dans la couleur
     *
     * @param idCouleur
     * @return CarteTarot
     */
    public CarteTarot getCarteLaPlusBasseDansCouleur(int idCouleur) {
        List<CarteTarot> jeu = getJeuCouleur(idCouleur);
        if (jeu != null && jeu.size() != 0) {
            return jeu.get(0);
        }
        return null;
    }

    /**
     * Check si il y a encore des cartes dans la couleur.
     *
     * @param idCouleur
     * @return
     */
    public boolean hasCarteInCouleur(int idCouleur) {
        List<CarteTarot> jeu = getJeuCouleur(idCouleur);
        if (jeu != null && jeu.size() != 0) {
            return true;
        }
        return false;
    }

    /**
     * Methode qui va renvoyer la carte la plus haute dans la couleur si c'est
     * un honneur. sinon renvoit la plus basse
     *
     * @param idCouleur
     * @return CarteTarot
     */
    public CarteTarot getCarteLaPlusHauteDansCouleur(int idCouleur) {
        List<CarteTarot> jeu = getJeuCouleur(idCouleur);
        if (jeu != null && jeu.size() != 0) {
            return (jeu.get(jeu.size() - 1).isBasse() == false ? jeu.get(jeu.size() - 1) : jeu.get(0));
        }
        return null;
    }

    public List<CarteTarot> getJeuCouleur(int idCouleur) {
        switch (idCouleur) {
            case 0:
                return jeuAtout;
            case 1:
                return jeuCoeur;
            case 2:
                return jeuTrefle;
            case 3:
                return jeuCarreau;
            default:
                // normalement case 4...
                return jeuPique;
        }
    }

    /**
     * On ajoute une carte au jeu.
     *
     * @param CarteTarot
     */
    public void add(CarteTarot carte) {
        hand.add(carte);
        getJeuCouleur(carte.getIdCouleur()).add(carte);
        if ("C".equals(carte.getValeurFaciale()))
            nbCavalier++;
        if ("V".equals(carte.getValeurFaciale()))
            nbValet++;
        if (carte.is21())
            vingtEtUn = carte;
        if (carte.isExcuse())
            excuse = carte;
        if (carte.isPetit())
            petit = carte;
        switch (carte.getIdCouleur()) {
            case 0:
                addAtout(carte);
                break;
            case 1:
                addCoeur(carte);
                break;
            case 2:
                addTrefle(carte);
                break;
            case 3:
                addCarreau(carte);
                break;
            default:
                // normalement case 4...
                addPique(carte);
                break;
        }

        Collections.sort(hand, new CarteComparator());
        Collections.sort(getJeuCouleur(carte.getIdCouleur()), new CarteComparator());
    }

    /**
     * On ajoute plusieurs cartes au jeu.
     *
     * @param cartes
     */
    public void addAll(List<Carte> cartes) {
        for (Carte carte : cartes) {
            add((CarteTarot) carte);
        }
    }

    private void addAtout(CarteTarot carte) {
        if (carte.getValeurAbstraite() >= 30) {
            nbAtoutMajeur++;
            jeuAtoutsMajeurs.add(carte);
        }
    }

    private void addCoeur(CarteTarot carte) {
        if (nbCoeur == 0)
            coupes--;
        if (nbCoeur == 1)
            singletons--;
        nbCoeur++;
        if (nbCoeur == 7) {
            longue6--;
            longue7++;
        } else if (nbCoeur == 6) {
            longue5--;
            longue6++;
        } else if (nbCoeur == 5) {
            longue5++;
        }

        if ("D".equals(carte.getValeurFaciale())) {
            dameCoeur = true;
            nbDameSeules++;
            if (roiCoeur) {
                nbDameSeules--;
                couplesRoiDame++;
            }
        } else if ("R".equals(carte.getValeurFaciale())) {
            roiCoeur = true;
            nbRoiSeuls++;
            if (dameCoeur) {
                nbRoiSeuls--;
                couplesRoiDame++;
            }
        }
    }

    private void addTrefle(CarteTarot carte) {
        if (nbTrefle == 0)
            coupes--;
        if (nbTrefle == 1)
            singletons--;
        nbTrefle++;
        if (nbTrefle == 7) {
            longue6--;
            longue7++;
        } else if (nbTrefle == 6) {
            longue5--;
            longue6++;
        } else if (nbTrefle == 5) {
            longue5++;
        }

        if ("D".equals(carte.getValeurFaciale())) {
            dameTrefle = true;
            nbDameSeules++;
            if (roiTrefle) {
                nbDameSeules--;
                couplesRoiDame++;
            }
        } else if ("R".equals(carte.getValeurFaciale())) {
            roiTrefle = true;
            nbRoiSeuls++;
            if (dameTrefle) {
                nbRoiSeuls--;
                couplesRoiDame++;
            }
        }
    }

    private void addCarreau(CarteTarot carte) {
        if (nbCarreau == 0)
            coupes--;
        if (nbCarreau == 1)
            singletons--;
        nbCarreau++;
        if (nbCarreau == 7) {
            longue6--;
            longue7++;
        } else if (nbCarreau == 6) {
            longue5--;
            longue6++;
        } else if (nbCarreau == 5) {
            longue5++;
        }

        if ("D".equals(carte.getValeurFaciale())) {
            dameCarreau = true;
            nbDameSeules++;
            if (roiCarreau) {
                nbDameSeules--;
                couplesRoiDame++;
            }
        } else if ("R".equals(carte.getValeurFaciale())) {
            roiCarreau = true;
            nbRoiSeuls++;
            if (dameCarreau) {
                nbRoiSeuls--;
                couplesRoiDame++;
            }
        }
    }

    private void addPique(CarteTarot carte) {
        if (nbPique == 0)
            coupes--;
        if (nbPique == 1)
            singletons--;
        nbPique++;
        if (nbPique == 7) {
            longue6--;
            longue7++;
        } else if (nbPique == 6) {
            longue5--;
            longue6++;
        } else if (nbPique == 5) {
            longue5++;
        }

        if ("D".equals(carte.getValeurFaciale())) {
            damePique = true;
            nbDameSeules++;
            if (roiPique) {
                nbDameSeules--;
                nbRoiSeuls--;
                couplesRoiDame++;
            }
        } else if ("R".equals(carte.getValeurFaciale())) {
            roiPique = true;
            nbRoiSeuls++;
            if (damePique) {
                nbRoiSeuls--;
                nbDameSeules--;
                couplesRoiDame++;
            }
        }
    }

    /**
     * Methode appelee lorsque le joueur n'a plus d'atout et qu'il veut se
     * defausser d'une petite carte. En general il va prendre la plus petite.
     * ATTENTION : peut renvoyer le Petit!!!
     *
     * @return CarteTarot
     */
    public CarteTarot getCarteLaPlusBasse() {
        Collections.sort(hand, new CarteValueComparator());
        return (CarteTarot) hand.get(0);
    }

    /**
     * Methode appelee lorsque le joueur n'a plus d'atout et qu'il est sur que
     * le pli va etre fait par son camp. Si pas d'honneur, renvoit une carte
     * basse.
     *
     * @return CarteTarot
     */
    public CarteTarot getHonneurLePlusHaut() {
        Collections.sort(hand, new CarteHonneurComparator());
        return ((CarteTarot) hand.get(0)).isBasse() == false ? (CarteTarot) hand.get(0) : getCarteLaPlusBasse();
    }

    /**
     * Methode qui va renvoyer l'atout le plus proche et de valeur superieure a
     * valueMin. Attention : peut renvoyer le Petit mais pas l'excuse Si plus
     * d'atout renvoit null
     *
     * @param valeurMin
     * @return CarteTarot or null
     */
    public CarteTarot getAtoutToBeMaitre(int valeurMin) {
        Collections.sort(jeuAtout, new CarteComparator());
        if (jeuAtout != null && jeuAtout.size() > 0) {
            for (CarteTarot carte : jeuAtout) {
                if (carte.getValeurAbstraite() > valeurMin) {
                    return carte;
                }
            }
        }
        return null;
    }

    /**
     * Methode qui va renvoyer l'atout le plus gros et de valeur superieure a
     * valueMin.. Si pas d'atout trouvee ou plus d'atout renvoit null
     *
     * @param valeurMin
     * @return CarteTarot or null
     */
    public CarteTarot getAtoutLePlusFort(int valeurMin) {
        Collections.sort(jeuAtout, new CarteComparator());
        if (jeuAtout != null && jeuAtout.size() > 0) {
            if (jeuAtout.get(jeuAtout.size() - 1).getValeurAbstraite() > valeurMin) {
                return jeuAtout.get(jeuAtout.size() - 1);
            }

        }
        return null;
    }

    /**
     * Methode qui va renvoyer l'atout le plus faible ormis le petit et
     * l'excuse. Si plus d'atout faibles renvoit l'excuse. null si plus d'atouts
     * ou seulement le petit.
     *
     * @param valeurMin
     * @return CarteTarot or null
     */
    public CarteTarot getAtoutLePlusFaibleOrmisPetit() {
        Collections.sort(jeuAtout, new CarteComparator());
        if (jeuAtout != null && jeuAtout.size() > 0) {
            for (CarteTarot carte : jeuAtout) {
                if (!carte.isPetit()) {
                    return carte;
                }
            }
        }
        if (excuse != null)
            return excuse;
        return null;
    }

    public int getSizeOfGame() {
        return hand.size();
    }

    public int getNbAtouts() {
        return jeuAtout.size();
    }

    public List<CarteTarot> getJeuAtout() {
        Collections.sort(jeuAtout, new CarteComparator());
        return jeuAtout;
    }

    public List<CarteTarot> getJeuAtoutsMajeurs() {
        Collections.sort(jeuAtoutsMajeurs, new CarteComparator());
        return jeuAtoutsMajeurs;
    }

    public void ordonne() {
        Collections.sort(hand, new CarteComparator());
        Collections.sort(jeuAtout, new CarteComparator());
        Collections.sort(jeuAtoutsMajeurs, new CarteComparator());
        Collections.sort(jeuCoeur, new CarteComparator());
        Collections.sort(jeuTrefle, new CarteComparator());
        Collections.sort(jeuCarreau, new CarteComparator());
        Collections.sort(jeuPique, new CarteComparator());
    }

    /**
     * Methode pour trouver une coupe dans le jeu
     *
     * @param nbCartesPossibles
     * @return List<CarteTarot>
     */
    public List<CarteTarot> getCartesPourCoupeFranchePourEcart(int nbCartesPossibles, int[] couleurs) {
        List<CarteTarot> cartesEcart = null;
        // on regarde pour toutes les couleurs, il faut qu'il y en ai moins de 7
        // et qu'on ai pas le roi
        for (int i = 1; i <= nbCartesPossibles; i++) {
            if (couleurs[TarotReferentiel.getIdCoeur()] != TarotReferentiel.getIdCoeur() && !roiCoeur && nbCoeur == i)
                cartesEcart = jeuCoeur;
            else if (couleurs[TarotReferentiel.getIdTrefle()] != TarotReferentiel.getIdTrefle() && !roiTrefle
                    && nbTrefle == i)
                cartesEcart = jeuTrefle;
            else if (couleurs[TarotReferentiel.getIdCarreau()] != TarotReferentiel.getIdCarreau() && !roiCarreau
                    && nbCarreau == i)
                cartesEcart = jeuCarreau;
            else if (couleurs[TarotReferentiel.getIdPique()] != TarotReferentiel.getIdPique() && !roiPique
                    && nbPique == i)
                cartesEcart = jeuPique;
            if (cartesEcart != null)
                break;
        }
        return cartesEcart;
    }

    /**
     * Methode pour trouver une singlette dans le jeu. Le jeu renvoye ne
     * comprend pas le roi si il est pr�sent dans la liste initiale.
     *
     * @param nbCartesPossibles
     * @return List<CarteTarot>
     */
    public List<CarteTarot> getCartesPourSinglettePourEcart(int nbCartesPossibles, int[] couleurs) {
        boolean retirerRoi = false;
        List<CarteTarot> cartesEcart = null;
        // il faut que le nombre de cartes dans la couleur moins i (+1 si on a
        // le roi) soit �gal � 1
        for (int i = 1; i <= nbCartesPossibles; i++) {
            if (couleurs[TarotReferentiel.getIdCoeur()] != TarotReferentiel.getIdCoeur()
                    && nbCoeur - (i + (roiCoeur ? 1 : 0)) == 0)
                cartesEcart = jeuCoeur;
            else if (couleurs[TarotReferentiel.getIdTrefle()] != TarotReferentiel.getIdTrefle()
                    && nbTrefle - (i + (roiTrefle ? 1 : 0)) == 0)
                cartesEcart = jeuTrefle;
            else if (couleurs[TarotReferentiel.getIdCarreau()] != TarotReferentiel.getIdCarreau()
                    && nbCarreau - (i + (roiCarreau ? 1 : 0)) == 0)
                cartesEcart = jeuCarreau;
            else if (couleurs[TarotReferentiel.getIdPique()] != TarotReferentiel.getIdPique()
                    && nbPique - (i + (roiPique ? 1 : 0)) == 0)
                cartesEcart = jeuPique;
            if (cartesEcart != null)
                break;
        }
        if (cartesEcart != null && "R".equalsIgnoreCase(cartesEcart.get(cartesEcart.size() - 1).getValeurFaciale())) {
            retirerRoi = true;
        }
        if (cartesEcart == null)
            return null;
        // c'est une singlette, on va donc mettre les points au chien, ou
        // laisser seulement le Roi
        return retirerRoi ? cartesEcart.subList(0, cartesEcart.size() - 1) : cartesEcart.subList(0, cartesEcart.size());
    }

    /**
     * Methode qui va retourner les cartes restantes pour que le chien soit
     * complet. En fonction du nombre d'atouts et de bout
     *
     * @param nbCartesPossibles
     * @param couleurs
     * @return
     */
    public List<CarteTarot> getCartesPourEcart(int nbCartesPossibles, int[] couleurs) {
        // TODO faire en fonction du nombre d'atouts et de bouts...
        int couleurLaMoinsLongue = -1;
        int tailleCouleurLaMoinsLongue = 50; // 50 valeur par defaut pour etre
        // sup�rieur � 14
        boolean retirerRoi = false;
        boolean continuer = true;
        List<CarteTarot> cartesCouleurEcart = null;
        List<CarteTarot> cartesEcart = new ArrayList<CarteTarot>();
        // int nbBouts =
        // (excuse!=null?1:0)+(petit!=null?1:0)+(vingtEtUn!=null?1:0);
        do {
            for (int i = 1; i < 5; i++) {
                // on it�re sur chaque couleur, si la couleur est pas dans le
                // tableau c'est qu'il n'y a pas de coupe ou de singlette dessus
                if (couleurs[i] != i) {
                    // on exclue les couleurs vides et celles o� il n'y a que le
                    // roi
                    if (getJeuCouleur(i).size() != 0 && !(getJeuCouleur(i).size() == 1 && hasRoiInCouleur(i))
                            && tailleCouleurLaMoinsLongue > getJeuCouleur(i).size()) {
                        tailleCouleurLaMoinsLongue = getJeuCouleur(i).size();
                        couleurLaMoinsLongue = i;
                    } else if (getJeuCouleur(i).size() == 0 || (getJeuCouleur(i).size() == 1 && hasRoiInCouleur(i))) {
                        // cette couleur ne pourra pas servir, on la met dans le
                        // tableau
                        couleurs[i] = i;
                    }
                }
            }
            if (couleurLaMoinsLongue > 0) {
                cartesCouleurEcart = getJeuCouleur(couleurLaMoinsLongue);
                if (hasRoiInCouleur(couleurLaMoinsLongue)) {
                    retirerRoi = true;
                }
                int index = cartesCouleurEcart.size() - nbCartesPossibles;
                if (index <= 0) {
                    // on prends tout sauf le roi eventuel
                    cartesEcart.addAll(retirerRoi ? cartesCouleurEcart.subList(0, cartesCouleurEcart.size() - 1)
                            : cartesCouleurEcart);
                } else {
                    // on prends juste ce qui nous interesse
                    cartesEcart.addAll(cartesCouleurEcart.subList(0, nbCartesPossibles));
                }
            }
            if (cartesEcart.size() == nbCartesPossibles
                    || (couleurs[TarotReferentiel.getIdCoeur()] == TarotReferentiel.getIdCoeur()
                    && couleurs[TarotReferentiel.getIdTrefle()] == TarotReferentiel.getIdTrefle()
                    && couleurs[TarotReferentiel.getIdCarreau()] == TarotReferentiel.getIdCarreau() && couleurs[TarotReferentiel
                    .getIdPique()] == TarotReferentiel.getIdPique())) {
                continuer = false;
            }
        } while (continuer);
        // si plus de couleur possibles, il faut retirer des atouts....
        if (cartesCouleurEcart == null || cartesEcart.size() != nbCartesPossibles) {
            Logs.info("#######################   ATOUTS ECARTES!!!!!!!!");
            int decal = (excuse != null ? 1 : 0) + (petit != null ? 1 : 0);
            cartesEcart.addAll(jeuAtout.subList(decal, decal + nbCartesPossibles - cartesEcart.size()));
        }
        return cartesEcart;
        /*
         * if (jeuAtout.size()<8){
         * 
         * } else if (jeuAtout.size()<10){
         * 
         * } else {
         * 
         * }
         */
    }

    private boolean hasRoiInCouleur(int couleur) {
        switch (couleur) {
            case 1:
                return roiCoeur;
            case 2:
                return roiTrefle;
            case 3:
                return roiCarreau;
            case 4:
                return roiPique;
            default:
                return false;
        }
    }

    public List<CarteTarot> getJeuLongue() {
        if (jeuLongue.size() == 0) {
            for (int i = 14; i > 0; i--) {
                for (int j = 1; j <= 4; j++) {
                    if (getJeuCouleur(j).size() == i) {
                        jeuLongue = getJeuCouleur(j);
                        break;
                    }
                }
                if (jeuLongue.size() != 0)
                    break;
            }
        }
        return jeuLongue;
    }

    /**
     * Methode qui renvoit l'Excuse ou null
     *
     * @return CarteTarot
     */
    public CarteTarot getExcuse() {
        return excuse;
    }

    /**
     * Methode qui renvoit le 21 ou null
     *
     * @return CarteTarot
     */
    public CarteTarot get21() {
        return vingtEtUn;
    }

    /**
     * Methode qui renvoit le Petit ou null
     *
     * @return CarteTarot
     */
    public CarteTarot getPetit() {
        return petit;
    }

    public int getLongue5() {
        return longue5;
    }

    public int getLongue6() {
        return longue6;
    }

    public int getLongue7() {
        return longue7;
    }

    public int getCoupes() {
        return coupes;
    }

    public int getSingletons() {
        return singletons;
    }

    public int getNbValet() {
        return nbValet;
    }

    public int getNbDameSeules() {
        return nbDameSeules;
    }

    public int getCouplesRoiDame() {
        return couplesRoiDame;
    }

    public int getNbRoiSeuls() {
        return nbRoiSeuls;
    }

    public int getNbCavalier() {
        return nbCavalier;
    }

    public int getNbAtoutsMajeurs() {
        return nbAtoutMajeur;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject o = super.toJSON();
        o.put("nbAtoutMajeur", nbAtoutMajeur);
        o.put("nbCoeur", nbCoeur);
        o.put("nbTrefle", nbTrefle);
        o.put("nbCarreau", nbCarreau);
        o.put("nbPique", nbPique);
        o.put("longue5", longue5);
        o.put("longue6", longue6);
        o.put("longue7", longue7);
        o.put("coupes", coupes);
        o.put("singletons", singletons);
        o.put("nbValet", nbValet);
        o.put("nbCavalier", nbCavalier);
        o.put("dameCoeur", dameCoeur);
        o.put("dameTrefle", dameTrefle);
        o.put("dameCarreau", dameCarreau);
        o.put("damePique", damePique);
        o.put("roiCoeur", roiCoeur);
        o.put("roiTrefle", roiTrefle);
        o.put("roiCarreau", roiCarreau);
        o.put("roiPique", roiPique);
        o.put("nbDameSeules", nbDameSeules);
        o.put("couplesRoiDame", couplesRoiDame);
        o.put("nbRoiSeuls", nbRoiSeuls);
        if (excuse != null) {
            o.put("excuse", excuse.toJSON());
        }
        if (vingtEtUn != null) {
            o.put("vingtEtUn", vingtEtUn.toJSON());
        }
        if (petit != null) {
            o.put("petit", petit.toJSON());
        }
        JSONArray array = new JSONArray();
        if (!Utils.isListEmpty(jeuAtout)) {
            for (CarteTarot carte : jeuAtout) {
                array.put(carte.toJSON());
            }
        }
        o.put("jeuAtout", array);

        array = new JSONArray();
        if (!Utils.isListEmpty(jeuAtoutsMajeurs)) {
            for (CarteTarot carte : jeuAtoutsMajeurs) {
                array.put(carte.toJSON());
            }
        }
        o.put("jeuAtoutsMajeurs", array);

        array = new JSONArray();
        if (!Utils.isListEmpty(jeuCarreau)) {
            for (CarteTarot carte : jeuCarreau) {
                array.put(carte.toJSON());
            }
        }
        o.put("jeuCarreau", array);

        array = new JSONArray();
        if (!Utils.isListEmpty(jeuCoeur)) {
            for (CarteTarot carte : jeuCoeur) {
                array.put(carte.toJSON());
            }
        }
        o.put("jeuCoeur", array);

        array = new JSONArray();
        if (!Utils.isListEmpty(jeuLongue)) {
            for (CarteTarot carte : jeuLongue) {
                array.put(carte.toJSON());
            }
        }
        o.put("jeuLongue", array);

        array = new JSONArray();
        if (!Utils.isListEmpty(jeuPique)) {
            for (CarteTarot carte : jeuPique) {
                array.put(carte.toJSON());
            }
        }
        o.put("jeuPique", array);

        array = new JSONArray();
        if (!Utils.isListEmpty(jeuTrefle)) {
            for (CarteTarot carte : jeuTrefle) {
                array.put(carte.toJSON());
            }
        }
        o.put("jeuTrefle", array);
        return o;
    }
}
