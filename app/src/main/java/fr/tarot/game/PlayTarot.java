package fr.tarot.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.commun.game.Carte;
import fr.commun.utils.Logs;
import fr.tarot.utils.CompteurPoints;
import fr.tarot.utils.TarotReferentiel;

public class PlayTarot {
	private List<Carte> chien = null;
	private ListePlayerTarot listePlayerTarotFlottante = null;
	private List<Carte> plisAttaque = null;
	private List<Carte> plisDefense = null;
	private float pointsRealises = 0;
	private int donneur = -1;
	private boolean defenseOwsAttaque = false, attaqueOwsDefense = false;
	private int pointsPoignees = 0;
	private boolean petitBoutDefense = false;
	private boolean petitBoutAttaque = false;
	
	public PlayTarot (List<PlayerTarot> listePlayerTarot){
		//init();
		plisAttaque = new ArrayList<Carte>();
		plisDefense = new ArrayList<Carte>();
		//afficheJeuComplet("JEU ENTIER AVEC MELANGE : ");
		distribution(listePlayerTarot);
		//alimentation de la liste flottante, le premier de la liste est celui qui est juste apres le donneur
		listePlayerTarotFlottante = new ListePlayerTarot();
		for (int i=0;i<listePlayerTarot.size();i++){
			PlayerTarot player = listePlayerTarot.get(i);
			if (player.isDonneur()){
				donneur = i;
				Logs.info(player.getName() + " est donneur.");
			}
		}
		int indice = donneur;
		do {
			indice++;
			if (indice == listePlayerTarot.size()) indice = 0;
			listePlayerTarotFlottante.add(listePlayerTarot.get(indice));
			Logs.debug(listePlayerTarot.get(indice).getName() + " ajoute dans la liste flottante");
		} while (indice != donneur);
	}

	private void distribution(List<PlayerTarot> listePlayerTarot) {
		Logs.debug("DISTRIBUTION");
		// on commence par le chien
		//TODO : calculer le nombre de cartes a donner en fonction du nombre de joueurs?
		chien = new ArrayList<Carte>();
		for (int i=0;i<6;i++){
			Logs.debug("Au chien : " + TarotReferentiel.getGame().get(i).toString());
			chien.add(TarotReferentiel.getGame().get(i));
		}
		int indexCard = 6;
		for (PlayerTarot playerTarot:listePlayerTarot) {
			playerTarot.addCartes(TarotReferentiel.getGame().subList(indexCard, indexCard+18));
			Logs.info(playerTarot.getName() + " : " + playerTarot.getJeu().toString());
			indexCard+=18;
		}
	}
	
