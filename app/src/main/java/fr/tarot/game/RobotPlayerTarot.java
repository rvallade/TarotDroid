package fr.tarot.game;

import java.util.ArrayList;
import java.util.List;

import fr.commun.game.Pli;
import fr.commun.utils.Logs;
import fr.tarot.utils.CalculValeurJeu;
import fr.tarot.utils.TarotReferentiel;

/**
 * Created by Romain on 07/07/2016.
 */
public class RobotPlayerTarot extends PlayerTarot {
    public RobotPlayerTarot(int id, String name, int idNextPlayer, int placeInListeGlobale, int placeInListeOrdonnee) {
        super(id, name, idNextPlayer, placeInListeGlobale, placeInListeOrdonnee);
        human = false;
    }

    @Override
    public int takeDecision(int lastEnchere) {
        jeu.ordonne();
        scoreJeu = CalculValeurJeu.getValeurJeu(jeu);

        if (scoreJeu > 40 && lastEnchere < TarotReferentiel.getIdPrise()) {
            enchere = TarotReferentiel.getIdPrise();
        }
        if (scoreJeu > 56 && lastEnchere < TarotReferentiel.getIdGarde()) {
            enchere = TarotReferentiel.getIdGarde();
        }
        // apres rentrent en ligne de compte les coupes et les singlettes
        if (enchere == TarotReferentiel.getIdGarde()) {
            scoreJeu = CalculValeurJeu.getValeurJeuForGSOrGC(jeu);
            if (scoreJeu > 71 && lastEnchere < TarotReferentiel.getIdGardeSans())
                enchere = TarotReferentiel.getIdGardeSans();
            if (scoreJeu > 80 && lastEnchere < TarotReferentiel.getIdGardeContre())
                enchere = TarotReferentiel.getIdGardeContre();
        }
        Logs.info(getName() + ", valeur du jeu : " + scoreJeu + " ==> " + TarotReferentiel.getContrat(enchere).getName());
        return enchere;
    }

    @Override
    public CarteTarot play(PliTarot pli) {
        if (isPreneur()) {
            return playRobotAttaque(pli);
        } else {
            return playRobotDefense(pli);
        }
    }

