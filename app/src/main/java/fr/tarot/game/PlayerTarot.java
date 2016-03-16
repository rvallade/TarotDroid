package fr.tarot.game;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.widget.ImageView;
import android.widget.TextView;
import fr.commun.game.Carte;
import fr.commun.game.Player;
import fr.commun.game.Pli;
import fr.commun.utils.Logs;
import fr.tarot.utils.CalculValeurJeu;
import fr.tarot.utils.TarotReferentiel;

public class PlayerTarot extends Player {
	protected JeuTarot jeu = null;
	private boolean preneur;
	private int scoreJeu = 0;
	// par defaut il passe
	private int enchere = TarotReferentiel.getIdPasse();
	private int placeInListeGlobale = -1;
	private int placeInListeOrdonnee = -1;
	private float pointsRealises = 0;
	private boolean premierTour = true;
	private int poignee = 0;
	
	ImageView carteJoueeIV = null;
    TextView nameJoueurTV = null;
	
	public PlayerTarot(int id, String name, int idNextPlayer, int placeInListeGlobale, int placeInListeOrdonnee) {
		super(id, name);
		this.idNextPlayer = idNextPlayer;
		this.placeInListeGlobale = placeInListeGlobale;
		this.placeInListeOrdonnee = placeInListeOrdonnee;
		jeu = new JeuTarot();
	}
	
	public void initFromJson(JSONObject o, Activity activity) throws JSONException {
	    super.initFromJson(o);
	    jeu = new JeuTarot(o.getJSONObject("jeu"));
	    preneur = o.getBoolean("preneur");
	    scoreJeu = o.getInt("scoreJeu");
	    enchere = o.getInt("enchere");
	    placeInListeGlobale = o.getInt("placeInListeGlobale");
	    placeInListeOrdonnee = o.getInt("placeInListeOrdonnee");
	    pointsRealises = (float) o.getDouble("pointsRealises");
	    premierTour = o.getBoolean("premierTour");
	    poignee = o.getInt("poignee");
	    carteJoueeIV.setImageResource(Integer.parseInt(o.getString("carteJoueeIVImage")));
	    nameJoueurTV.setText(name);
	}
	
	public void reInit(){
		preneur = false;
		scoreJeu = 0;
		// par defaut il passe
		enchere = TarotReferentiel.getIdPasse();
		donneur = false;
		jeu = new JeuTarot();
		premierTour = true;
		poignee = 0;
		if (nameJoueurTV!=null) nameJoueurTV.setText(getName());
	}
	
	public void addCarte(CarteTarot carte){
		jeu.add(carte);
	}
	
	public void addCartes(List<Carte> cartes){
		jeu.addAll(cartes);
	}	
	
	public boolean isPreneur() {
		return preneur;
	}

	public void setPreneur(boolean preneur) {
		this.preneur = preneur;
	}

	public int takeDecision(int lastEnchere){
		jeu.ordonne();
		if (isHuman()){
			return takeDecisionHuman(lastEnchere);
		} else {
			return takeDecisionRobot(lastEnchere);
		}
	}
	
	private int takeDecisionHuman(int lastEnchere){
		BufferedReader br;
		int decision = 0;
		boolean error =false;
		
		Logs.info(jeu.toString());
		
		// open up standard input
		br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Quel contrat voulez-vous realiser?");
		System.out.println("\t-1/Passe");
		System.out.println("\t1/Prise");
		System.out.println("\t2/Garde");
		System.out.println("\t3/Garde Sans");
		System.out.println("\t4/Garde Contre");
		
		do {
			try {
				System.out.println("Votre Choix : ");
				decision = Integer.parseInt(br.readLine());
			} catch (Exception e) {
				System.out.println("Erreur dans la saisie!");
				error = true;
			}
		} while(error);
		return decision;
	}
	
	private int takeDecisionRobot(int lastEnchere){
		scoreJeu = CalculValeurJeu.getValeurJeu(jeu);
		//if (score<40) return TarotReferentiel.getIdPasse();
		if (scoreJeu>40 && lastEnchere < TarotReferentiel.getIdPrise()) 
			enchere = TarotReferentiel.getIdPrise();
		if (scoreJeu>56 && lastEnchere < TarotReferentiel.getIdGarde())
			enchere = TarotReferentiel.getIdGarde();
		// apres rentrent en ligne de compte les coupes et les singlettes
		if (enchere == TarotReferentiel.getIdGarde()){
			scoreJeu = CalculValeurJeu.getValeurJeuForGSOrGC(jeu);
			if (scoreJeu>71 && lastEnchere < TarotReferentiel.getIdGardeSans())
				enchere = TarotReferentiel.getIdGardeSans();
			if (scoreJeu>80 && lastEnchere < TarotReferentiel.getIdGardeContre())
				enchere = TarotReferentiel.getIdGardeContre();
		}
		Logs.info(getName() + ", valeur du jeu : " + scoreJeu + " ==> " + TarotReferentiel.getContrat(enchere).getName());
		return enchere;
	}
	