	public void play() {
		int enchere =  TarotReferentiel.getIdPasse();
		int lastEnchere = TarotReferentiel.getIdPasse();
		int idPreneur = -1;
		// TODO Tenir compte du donneur pour les encheres, le premier a parler est celui qui est juste apres
		for (int z=0;z<listePlayerTarotFlottante.size();z++) {
			PlayerTarot playerTarot = listePlayerTarotFlottante.get(z);
			enchere = playerTarot.takeDecision(lastEnchere);
			Logs.info(playerTarot.getName() + " annonce " + TarotReferentiel.getContrat(enchere).getName());
			if (enchere>lastEnchere){
				idPreneur = z;
				lastEnchere = enchere;
			}
		}
		
		if (lastEnchere != TarotReferentiel.getIdPasse()){
			listePlayerTarotFlottante.setPreneur(idPreneur);
			// que faire du chien?
			if (lastEnchere==TarotReferentiel.getIdPrise() || lastEnchere==TarotReferentiel.getIdGarde()){
				listePlayerTarotFlottante.getPreneur().addCartes(chien);
				Logs.info(listePlayerTarotFlottante.getPreneur().getName() + " : " + listePlayerTarotFlottante.getPreneur().getJeu().toString());
				plisAttaque.addAll(listePlayerTarotFlottante.getPreneur().gereChien());
			} else if (lastEnchere==TarotReferentiel.getIdGardeSans()){
				plisAttaque.addAll(chien);
			} else if (lastEnchere==TarotReferentiel.getIdGardeContre()){
				plisDefense.addAll(chien);
			}
			PlayerTarot vainqueurPli = null;
			PlayerTarot joueurExcuse = null;
			// ici on va jouer un nombre donne de Plis, qui correspond au nombre de cartes par joueurs, par defaut du joueur 0.
			//int nbCartesParJoueur = listePlayerTarot.get(0).getSizeOfGame();
			int indexPositionVainqueurPli = -1;
			int nbParties = 1;
			Logs.info("##################################################");
			//for (int i=0;i<nbCartesParJoueur; i++){
			while (listePlayerTarotFlottante.get(0).getSizeOfGame() != 0){
				Logs.debug("Pli " + nbParties);
				PliTarot pli = new PliTarot();
				// TODO impl�menter le premier qui demarre est celui � la gauche du donneur
				// premier tour, c'est le joueur en tete de liste qui demarre
				for (int y=0; y<listePlayerTarotFlottante.size();y++) {
					PlayerTarot player = listePlayerTarotFlottante.get(y);
					player.play(pli);
					if (pli.getVainqueurPli() == player.getIdPlayer()) indexPositionVainqueurPli = y;
					if (nbParties == 1){
						// on alimente le compteur des poignees pour les decomptes finaux
						pointsPoignees+=TarotReferentiel.getPointsPoignees(player.getPoignee());
					}
				}
				GameDatas.getGameDatas().finDePli(pli);
				vainqueurPli = listePlayerTarotFlottante.get(indexPositionVainqueurPli);
				Logs.info("Vainqueur : " + vainqueurPli.getName());
				
				// TODO CAS DE L'EXCUSE : ELLE DOIT RETOURNER DANS LE TAS DE CELUI QUI L'A JOUEE
				if (pli.isExcusePlayed()){
					joueurExcuse = listePlayerTarotFlottante.getPlayerById(pli.getWhoPlayedExcuse());
					if ((!joueurExcuse.isPreneur() && vainqueurPli.isPreneur())
							|| joueurExcuse.isPreneur()){
						// L'excuse n'est pas jouee par le camp qui a gagne le pli
						//boolean excuseFound = false;
						for (int i=0;i<pli.getCards().size();i++){
							CarteTarot carte = (CarteTarot) pli.getCards().get(i);
							if (carte.isExcuse()){
								//excuseFound = true;
								if (vainqueurPli.isPreneur()){
									// l'attaquant fait le pli, la defense lui doit une carte
									if (!echangeCarte(plisDefense, pli.getCards())){
										// TODO la d�fense doit une carte basse
										defenseOwsAttaque = true;
									}
									plisDefense.add(pli.getCards().get(i));
									pli.getCards().remove(i);
								} else {
									// la defense fait le plis, l'attaquant doit une carte
									if (!echangeCarte(plisAttaque, pli.getCards())) {
										// TODO l'attaque doit une carte basse
										attaqueOwsDefense = true;
									}
									plisAttaque.add(pli.getCards().get(i));
									pli.getCards().remove(i);
								}
							}
						}
					}
				}
				if (listePlayerTarotFlottante.get(0).getSizeOfGame() == 0 && pli.isPetitMaitre()){
					// petit au bout, pour qui?
					if (vainqueurPli.isPreneur()) {
						petitBoutAttaque = true;
					} else {
						petitBoutDefense = true;
					}
				}
				// TODO FAIRE ATTENTION AUX PLACES QUI TOURNENT DANS LA LISTE
				if (vainqueurPli.isPreneur()) {
					if (attaqueOwsDefense){
						attaqueOwsDefense = echangeCarte(plisAttaque, pli.getCards());
					}
					Logs.info("Ajout du pli pour l'Attaque.");
					//Logs.info("Valeur du pli " + pli.getValeur());
					plisAttaque.addAll(pli.getCards());
					//pointsRealises += pli.getValeur();
				} else {
					if (defenseOwsAttaque){
						defenseOwsAttaque = echangeCarte(plisDefense, pli.getCards());
					}
					plisDefense.addAll(pli.getCards());
				}
				//nbCartesParJoueur--;
				// on reorganise la liste des joueurs pour permettre de placer celui qui vient de gagner en tete de liste
				Collections.rotate(listePlayerTarotFlottante, 0-indexPositionVainqueurPli);
				indexPositionVainqueurPli = -1;
				Logs.info("##################################################");
				nbParties++;
			}
			// on compte les points
			calculScores();
			if (lastEnchere==TarotReferentiel.getIdPrise() || lastEnchere==TarotReferentiel.getIdGarde()){
				for(Carte carte:plisAttaque.subList(0, 6)){
					Logs.info("Le preneur avait �cart� : " + carte.toString());
				}
			} else if (lastEnchere==TarotReferentiel.getIdGardeSans()){
				for(Carte carte:plisAttaque.subList(0, 6)){
					Logs.info("Cartes au chien : " + carte.toString());
				}
			} else {
				for(Carte carte:plisDefense.subList(0, 6)){
					Logs.info("Cartes au chien : " + carte.toString());
				}
			}
		}
	}
	