    private CarteTarot playRobotAttaque(PliTarot pli) {
        CarteTarot carteJouee = null;
        // cas particulier, si il ne lui reste que deux cartes et que l'une d'entre elles est l'excuse il doit le jouer, sinon il la perd
        if (getSizeOfGame() == 2 && jeu.getExcuse() != null) {
            carteJouee = jeu.getExcuse();
        } else {
            GameDatas gameDatas = GameDatas.getGameDatas();
            if (pli.getCards().isEmpty() || pli.getIdCouleurDemandee() == -1) {
                // premier a jouer ou la premiere carte est l'excuse?
                // on va regarder si il a une longue. Si oui il va jouer dans la longue, honneur en tete
                // si il en a.
                List<CarteTarot> longue = jeu.getJeuLongue();
                if (longue != null && longue.size() != 0) {
                    CarteTarot firstCardOfLongue = longue.get(0);
                    CarteTarot lastCardOfLongue = longue.get(longue.size() - 1);
                    if (gameDatas.getNbTourACouleur(firstCardOfLongue.getIdCouleur()) == 0
                            && "R".equals(lastCardOfLongue.getValeurFaciale())) {
                        carteJouee = lastCardOfLongue;
                    } else if (gameDatas.getNbTourACouleur(firstCardOfLongue.getIdCouleur()) == 1
                            && "D".equals(lastCardOfLongue.getValeurFaciale())
                            && gameDatas.isCarteMaitre(lastCardOfLongue)) {
                        // on tente la dame...
                        carteJouee = lastCardOfLongue;
                    } else {
                        carteJouee = firstCardOfLongue;
                        // cela peut etre une carte a points qui n'est pas maitre, on doit tester
                        if (!carteJouee.isBasse() && !gameDatas.isCarteMaitre(carteJouee)) {
                            // ne pas jouer la carte si elle n'est pas potentiellement maitre
                            int idCouleur = getCouleurDejaJoueeEtEnStock();
                            // on va jouer une couleur qui a deja ete jouee
                            if (idCouleur != -1) {
                                carteJouee = jeu.getJeuCouleur(idCouleur).get(0);
                            }
                            if (carteJouee == null || !carteJouee.isBasse() && !gameDatas.isCarteMaitre(carteJouee)) {
                                // on va jouer Atout ou ouvrir une couleur
                                // pas d'atout pour etre maitre, on met le plus faible
                                carteJouee = jeu.getAtoutLePlusFaibleOrmisPetit();
                                if (carteJouee == null || carteJouee.isExcuse()) {
                                    carteJouee = jeu.getCarteLaPlusBasse();
                                }
                            }
                        }
                    }
                } else {
                    int idCouleur = getCouleurDejaJoueeEtEnStock();
                    // on va jouer une couleur qui a deja ete jouee
                    if (idCouleur != -1) {
                        carteJouee = jeu.getJeuCouleur(idCouleur).get(0);
                    }
                    if (carteJouee == null || !carteJouee.isBasse() && !gameDatas.isCarteMaitre(carteJouee)) {
                        // on va jouer Atout ou ouvrir une couleur
                        // pas d'atout pour etre maitre, on met le plus faible
                        carteJouee = jeu.getAtoutLePlusFaibleOrmisPetit();
                        if (carteJouee == null || carteJouee.isExcuse()) {
                            carteJouee = jeu.getCarteLaPlusBasse();
                        }
                    }
                }
            } else {
                if (pli.getIdCouleurDemandee() != TarotReferentiel.getIdAtout()) {
                    // couleur demandee, pas atout
                    if (jeu.hasCarteInCouleur(pli.getIdCouleurDemandee())) {
                        // couleur en stock
                        if (isDernierAJouer(pli)) {
                            // on doit mettre ce qu'il faut pour etre maitre, sinon on met une petite
                            carteJouee = jeu.getCarteLaPlusHauteDansCouleur(pli.getIdCouleurDemandee());
                            if (!pli.testCarte(carteJouee.getValeurAbstraite()) || gameDatas.isCarteMaitre(carteJouee)) {
                                carteJouee = jeu.getCarteLaPlusBasseDansCouleur(pli.getIdCouleurDemandee());
                            }
                        } else {
                            // il n'est pas le dernier a jouer mais si il a le roi et que c'est le premier tour a cette couleur il doit le poser.
                            // pareil si c'est la dame et que c'est au deuxieme tour et qu'elle est maitre
                            if (gameDatas.getNbTourACouleur(pli.getIdCouleurDemandee()) == 0
                                    && "R".equals(jeu.getCarteLaPlusHauteDansCouleur(pli.getIdCouleurDemandee()).getValeurFaciale())) {
                                carteJouee = jeu.getCarteLaPlusHauteDansCouleur(pli.getIdCouleurDemandee());
                            } else if (gameDatas.getNbTourACouleur(pli.getIdCouleurDemandee()) == 1
                                    && "D".equals(jeu.getCarteLaPlusHauteDansCouleur(pli.getIdCouleurDemandee()).getValeurFaciale())
                                    && pli.testCarte(jeu.getCarteLaPlusHauteDansCouleur(pli.getIdCouleurDemandee()).getValeurAbstraite())
                                    && gameDatas.isCarteMaitre(jeu.getCarteLaPlusHauteDansCouleur(pli.getIdCouleurDemandee()))) {
                                carteJouee = jeu.getCarteLaPlusHauteDansCouleur(pli.getIdCouleurDemandee());
                            } else {
                                carteJouee = jeu.getCarteLaPlusBasseDansCouleur(pli.getIdCouleurDemandee());
                            }
                        }
                    } else {
                        // il n'a pas la couleur demandee, il doit couper
                        if (isDernierAJouer(pli)) {
                            // on pose le petit si on l'a.
                            if (jeu.getPetit() != null && pli.testCarte(jeu.getPetit().getValeurAbstraite())) {
                                carteJouee = jeu.getPetit();
                            } else {
                                if (pli.getRapportValueSurNbCartes() > 1) {
                                    carteJouee = jeu.getAtoutToBeMaitre(pli.getValueMax());
                                    if (carteJouee == null) {
                                        // on ne peut pas etre maitre, on met un atout plus petit
                                        carteJouee = jeu.getAtoutLePlusFaibleOrmisPetit();
                                    }
                                    if (carteJouee == null && jeu.getExcuse() != null)
                                        carteJouee = jeu.getExcuse();
                                    // en dernier recours....
                                    if (carteJouee == null && jeu.getPetit() != null)
                                        carteJouee = jeu.getPetit();
                                    // on pisse...
                                    if (carteJouee == null) {
                                        carteJouee = jeu.getCarteLaPlusBasse();
                                    }
                                } else {
                                    if (jeu.getExcuse() != null) {
                                        carteJouee = jeu.getExcuse();
                                    } else {
                                        carteJouee = getCartePourCoupe(pli);
                                    }
                                }
                            }
                        } else {
                            // Pli interessant ou pas?
                            if (pli.getRapportValueSurNbCartes() > 1 || pli.isPetitPlayed()) {
                                carteJouee = jeu.getAtoutLePlusFort(pli.getValueMax());
                                if (carteJouee != null
                                        && pli.testCarte(carteJouee.getValeurAbstraite())
                                        && gameDatas.isCarteMaitre(carteJouee)) {
                                    // cette carte est maitresse quoiqu'il arrive, on la joue
                                } else {
                                    carteJouee = jeu.getAtoutToBeMaitre(pli.getValueMax());
                                    // pas d'atout pour etre maitre, on met le plus faible
                                    if (carteJouee == null && jeu.getExcuse() != null)
                                        carteJouee = jeu.getExcuse();
                                    if (carteJouee == null) {
                                        carteJouee = jeu.getAtoutLePlusFaibleOrmisPetit();
                                    }
                                    if (carteJouee == null && jeu.getPetit() != null)
                                        carteJouee = jeu.getPetit();
                                    if (carteJouee == null) {
                                        carteJouee = jeu.getCarteLaPlusBasse();
                                    }

                                }
                            } else {
                                if (jeu.getExcuse() != null) {
                                    carteJouee = jeu.getExcuse();
                                } else {
                                    carteJouee = getCartePourCoupe(pli);
                                }
                            }
                        }
                    }
                } else {
                    // atout demande
                    if (isDernierAJouer(pli) || amITheOnlyPlayerWithAtouts(pli)) {
                        // il ne reste plus d'atouts chez les autres ou on est le dernier a jouer
                        if (jeu.getExcuse() != null) {
                            carteJouee = jeu.getExcuse();
                        } else {
                            carteJouee = getCartePourCoupe(pli);
                        }
                    } else {
                        if (pli.isPetitPlayed()) {
                            carteJouee = jeu.getAtoutLePlusFort(pli.getValueMax());
                            if (carteJouee != null
                                    && pli.testCarte(carteJouee.getValeurAbstraite())
                                    && gameDatas.isCarteMaitre(carteJouee)) {
                                // cette carte est maitresse quoiqu'il arrive, on la joue
                            } else {
                                carteJouee = jeu.getAtoutToBeMaitre(pli.getValueMax());
                                // pas d'atout pour etre maitre, on met le plus faible
                                if (carteJouee == null && jeu.getExcuse() != null)
                                    carteJouee = jeu.getExcuse();
                                if (carteJouee == null) {
                                    carteJouee = jeu.getAtoutLePlusFaibleOrmisPetit();
                                }
                                if (carteJouee == null && jeu.getPetit() != null)
                                    carteJouee = jeu.getPetit();
                                if (carteJouee == null) {
                                    carteJouee = jeu.getCarteLaPlusBasse();
                                }

                            }
                        } else {
                            carteJouee = getCartePourCoupe(pli);
                        }
                    }
                }
            }
        }
        lastCardPlayed = carteJouee;
        return carteJouee;
    }