	public int hasPoignee(){
        // presentation des poignees ou du petit sec
        if (jeu.getNbAtouts()>=15){
            // triple poignee
            poignee = 3;
        } else if (jeu.getNbAtouts()>=13){
            // double poignee
            poignee = 2;
        } else if (jeu.getNbAtouts()>=10){
            // simple poignee
            poignee = 1;
        }
        return poignee;
	}
	public List<Carte> getCartesPoignee(){
	    List<Carte> poignee = new ArrayList<Carte>();
	    int nbAtouts = 0;
        int index = 0;
        switch (hasPoignee()) {
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
	    while (index!=nbAtouts){
            poignee.add(jeu.getJeuAtout().get(jeu.getJeuAtout().size()-1-index));
            index++;
        }
	    Collections.reverse(poignee);
	    return poignee;
	}
	public CarteTarot play(PliTarot pli){
		// TODO Ne pas permettre au joueur de commencer par le petit
		// TODO Si l'excuse a ete jouee en premier alors le joueur peut jouer ce qu'il veut
		// si le joueur est le premier il doit choisir la couleur qu'il veut jouer
		// TODO voir si on ne peut pas travailler avec la carte r�cup�r�e au d�but et essayer de mutualiser le code.....
		if (isHuman()){
			return playHuman(pli);
		} else {
			/*if (premierTour){
				hasPoignee();
				gerePoignees();
				premierTour = false;
			}*/
			//playRobot(pli);
			if (isPreneur()){
			    return playRobotAttaque(pli);
			} else {
			    return playRobotDefense(pli);
			}
		}
	}
	
	private CarteTarot playRobotAttaque(PliTarot pli) {
		CarteTarot carteJouee = null;
		// cas particulier, si il ne lui reste que deux cartes et que l'une d'entre elles est l'excuse il doit le jouer, sinon il la perd
		if (getSizeOfGame() == 2 && jeu.getExcuse() != null) {
			carteJouee = jeu.getExcuse();
		} else {
			if (pli.getCards().isEmpty() || pli.getIdCouleurDemandee() == -1){
				// premier a jouer ou la premiere carte est l'excuse?
			    // on va regarder si il a une longue. Si oui il va jouer dans la longue, honneur en tete
			    // si il en a.
				if (jeu.getJeuLongue() != null && jeu.getJeuLongue().size() != 0){
				    if (GameDatas.getGameDatas().getNbTourACouleur(jeu.getJeuLongue().get(0).getIdCouleur()) == 0
				            && "R".equals(jeu.getJeuLongue().get(jeu.getJeuLongue().size()-1).getValeurFaciale())){
				        carteJouee = jeu.getJeuLongue().get(jeu.getJeuLongue().size()-1);
				    } else if(GameDatas.getGameDatas().getNbTourACouleur(jeu.getJeuLongue().get(0).getIdCouleur()) == 1
                            && "D".equals(jeu.getJeuLongue().get(jeu.getJeuLongue().size()-1).getValeurFaciale())
                            && GameDatas.getGameDatas().isCarteMaitre(jeu.getJeuLongue().get(jeu.getJeuLongue().size()-1))){
				        // on tente la dame
				        carteJouee = jeu.getJeuLongue().get(jeu.getJeuLongue().size()-1);
				    } else {
                        carteJouee = jeu.getJeuLongue().get(0);
                        // cela peut etre une carte a points qui n'est pas maitre, on doit tester
                        if (!carteJouee.isBasse() && !GameDatas.getGameDatas().isCarteMaitre(carteJouee)) {
                            // ne pas jouer la carte si elle n'est pas potentiellement maitre
                            int idCouleur = getCouleurDejaJoueeEtEnStock();
                            // on va jouer une couleur qui a deja ete jouee
                            if (idCouleur != -1){
                                carteJouee = jeu.getJeuCouleur(idCouleur).get(0);
                            }
                            if (carteJouee == null || !carteJouee.isBasse() && !GameDatas.getGameDatas().isCarteMaitre(carteJouee)) {
                                // on va jouer Atout ou ouvrir une couleur
                                // pas d'atout pour etre maitre, on met le plus faible
                                carteJouee = jeu.getAtoutLePlusFaibleOrmisPetit();
                                if (carteJouee == null || carteJouee.isExcuse()){
                                    carteJouee = jeu.getCarteLaPlusBasse();
                                }
                            }
                        }
                    }
				} else {
					int idCouleur = getCouleurDejaJoueeEtEnStock();
					// on va jouer une couleur qui a deja ete jouee
					if (idCouleur != -1){
						carteJouee = jeu.getJeuCouleur(idCouleur).get(0);
					}
					if (carteJouee == null || !carteJouee.isBasse() && !GameDatas.getGameDatas().isCarteMaitre(carteJouee)) {
					    // on va jouer Atout ou ouvrir une couleur
                        // pas d'atout pour etre maitre, on met le plus faible
                        carteJouee = jeu.getAtoutLePlusFaibleOrmisPetit();
                        if (carteJouee == null || carteJouee.isExcuse()){
                            carteJouee = jeu.getCarteLaPlusBasse();
                        }
					}
				}
			} else {
				if (pli.getIdCouleurDemandee() != TarotReferentiel.getIdAtout()){
					// couleur demandee, pas atout
					if (jeu.hasCarteInCouleur(pli.getIdCouleurDemandee())){
						// couleur en stock
						if (isDernierAJouer(pli)){
							// on doit mettre ce qu'il faut pour etre maitre, sinon on met une petite
							carteJouee = jeu.getCarteLaPlusHauteDansCouleur(pli.getIdCouleurDemandee());
							if (!pli.testCarte(carteJouee.getValeurAbstraite()) || GameDatas.getGameDatas().isCarteMaitre(carteJouee)){
								carteJouee = jeu.getCarteLaPlusBasseDansCouleur(pli.getIdCouleurDemandee());
							}
						} else {
						    // il n'est pas le dernier a jouer mais si il a le roi et que c'est le premier tour a cette couleur il doit le poser.
						    // pareil si c'est la dame et que c'est au deuxieme tour et qu'elle est maitre
						    if (GameDatas.getGameDatas().getNbTourACouleur(pli.getIdCouleurDemandee()) == 0
						            && "R".equals(jeu.getCarteLaPlusHauteDansCouleur(pli.getIdCouleurDemandee()).getValeurFaciale())){
						        carteJouee = jeu.getCarteLaPlusHauteDansCouleur(pli.getIdCouleurDemandee());
						    } else if (GameDatas.getGameDatas().getNbTourACouleur(pli.getIdCouleurDemandee()) == 1
                                    && "D".equals(jeu.getCarteLaPlusHauteDansCouleur(pli.getIdCouleurDemandee()).getValeurFaciale())
                                    && pli.testCarte(jeu.getCarteLaPlusHauteDansCouleur(pli.getIdCouleurDemandee()).getValeurAbstraite())
                                    && GameDatas.getGameDatas().isCarteMaitre(jeu.getCarteLaPlusHauteDansCouleur(pli.getIdCouleurDemandee()))){
                                carteJouee = jeu.getCarteLaPlusHauteDansCouleur(pli.getIdCouleurDemandee());
                            } else {
                                carteJouee = jeu.getCarteLaPlusBasseDansCouleur(pli.getIdCouleurDemandee());
                            }
						}
					} else {
						// il n'a pas la couleur demand�e, il doit couper
						if (isDernierAJouer(pli)){
							// on pose le petit si on l'a.
							if (jeu.getPetit()!=null && pli.testCarte(jeu.getPetit().getValeurAbstraite())){
								carteJouee = jeu.getPetit();
							} else {
								if (pli.getRapportValueSurNbCartes()>1){
									carteJouee = jeu.getAtoutToBeMaitre(pli.getValueMax());
									if (carteJouee == null) {
										// on ne peut pas �tre maitre, on met un atout plus petit
										carteJouee = jeu.getAtoutLePlusFaibleOrmisPetit();
									}
									if (carteJouee == null && jeu.getExcuse() != null) carteJouee = jeu.getExcuse();
									// en dernier recours....
									if (carteJouee == null && jeu.getPetit() != null) carteJouee = jeu.getPetit();
									// on pisse...
									if (carteJouee == null){
										carteJouee = jeu.getCarteLaPlusBasse();
									}
								} else {
									if (jeu.getExcuse()!=null){
										carteJouee = jeu.getExcuse();
									} else {
										carteJouee = jeu.getAtoutToBeMaitre(pli.getValueMax());
										// pas d'atout pour etre maitre, on met le plus faible
										if (carteJouee == null) {
											carteJouee = jeu.getAtoutLePlusFaibleOrmisPetit();
										}
										if (carteJouee == null && jeu.getPetit() != null) carteJouee = jeu.getPetit();
										if (carteJouee == null){
											carteJouee = jeu.getCarteLaPlusBasse();
										}
									}
								}
							}
						} else {
							// Pli interessant ou pas?
							if (pli.getRapportValueSurNbCartes()>1 || pli.isPetitPlayed()){
								carteJouee = jeu.getAtoutLePlusFort(pli.getValueMax());
								if (carteJouee!=null 
										&& pli.testCarte(carteJouee.getValeurAbstraite()) 
										&& GameDatas.getGameDatas().isCarteMaitre(carteJouee)){
									// cette carte est maitresse quoiqu'il arrive, on la joue
								} else {
									carteJouee = jeu.getAtoutToBeMaitre(pli.getValueMax());
									// pas d'atout pour etre maitre, on met le plus faible
									if (carteJouee == null && jeu.getExcuse() != null) carteJouee = jeu.getExcuse();
									if (carteJouee == null) {
										carteJouee = jeu.getAtoutLePlusFaibleOrmisPetit();
									}
									if (carteJouee == null && jeu.getPetit() != null) carteJouee = jeu.getPetit();
									if (carteJouee == null){
										carteJouee = jeu.getCarteLaPlusBasse();
									}
									
								}
							} else {
								if (jeu.getExcuse()!=null){
									carteJouee = jeu.getExcuse();
								} else {
									carteJouee = jeu.getAtoutToBeMaitre(pli.getValueMax());
									// pas d'atout pour etre maitre, on met le plus faible
									if (carteJouee == null) {
										carteJouee = jeu.getAtoutLePlusFaibleOrmisPetit();
									}
									if (carteJouee == null && jeu.getPetit() != null) carteJouee = jeu.getPetit();
									if (carteJouee == null){
										carteJouee = jeu.getCarteLaPlusBasse();
									}
								}
							}
						}
					}
				}  else {
					// atout demand�
					if (isDernierAJouer(pli) || nextPlayersHaveNoAtouts(pli)){
						// il ne reste plus d'atouts chez les autres ou on est le dernier � jouer
						if (jeu.getExcuse()!=null){
							carteJouee = jeu.getExcuse();
						} else {
							carteJouee = jeu.getAtoutToBeMaitre(pli.getValueMax());
							// pas d'atout pour etre maitre, on met le plus faible
							if (carteJouee == null) {
								carteJouee = jeu.getAtoutLePlusFaibleOrmisPetit();
							}
							if (carteJouee == null && jeu.getPetit() != null) carteJouee = jeu.getPetit();
							if (carteJouee == null){
								carteJouee = jeu.getCarteLaPlusBasse();
							}
						}
					} else {
						if (pli.isPetitPlayed()){
							carteJouee = jeu.getAtoutLePlusFort(pli.getValueMax());
							if (carteJouee!=null 
									&& pli.testCarte(carteJouee.getValeurAbstraite()) 
									&& GameDatas.getGameDatas().isCarteMaitre(carteJouee)){
								// cette carte est maitresse quoiqu'il arrive, on la joue
							} else {
								carteJouee = jeu.getAtoutToBeMaitre(pli.getValueMax());
								// pas d'atout pour etre maitre, on met le plus faible
								if (carteJouee == null && jeu.getExcuse() != null) carteJouee = jeu.getExcuse();
								if (carteJouee == null) {
									carteJouee = jeu.getAtoutLePlusFaibleOrmisPetit();
								}
								if (carteJouee == null && jeu.getPetit() != null) carteJouee = jeu.getPetit();
								if (carteJouee == null){
									carteJouee = jeu.getCarteLaPlusBasse();
								}
								
							}
						} else {
							carteJouee = jeu.getAtoutToBeMaitre(pli.getValueMax());
							// pas d'atout pour etre maitre, on met le plus faible
							if (carteJouee == null) {
								carteJouee = jeu.getAtoutLePlusFaibleOrmisPetit();
							}
							if (carteJouee == null && jeu.getPetit() != null) carteJouee = jeu.getPetit();
							if (carteJouee == null){
								carteJouee = jeu.getCarteLaPlusBasse();
							}
						}
					}
				}
			}
		}	
		return carteJouee;
		//playCard(pli, carteJouee);
		//on retire la carte de la main du joueur
		//jeu.remove(carteJouee);
	}
	
	private CarteTarot playRobotDefense(PliTarot pli){
		CarteTarot carteJouee = null;
		// cas particulier, si il ne lui reste que deux cartes et que l'une d'entre elles est l'excuse il doit le jouer, sinon il la perd
		if (getSizeOfGame() == 2 && jeu.getExcuse() != null) {
			carteJouee = jeu.getExcuse();
		} else {
			if (pli.getCards().isEmpty() || pli.getIdCouleurDemandee() == -1){
				// premier a jouer ou la premiere carte est l'excuse?
				// on essaye d'ouvrir
				if (!pli.isPreneurPlayed()){
				    // le preneur n'a pas joue, le but va etre de le faire couper
                    // on essaye de le faire couper, par defaut avec la carte la plus basse du jeu.
                    // V0.14: pas la peine de jouer le petit en premier si je preneur n'a plus d'atouts on essaye de l'emmener au bout
				    /*if (jeu.getPetit() != null && !GameDatas.getGameDatas().preneurHasAtouts()){
                        carteJouee = jeu.getPetit();
                    }*/
                    if (carteJouee == null && GameDatas.getGameDatas().isCoupePreneur(jeu.getCarteLaPlusBasse().getIdCouleur())){
                        carteJouee = jeu.getCarteLaPlusBasse();
                    } else {
                        // cette carte l� n'est pas dans sa coupe, on joue ailleurs
                        if (carteJouee == null){
                            for (int i=1;i<=4;i++){
                                // est-ce que l'attaquant coupe cette couleur, est-ce qu'on en a et est-ce que la carte est basse?
                                if (jeu.getCarteLaPlusBasseDansCouleur(i)!=null 
										&& GameDatas.getGameDatas().isCoupePreneur(i)
										&& jeu.getCarteLaPlusBasseDansCouleur(i).isBasse()){
                                    carteJouee = jeu.getCarteLaPlusBasseDansCouleur(i);
                                    break;
                                }
                            }
                        }
                        if (carteJouee == null){
                            carteJouee = jeu.getCarteLaPlusBasse();
                        }
                    }
					// IL FAUT TESTER LA CARTE SI ELLE EST NULLE, EN PRENDRE UNE AUTRE OU UN ATOUT, 
					// OU L'EXCUSE OU LE PETIT EN DERNIER
					if (carteJouee.isPetit() && GameDatas.getGameDatas().preneurHasAtouts() && jeu.getJeuAtout().size()>1){
						if(jeu.getExcuse() != null) {
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
				// il y a d�j� une couleur demand�e
				if (isDernierAJouer(pli)){
					if (pli.isPreneurMaitre()){
						if (pli.getIdCouleurDemandee()==TarotReferentiel.getIdAtout()){
							if (pli.getRapportValueSurNbCartes()<1 && jeu.getExcuse()!=null){
								carteJouee = jeu.getExcuse();
							} else {
								// pli interessant ou pas d'excuse, on coupe
								carteJouee = jeu.getAtoutToBeMaitre(pli.getValueMax());
								if (carteJouee == null) carteJouee = jeu.getAtoutLePlusFaibleOrmisPetit();
								if (carteJouee == null && jeu.getPetit()!=null) carteJouee = jeu.getPetit();
								if (carteJouee == null) carteJouee = jeu.getCarteLaPlusBasse();
							}
						} else {
							if (jeu.hasCarteInCouleur(pli.getIdCouleurDemandee())){
								// on a du stock
								carteJouee = jeu.getCarteLaPlusHauteDansCouleur(pli.getIdCouleurDemandee());
								// est-ce qu'on serait maitre avec la carte la plus haute?
								if (!pli.testCarte(carteJouee.getValeurAbstraite())){
									// non, donc on va mettre une basse
									carteJouee = jeu.getCarteLaPlusBasseDansCouleur(pli.getIdCouleurDemandee());
								}
							} else {
								// on coupe
								carteJouee = jeu.getAtoutToBeMaitre(pli.getValueMax());
								if (carteJouee == null) carteJouee = jeu.getAtoutLePlusFaibleOrmisPetit();
								if (carteJouee == null && jeu.getPetit()!=null) carteJouee = jeu.getPetit();
								if (carteJouee == null) carteJouee = jeu.getCarteLaPlusBasse();
							}
						}
					} else {
						// mettre des points!!!!!
						if (pli.getIdCouleurDemandee()==TarotReferentiel.getIdAtout()){
							carteJouee = jeu.getAtoutToBeMaitre(pli.getValueMax());
							if (carteJouee == null) carteJouee = jeu.getAtoutLePlusFaibleOrmisPetit();
							if (carteJouee == null) carteJouee = jeu.getHonneurLePlusHaut();
						} else {
							if (jeu.hasCarteInCouleur(pli.getIdCouleurDemandee())){
								carteJouee = jeu.getCarteLaPlusHauteDansCouleur(pli.getIdCouleurDemandee());
							} else {
								// on coupe avec le plus petit atout, on est dernier
								carteJouee = jeu.getAtoutToBeMaitre(pli.getValueMax());
								if (carteJouee == null) carteJouee = jeu.getHonneurLePlusHaut();
							}
						}
					}
				} else {
					// On n'est pas dernier � jouer sur ce pli
					if (pli.getIdCouleurDemandee()==TarotReferentiel.getIdAtout()){
						if (pli.getRapportValueSurNbCartes()<1 && jeu.getExcuse()!=null){
							carteJouee = jeu.getExcuse();
						} else {
							// pli interessant ou pas d'excuse, on coupe
							carteJouee = jeu.getAtoutToBeMaitre(pli.getValueMax());
							if (carteJouee == null) carteJouee = jeu.getAtoutLePlusFaibleOrmisPetit();
							if (carteJouee == null && jeu.getPetit()!=null) carteJouee = jeu.getPetit();
							if (carteJouee == null) carteJouee = jeu.getCarteLaPlusBasse();
						}
					} else {
						if (jeu.hasCarteInCouleur(pli.getIdCouleurDemandee())){
							if (pli.isPreneurPlayed()){
								// on a du stock
								carteJouee = jeu.getCarteLaPlusHauteDansCouleur(pli.getIdCouleurDemandee());
								// est-ce qu'on serait maitre avec la carte la plus haute?
								if (!pli.testCarte(carteJouee.getValeurAbstraite())){
									if (pli.isPreneurMaitre()){
										carteJouee = jeu.getCarteLaPlusBasseDansCouleur(pli.getIdCouleurDemandee());
									}
								}
							} else {
								carteJouee = jeu.getCarteLaPlusBasseDansCouleur(pli.getIdCouleurDemandee());
							}
						} else {
                            // On coupe, on choisit l'atout qui nous fera maitre du pli
                            carteJouee = jeu.getAtoutToBeMaitre(pli.getValueMax());
                            if (carteJouee == null){
                                // on ne peut pas �tre maitre
                                if (pli.isPreneurPlayed()){
                                    // le preneur a joue
                                    if (!pli.isPreneurMaitre()
                                            && jeu.getPetit()!=null){
                                        // on  a pas d'atout pour etre maitre et le preneur a joue, il n'est pas maitre. 
                                        // on peut donc poser le Petit il est sauf
                                        carteJouee = jeu.getPetit();
                                    }
                                } else {
                                    // le preneur n'a pas joue mais est-ce que la carte qui fait le pli est la plus forte du jeu?
                                    if (GameDatas.getGameDatas().isCarteLaPlusForteDuJeu(pli.getValueMax()) && jeu.getPetit()!=null) 
										carteJouee = jeu.getPetit();
                                }
                            } else {
                                if (carteJouee.isPetit() && !pli.isPreneurPlayed() && jeu.getAtoutLePlusFaibleOrmisPetit()!=null){
                                    // danger, on ne pose pas le petit alors qu'on a d'autres atouts et que le preneur n'a pas joue
                                    carteJouee = jeu.getAtoutLePlusFaibleOrmisPetit();
                                }
                            }
                            if (carteJouee == null  && jeu.getExcuse()!=null) carteJouee = jeu.getExcuse();
                            if (carteJouee == null) carteJouee = jeu.getAtoutLePlusFaibleOrmisPetit();
                            if (carteJouee == null && jeu.getPetit()!=null) carteJouee = jeu.getPetit();// oblig� de jouer le Petit
                            // pour la suite on pisse
                            if (carteJouee == null && !pli.isPreneurMaitre()
                                    && (pli.isPreneurPlayed() || GameDatas.getGameDatas().isCarteLaPlusForteDuJeu(pli.getValueMax()))){
                                carteJouee = jeu.getHonneurLePlusHaut();
                            }
                            if (carteJouee == null) carteJouee = jeu.getCarteLaPlusBasse();
						}
					}
				}
			}
		}
		if (carteJouee==null){
		    // dans le doute....
			carteJouee = jeu.getCarteLaPlusBasse();
            if (carteJouee.isPetit() && !pli.isPreneurPlayed() && GameDatas.getGameDatas().preneurHasAtouts() && jeu.getJeuAtout().size()>1){
                if(jeu.getExcuse()!=null) {
                    carteJouee = jeu.getExcuse();
                } else {
                    carteJouee = jeu.getAtoutLePlusFaibleOrmisPetit();
                }
            }
		}
		return carteJouee;
		//playCard(pli, carteJouee);
		//on retire la carte de la main du joueur
		//jeu.remove(carteJouee);
	}
	
	public void playCard(PliTarot pli, CarteTarot carte){
		// on ajoute la carte au pli
		pli.playCard(carte, this);
		Logs.info(getName() + " joue le " + carte.toString());
		played = true;
		jeu.remove(carte);
	}

	private CarteTarot playHuman(PliTarot pli){
		// TODO g�rer les poignees pour les humains
		CarteTarot card = null;
		String indexCarte = null;
		BufferedReader br;
		boolean error = false;
		System.out.println(jeu.toString());
		int index = 0;
		do {
			System.out.println("Quelle carte allez vous jouer? : ");

			// open up standard input
			br = new BufferedReader(new InputStreamReader(System.in));
			
			try {
				error = false;
				indexCarte = br.readLine();
			} catch (Exception e) {
				System.out.println("Erreur dans la saisie!");
				error = true;
			}
			try {
				index = Integer.parseInt(indexCarte);
			} catch (NumberFormatException nfe) {
				System.out.println("La saisie n'est pas un entier correct!");
				error = true;
			}
			
			if (index <0 || index>getSizeOfGame()) {
				System.out.println("La saisie n'est pas correcte, v�rifiez la position!");
				error = true;
			}
			
			if (!error){
				// on a la carte, on la r�cup�re
				card = (CarteTarot) jeu.getCard(index);
				if(!testCarte(card, pli)){
					System.out.println("Vous ne pouvez pas jouer cette carte!");
					error = true;
				}
			}
		} while (error);
		
		// TODO par la suite essayer de mutualiser le code de v�rification avec celui pour le joueur robot?
		return card;
		//playCard(pli, card);
		//jeu.remove(card);
	}
	
	/**
	 * Ajoute les points de la partie en cours 
	 * @param points
	 */
	public void addPoints(float points){
		pointsRealises += points;
	}
	
	// TODO A pr�voir : ne pr�senter que les cartes qui peuvent �tre jouees, dans une version graphique, griser les cartes qui ne peuvent pas �tre jouees?
	/**
	 * M�thode qui va tester la carte jouee par l'humain
	 * Se r�f�rer au fichier Models.pptx dans le r�pertoire "docs" pour trouver les sch�mas
	 * @param carte
	 * @param pli
	 * @return
	 */
	public boolean testCarte(CarteTarot carte, PliTarot pli){
		// l'excuse peut �tre jouee � tout moment
		if (carte.isExcuse()) return true;
		// si il n'y a pas de couleur en cours, on peut jouer ce qu'on veut
		if (pli.getIdCouleurDemandee() == -1) return true;
		if (carte.getIdCouleur() != pli.getIdCouleurDemandee()) {
			if (jeu.getJeuCouleur(pli.getIdCouleurDemandee()).size() >0){
				// il lui reste de la couleur il doit la jouer.
				return false;
			} else {
				// il n'a pas de la couleur demand�e
				if (pli.getIdCouleurDemandee() != TarotReferentiel.getIdAtout()){
					// pour un pli non atouts
					if (carte.getIdCouleur() != TarotReferentiel.getIdAtout()){
						// il pisse...
						if (jeu.getNbAtouts() >0
								&& !(jeu.getNbAtouts()==1 
									&& jeu.getExcuse()!=null)) 
							return false;
						GameDatas.getGameDatas().setPlayerHasNoMoreAtouts(idPlayer);
						if (preneur){
							GameDatas.getGameDatas().preneurHasNoMoreAtouts();
						}
					} else {
						// il coupe
						return testAtout(carte, pli);
					}
				}
			}
		} else {
			if (preneur){
				GameDatas.getGameDatas().getCoupesPreneur()[pli.getIdCouleurDemandee()] = false;
			}
			if (pli.getIdCouleurDemandee() == TarotReferentiel.getIdAtout()){
				return testAtout(carte, pli);
			}
		}
		return true;
	}
	
	/**
	 * Methode qui va tester si l'atout joue est valable :
	 * _ est-ce qu'il est plus fort que le plus fort du pli?
	 * _ dans le cas contraire est-ce qu'il dispose d'un atout plus fort que le plus fort du pli? 
	 * @param carte
	 * @param pli
	 * @return
	 */
	private boolean testAtout(CarteTarot carte, PliTarot pli){
		CarteTarot bestAtout = jeu.getJeuAtout().get(jeu.getNbAtouts()-1);
		if (carte.getValeurAbstraite()< pli.getValueMax() 
				&& pli.getValueMax()<bestAtout.getValeurAbstraite()){
			return false;
		}
		GameDatas.getGameDatas().setCoupe(idPlayer, pli.getIdCouleurDemandee());
		if (preneur){
			GameDatas.getGameDatas().getCoupesPreneur()[pli.getIdCouleurDemandee()] = true;
		}
		return true;
	}
	
	/**
	 * Methode qui va g�rer les poignees
	 */
	private void gerePoignees(){
		int nbAtouts = 0;
		String texte = "";
		StringBuilder message = null;
		if (poignee>0){
			// on doit choisir combien d'atout afficher
			switch (poignee) {
			case 1:
				nbAtouts = 10;
				texte = "simple";
				break;
			case 2:
				nbAtouts = 13;
				texte = "double";
				break;
			case 3:
				nbAtouts = 15;
				texte = "triple";
				break;
			}
			message = new StringBuilder(getName());
			message.append(" pr�sente une ");
			message.append(texte);
			message.append(" poignee : ");
			// on pr�sente du plus fort au plus faible
			int index = 0;
			Carte carte = null;
			while (index!=nbAtouts){
				carte = jeu.getJeuAtout().get(jeu.getJeuAtout().size()-1-index);
				message.append(carte.getValeurFaciale());
				message.append(";");
				index++;
			}
			Logs.info(message.toString());
		}
	}
	
	public List<CarteTarot> gereChien(){
		if (isHuman()){
			return gereChienHuman();
		} else {
			return gereChienRobot();
		}
	}
	private List<CarteTarot> gereChienHuman(){
		BufferedReader br;
		String indexCarte = null;
		List<CarteTarot> liste = new ArrayList<CarteTarot>();
		int index = -1;
		boolean error = false;
		int nbCarte = 0;
		do {
			if (liste != null){
				System.out.println("Voici votre �cart : ");
				for (Carte carte:liste){
					System.out.print(carte.toShortString());
					System.out.print(";");
				}
			}
			System.out.println("Voici votre jeu :");
			System.out.println(jeu.toString());
			System.out.println("Quelles carte voulez vous �carter?");
			System.out.println("carte ");
			System.out.print(nbCarte);
			System.out.print(" : ");

			// open up standard input
			br = new BufferedReader(new InputStreamReader(System.in));
			
			try {
				error = false;
				indexCarte = br.readLine();
			} catch (Exception e) {
				System.out.println("Erreur dans la saisie!");
				error = true;
			}
			try {
				index = Integer.parseInt(indexCarte);
			} catch (NumberFormatException nfe) {
				System.out.println("La saisie n'est pas un entier correct!");
				error = true;
			}
			
			if (index <0 || index>getSizeOfGame()) {
				System.out.println("La saisie n'est pas correcte, v�rifiez la position!");
				error = true;
			}
			
			if (liste.contains(jeu.getCard(index))){
				System.out.println("Cette carte est d�j� �cart�e!");
				error = true;
			}
			
			if (!error){
				liste.add((CarteTarot) jeu.getCard(index));
				nbCarte++;
			}
		} while (error || nbCarte!=6);
		jeu.removeAll(liste);
		return liste;
	}
	private List<CarteTarot> gereChienRobot(){
		List<CarteTarot> ecart = new ArrayList<CarteTarot>();
		int[] couleurs = {-1,-1,-1,-1,-1};
		// TODO il doit se faire un maximum de coupes ou singlettes. sans mettre d'atouts au chien...
		List<CarteTarot> jeuEcart;
		do{ 
			jeuEcart = jeu.getCartesPourCoupeFranchePourEcart(6-ecart.size(), couleurs);
			if (jeuEcart!=null){
				ecart.addAll(jeuEcart);
				couleurs[jeuEcart.get(0).getIdCouleur()] = jeuEcart.get(0).getIdCouleur();
			}
		} while (jeuEcart!=null && ecart.size()!=6);
		// est-ce qu'il reste des cartes � mettre � l'�cart?
		if (ecart.size()!=6){
			do{
				jeuEcart = jeu.getCartesPourSinglettePourEcart(6-ecart.size(), couleurs);
				if (jeuEcart!=null){
					ecart.addAll(jeuEcart);
					couleurs[jeuEcart.get(0).getIdCouleur()] = jeuEcart.get(0).getIdCouleur();
				}
			} while (jeuEcart!=null && ecart.size()<6);
		}
		//if (ecart.size()>6){
		//	System.out.print("sdfqsdfqsdfqsdf");
		//}
		// est-ce qu'il reste des cartes � mettre � l'�cart?
		// sauf que l� plus de coupes ou de singlettes, on va retirer les basses en essayant de proteger les longues
		if (ecart.size()!=6){
			jeuEcart = jeu.getCartesPourEcart(6-ecart.size(), couleurs);
			ecart.addAll(jeuEcart);
		}
		jeu.removeAll(ecart);
		return ecart;
	}
	
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
	 * @return
	 */
	public int getEnchere() {
		return enchere;
	}
	public int getPoignee() {
		return poignee;
	}
	
	public boolean hasNoMoreCard(){
		return jeu.getSizeOfGame()==0;
	}
	public int getSizeOfGame(){
		return jeu.getSizeOfGame();
	}
	public JeuTarot getJeu(){
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
	
	private boolean isDernierAJouer(Pli pli){
		return pli.getCards().size()==3;
	}
	
	/**
	 * Savoir si les joueurs suivants ont des Atouts
	 * 21-nb Atouts tomb�s - nb Atouts en main >0 oui
	 * @param pli
	 * @return
	 */
	private boolean nextPlayersHaveNoAtouts(Pli pli){
		return jeu.getNbAtouts()==GameDatas.getGameDatas().getNbAtoutRestant();
	}
	
	private int getCouleurDejaJoueeEtEnStock(){
		for (int i=1;i<=4;i++){
			if (jeu.hasCarteInCouleur(i) 
					&& GameDatas.getGameDatas().isCouleurPlayed(i)) return i;
		}
		return -1;
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
    public float getPointsRealises() {
        return pointsRealises;
    }
    public void setEnchere(int enchere) {
        this.enchere = enchere;
    }
    public void setPointsRealises(float pointsRealises) {
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
}
