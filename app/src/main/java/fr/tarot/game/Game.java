package fr.tarot.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import fr.commun.utils.Logs;
import fr.tarot.utils.TarotReferentiel;

public class Game {
//    private static ListePlayerTarot listePlayerTarot = null;
//    //private static List<PlayerTarot> listePlayerTarotFlottante = null;
//    private static int nbPlayerTarot = 4;
//
//    public static void main(String[] args) {
//        boolean continuer = false;
//        String continuer_s = null;
//        PlayTarot game = null;
//        BufferedReader br;
//        // initialisation du jeu
//        TarotReferentiel.createJeu();
//
//        //initialisation des PlayerTarot
//        listePlayerTarot = new ListePlayerTarot();
//        for (int i = 0; i <= nbPlayerTarot - 1; i++) {
//            listePlayerTarot.add(i, new PlayerTarot(i, TarotReferentiel.getJoueur(i), i + 1, i, i));
//        }
//        // pour le dernier de la liste le joueur suivant est le premier :
//        listePlayerTarot.get(listePlayerTarot.size() - 1).setIdNextPlayer(0);
//        // on initailise le donneur
//        listePlayerTarot.get(0).setDonneur(true);
//
//        gereHuman();
//
//		/*listePlayerTarotFlottante = new ArrayList<>();
//		for (PlayerTarot player:listePlayerTarot){
//			listePlayerTarotFlottante.add(player);
//		}*/
//
//        do {
//            continuer = false;
//            TarotReferentiel.shuffleGame();
//            GameDatas.reInitGameDatas();
//            game = new PlayTarot(listePlayerTarot);
//            try {
//                game.play();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            try {
//                System.out.println("Continuer la Partie Y/N: ");
//
//                // open up standard input
//                //br = new BufferedReader(new InputStreamReader(System.in));
//                //continuer_s = br.readLine();
//
//                continuer_s = "Y";
//                if ("Y".equalsIgnoreCase(continuer_s)) {
//                    continuer = true;
//                    int indiceDonneur = -1;
//                    int i = 0;
//                    // reinitialisation partielle des joueurs
//                    for (PlayerTarot player : listePlayerTarot) {
//                        if (player.isDonneur()) indiceDonneur = i;
//                        player.reInit();
//                        i++;
//                    }
//                    // gestion du donneur
//                    listePlayerTarot.get(indiceDonneur).setDonneur(false);
//                    listePlayerTarot.get(indiceDonneur == (listePlayerTarot.size() - 1) ? 0 : indiceDonneur + 1).setDonneur(true);
//                }
//            } catch (Exception ioe) {
//                Logs.error(ioe.getMessage());
//                System.exit(1);
//            }
//        } while (continuer);
//    }
//
//    private static void gereHuman() {
//        try {
//            BufferedReader br;
//            String isHuman = null;
//            String indexHuman = null;
//            String nameHuman = null;
//
//            System.out.println("Un Joueur est-il humain Y/N: ");
//
//            // open up standard input
//            br = new BufferedReader(new InputStreamReader(System.in));
//
//            isHuman = br.readLine();
//            if ("Y".equalsIgnoreCase(isHuman)) {
//                System.out.println("Indice de ce Joueur (0 ==> 3) : ");
//
//                br = new BufferedReader(new InputStreamReader(System.in));
//
//                indexHuman = br.readLine();
//
//                System.out.println("Nom de ce Joueur : ");
//
//                br = new BufferedReader(new InputStreamReader(System.in));
//
//                nameHuman = br.readLine();
//
//                listePlayerTarot.get(Integer.parseInt(indexHuman)).setHuman(true);
//                listePlayerTarot.get(Integer.parseInt(indexHuman)).setName(nameHuman);
//                System.out.println("Bienvenue " + nameHuman);
//            }
//        } catch (IOException ioe) {
//            Logs.error(ioe.getMessage());
//            System.exit(1);
//        }
//    }
//
//	/*private static void afficheJeuComplet(String libelle) {
//		Logs.debug(libelle);
//		for (Carte carte : game) {
//			Logs.debug(carte.toString());
//		}
//		Logs.debug("nombre de cartes = " + game.size());
//	}*/
//
}