    private CarteTarot playRobotDefense(PliTarot pli) {
        CarteTarot carteJouee = null;
        GameDatas gameDatas = GameDatas.getGameDatas();
        // cas particulier, si il ne lui reste que deux cartes et que l'une d'entre elles est l'excuse il doit le jouer, sinon il la perd
        if (getSizeOfGame() == 2 && jeu.getExcuse() != null) {
            carteJouee = jeu.getExcuse();
        } else {
            if (pli.getCards().isEmpty() || pli.getIdCouleurDemandee() == -1) {
                // premier a jouer ou la premiere carte est l'excuse?
                // on essaye d'ouvrir
                if (!pli.isPreneurPlayed()) {
                    // le preneur n'a pas joue, le but va etre de le faire couper
                    // on essaye de le faire couper, par defaut avec la carte la plus basse du jeu.
                    // V0.14: pas la peine de jouer le petit en premier si je preneur n'a plus d'atouts on essaye de l'emmener au bout
				    /*if (jeu.getPetit() != null && !gameDatas.preneurHasAtouts()){
                        carteJouee = jeu.getPetit();
                    }*/
                    if (gameDatas.isCoupePreneur(jeu.getCarteLaPlusBasse().getIdCouleur())) {
                        carteJouee = jeu.getCarteLaPlusBasse();
                    } else {
                        // cette carte la n'est pas dans sa coupe, on joue ailleurs
                        for (int i = 1; i <= 4; i++) {
                            // est-ce que l'attaquant coupe cette couleur, est-ce qu'on en a et est-ce que la carte est basse?
                            if (jeu.getCarteLaPlusBasseDansCouleur(i) != null
                                    && gameDatas.isCoupePreneur(i)
                                    && jeu.getCarteLaPlusBasseDansCouleur(i).isBasse()) {
                                carteJouee = jeu.getCarteLaPlusBasseDansCouleur(i);
                                break;
                            }
                        }
                        if (carteJouee == null) {
                            carteJouee = jeu.getCarteLaPlusBasse();
                        }
                    }
                    // IL FAUT TESTER LA CARTE SI ELLE EST NULLE, EN PRENDRE UNE AUTRE OU UN ATOUT,
                    // OU L'EXCUSE OU LE PETIT EN DERNIER
                    if (carteJouee.isPetit() && gameDatas.preneurHasAtouts() && jeu.getJeuAtout().size() > 1) {
                        if (jeu.getExcuse() != null) {
                            carteJouee = jeu.getExcuse();
                        } else {
                            // on recupere l'atout le plus faible ormis le petit
                            carteJouee = jeu.getAtoutLePlusFaibleOrmisPetit();
                        }
                    }
                } else {
                    // le preneur a joue l'excuse, on sort les points!
                    if (jeu.getPetit() != null) {
                        carteJouee = jeu.getPetit();
                    } else {
                        carteJouee = jeu.getHonneurLePlusHaut();
                    }
                }
            } else {
                // il y a deja une couleur demandee
                if (isDernierAJouer(pli)) {
                    if (pli.isPreneurMaitre()) {
                        if (pli.getIdCouleurDemandee() == TarotReferentiel.getIdAtout()) {
                            if (pli.getRapportValueSurNbCartes() < 1 && jeu.getExcuse() != null) {
                                carteJouee = jeu.getExcuse();
                            } else {
                                // pli interessant ou pas d'excuse, on coupe
                                carteJouee = getCartePourCoupe(pli);
                            }
                        } else {
                            if (jeu.hasCarteInCouleur(pli.getIdCouleurDemandee())) {
                                // on a du stock
                                carteJouee = jeu.getCarteLaPlusHauteDansCouleur(pli.getIdCouleurDemandee());
                                // est-ce qu'on serait maitre avec la carte la plus haute?
                                if (!pli.testCarte(carteJouee.getValeurAbstraite())) {
                                    // non, donc on va mettre une basse
                                    carteJouee = jeu.getCarteLaPlusBasseDansCouleur(pli.getIdCouleurDemandee());
                                }
                            } else {
                                // on coupe
                                carteJouee = getCartePourCoupe(pli);
                            }
                        }
                    } else {
                        // mettre des points!!!!!
                        if (pli.getIdCouleurDemandee() == TarotReferentiel.getIdAtout()) {
                            carteJouee = jeu.getAtoutToBeMaitre(pli.getValueMax());
                            if (carteJouee == null) {
                                carteJouee = jeu.getAtoutLePlusFaibleOrmisPetit();
                            }
                            if (carteJouee == null) {
                                carteJouee = jeu.getHonneurLePlusHaut();
                            }
                        } else {
                            if (jeu.hasCarteInCouleur(pli.getIdCouleurDemandee())) {
                                carteJouee = jeu.getCarteLaPlusHauteDansCouleur(pli.getIdCouleurDemandee());
                            } else {
                                // on coupe avec le plus petit atout, on est dernier
                                carteJouee = jeu.getAtoutToBeMaitre(pli.getValueMax());
                                if (carteJouee == null) {
                                    carteJouee = jeu.getHonneurLePlusHaut();
                                }
                            }
                        }
                    }
                } else {
                    // On n'est pas dernier a jouer sur ce pli
                    if (pli.getIdCouleurDemandee() == TarotReferentiel.getIdAtout()) {
                        if (pli.getRapportValueSurNbCartes() < 1 && jeu.getExcuse() != null) {
                            carteJouee = jeu.getExcuse();
                        } else {
                            // pli interessant ou pas d'excuse, on coupe
                            carteJouee = getCartePourCoupe(pli);
                        }
                    } else {
                        if (jeu.hasCarteInCouleur(pli.getIdCouleurDemandee())) {
                            if (pli.isPreneurPlayed()) {
                                // on a du stock
                                carteJouee = jeu.getCarteLaPlusHauteDansCouleur(pli.getIdCouleurDemandee());
                                // est-ce qu'on serait maitre avec la carte la plus haute?
                                if (!pli.testCarte(carteJouee.getValeurAbstraite())) {
                                    if (pli.isPreneurMaitre()) {
                                        carteJouee = jeu.getCarteLaPlusBasseDansCouleur(pli.getIdCouleurDemandee());
                                    }
                                }
                            } else {
                                carteJouee = jeu.getCarteLaPlusBasseDansCouleur(pli.getIdCouleurDemandee());
                            }
                        } else {
                            // On coupe, on choisit l'atout qui nous fera maitre du pli
                            carteJouee = jeu.getAtoutToBeMaitre(pli.getValueMax());
                            if (carteJouee == null) {
                                // on ne peut pas etre maitre
                                if (pli.isPreneurPlayed()) {
                                    // le preneur a joue
                                    if (!pli.isPreneurMaitre()
                                            && jeu.getPetit() != null) {
                                        // on  a pas d'atout pour etre maitre et le preneur a joue, il n'est pas maitre.
                                        // on peut donc poser le Petit il est sauf
                                        carteJouee = jeu.getPetit();
                                    }
                                } else {
                                    // le preneur n'a pas joue mais est-ce que la carte qui fait le pli est la plus forte du jeu?
                                    if (gameDatas.isCarteLaPlusForteDuJeu(pli.getValueMax()) && jeu.getPetit() != null)
                                        carteJouee = jeu.getPetit();
                                }
                            } else {
                                if (carteJouee.isPetit() && !pli.isPreneurPlayed() && jeu.getAtoutLePlusFaibleOrmisPetit() != null) {
                                    // danger, on ne pose pas le petit alors qu'on a d'autres atouts et que le preneur n'a pas joue
                                    carteJouee = jeu.getAtoutLePlusFaibleOrmisPetit();
                                }
                            }
                            if (carteJouee == null && jeu.getExcuse() != null) {
                                carteJouee = jeu.getExcuse();
                            }
                            if (carteJouee == null) {
                                carteJouee = jeu.getAtoutLePlusFaibleOrmisPetit();
                            }
                            if (carteJouee == null && jeu.getPetit() != null) {
                                carteJouee = jeu.getPetit();// oblige de jouer le Petit
                            }
                            // pour la suite on pisse
                            if (carteJouee == null && !pli.isPreneurMaitre()
                                    && (pli.isPreneurPlayed() || gameDatas.isCarteLaPlusForteDuJeu(pli.getValueMax()))) {
                                carteJouee = jeu.getHonneurLePlusHaut();
                            }
                            if (carteJouee == null) {
                                carteJouee = jeu.getCarteLaPlusBasse();
                            }
                        }
                    }
                }
            }
        }
        if (carteJouee == null) {
            // dans le doute....
            carteJouee = jeu.getCarteLaPlusBasse();
            if (carteJouee.isPetit() && !pli.isPreneurPlayed() && gameDatas.preneurHasAtouts() && jeu.getJeuAtout().size() > 1) {
                if (jeu.getExcuse() != null) {
                    carteJouee = jeu.getExcuse();
                } else {
                    carteJouee = jeu.getAtoutLePlusFaibleOrmisPetit();
                }
            }
        }
        lastCardPlayed = carteJouee;
        return carteJouee;
    }