	/**
	 * Methode qui va decrementer d'une carte basse le tas de carte "from" et incrementer le tas "to".
	 * @param from
	 * @param to
	 * @return true Si l'echange a pu se faire
	 */
	private boolean echangeCarte(List<Carte> from, List<Carte> to){
		boolean carteBasseFound = false;
		int index = 0;
		for (index = 0;index<from.size();index++){
			CarteTarot carteADonner = (CarteTarot) from.get(index);
			if (carteADonner.isBasse()){
				carteBasseFound = true;
				// on a la carte basse, on l'echange avec l'excuse
				to.add(from.get(index));
				from.remove(index);
				break;
			}
		}
		return carteBasseFound;
	}
	
	private void calculScores(){
		int chuteReussite = 1;
		Contrat contratAnnonce = null;
		PlayerTarot attaquant = listePlayerTarotFlottante.getPreneur();
		CompteurPoints compteur = new CompteurPoints(plisAttaque);
		float pointsAttaquant = compteur.comptePointsPlis();
		int primePetitAuBout = 0;
		
		// recuperation du contrat du joueur
		contratAnnonce = TarotReferentiel.getContrat(attaquant.getEnchere());
		
		// calcul de la difference
		float difference = pointsAttaquant - TarotReferentiel.getPointsToDo(compteur.getNbBouts());
		if (difference < 0) chuteReussite = -1;
		
		// difference n'est pas un entier? si le preneur realise son contrat, on arrondit son total a l'entier superieur
		if (difference != ((int) difference)){
			if (chuteReussite>0){
				pointsAttaquant = (float) Math.ceil(pointsAttaquant);
			} else {
				pointsAttaquant = (float) Math.floor(pointsAttaquant);
			}
		}
		
		// correspond au score de chaque defenseur, le prenant marquant +/- 3x
		pointsRealises = chuteReussite * contratAnnonce.getMultiplicateur() * (Math.abs(difference)+25+pointsPoignees);
		
		// Petit au bout
		// La prime est acquise au camp vainqueur du dernier pli
		if (petitBoutAttaque || petitBoutDefense){
			primePetitAuBout = contratAnnonce.getMultiplicateur()*10;
			Logs.info("Prime de petit au bout : " + primePetitAuBout);
			pointsRealises+=petitBoutAttaque?primePetitAuBout:-primePetitAuBout;
		}
		Logs.info("Points pour le preneur : " + (3*pointsRealises));
		for(PlayerTarot player:listePlayerTarotFlottante){
			if(player.isPreneur()){
				player.addPoints(3*pointsRealises);
			} else {
				player.addPoints(chuteReussite*pointsRealises);			
			}
		}
		// TODO Le chelem : dans le cas du chelemn l'excuse jouee au dernier pli le remporte, et le petit est considere au bout si il est joue a l'avant dernier pli
	}
}