    private int getCouleurDejaJoueeEtEnStock() {
        for (int i = 1; i <= 4; i++) {
            if (jeu.hasCarteInCouleur(i) && GameDatas.getGameDatas().isCouleurPlayed(i)) {
                return i;
            }
        }
        return -1;
    }

    private boolean isDernierAJouer(Pli pli) {
        return pli.getCards().size() == 3;
    }

    private CarteTarot getCartePourCoupe(PliTarot pli) {
        CarteTarot carteJouee = jeu.getAtoutToBeMaitre(pli.getValueMax());
        if (carteJouee == null) {
            carteJouee = jeu.getAtoutLePlusFaibleOrmisPetit();
        }
        if (carteJouee == null && jeu.getPetit() != null) {
            carteJouee = jeu.getPetit();
        }
        if (carteJouee == null) {
            carteJouee = jeu.getCarteLaPlusBasse();
        }
        return carteJouee;
    }

    /**
     * Savoir si les joueurs suivants ont des Atouts
     * 21-nb Atouts tombes - nb Atouts en main > 0 oui
     *
     * @param pli
     * @return
     */
    private boolean amITheOnlyPlayerWithAtouts(Pli pli) {
        return jeu.getNbAtouts() == GameDatas.getGameDatas().getNbAtoutRestant();
    }

    @Override
    public List<CarteTarot> gereEcart() {
        List<CarteTarot> ecart = new ArrayList<CarteTarot>();
        int[] couleurs = {-1, -1, -1, -1, -1};
        // TODO il doit se faire un maximum de coupes ou singlettes. sans mettre d'atouts au chien...
        List<CarteTarot> jeuEcart;
        do {
            jeuEcart = jeu.getCartesPourCoupeFranchePourEcart(6 - ecart.size(), couleurs);
            if (jeuEcart != null) {
                ecart.addAll(jeuEcart);
                couleurs[jeuEcart.get(0).getIdCouleur()] = jeuEcart.get(0).getIdCouleur();
            }
        } while (jeuEcart != null && ecart.size() != 6);
        // est-ce qu'il reste des cartes a mettre a l'ecart?
        if (ecart.size() != 6) {
            do {
                jeuEcart = jeu.getCartesPourSinglettePourEcart(6 - ecart.size(), couleurs);
                if (jeuEcart != null) {
                    ecart.addAll(jeuEcart);
                    couleurs[jeuEcart.get(0).getIdCouleur()] = jeuEcart.get(0).getIdCouleur();
                }
            } while (jeuEcart != null && ecart.size() < 6);
        }

        // est-ce qu'il reste des cartes a mettre a l'ecart?
        // sauf que la plus de coupes ou de singlettes, on va retirer les basses en essayant de proteger les longues
        if (ecart.size() != 6) {
            jeuEcart = jeu.getCartesPourEcart(6 - ecart.size(), couleurs);
            ecart.addAll(jeuEcart);
        }
        jeu.removeAll(ecart);
        return ecart;
    }
}
