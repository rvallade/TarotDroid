package fr.rv.tarotdroid;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.commun.game.Carte;
import fr.commun.utils.Logs;
import fr.commun.utils.Utils;
import fr.tarot.game.CarteTarot;
import fr.tarot.game.Contrat;
import fr.tarot.game.GameDatas;
import fr.tarot.game.HumanPlayerTarot;
import fr.tarot.game.ListePlayerTarot;
import fr.tarot.game.PlayerTarot;
import fr.tarot.game.PliTarot;
import fr.tarot.game.RobotPlayerTarot;
import fr.tarot.utils.CompteurPoints;
import fr.tarot.utils.TarotReferentiel;

public class TarotDroidActivity extends BaseActivity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        setContentView(R.layout.main);

        joueur1IV = (ImageView) findViewById(R.id.carteJ1);
        nameJoueur1TV = (TextView) findViewById(R.id.txtNameJ1);
        joueur2IV = (ImageView) findViewById(R.id.carteJ2);
        nameJoueur2TV = (TextView) findViewById(R.id.txtNameJ2);
        joueur4IV = (ImageView) findViewById(R.id.carteJ4);
        nameJoueur4TV = (TextView) findViewById(R.id.txtNameJ4);
        humanIV = (ImageView) findViewById(R.id.carteHuman);
        nameHumanTV = (TextView) findViewById(R.id.txtNameHuman);

        jeuJoueurGW = (GridView) findViewById(R.id.jeuHuman);

        btnMain = (Button) findViewById(R.id.btnMain);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        namePlayer = settings.getString("nameHuman", getResources().getString(R.string.namePlayer));
        String score = settings.getString("scoreHuman", "0");
        if ("0".equals(score)) {
            reInitScores();
        }
        builder = new Builder(this);

        // initialisation du jeu
        TarotReferentiel.createJeu(getResources(), getPackageName());
        // initialisation des PlayerTarot
        listePlayerTarot = new ListePlayerTarot();
        PlayerTarot player;
        for (int i = 0; i <= NB_PLAYERS_TAROT - 1; i++) {
            if (i == 2) {
                player = new HumanPlayerTarot(i, TarotReferentiel.getJoueur(i), i + 1, i, i);
                player.setName(namePlayer);
                humanPlayer = (HumanPlayerTarot) player;
            } else {
                player = new RobotPlayerTarot(i, TarotReferentiel.getJoueur(i), i + 1, i, i);
            }
            listePlayerTarot.add(i, player);
        }

        complement = null;
        listePlayerTarot.get(0).setCarteJoueeIV(joueur1IV);
        listePlayerTarot.get(0).setNameJoueurTV(nameJoueur1TV);
        listePlayerTarot.get(0).setPointsRealises(Float.parseFloat(settings.getString(listePlayerTarot.get(0).getName(), "0")));
        setTextInPlayerTextView(nameJoueur1TV, listePlayerTarot.get(0).getName());
        listePlayerTarot.get(1).setCarteJoueeIV(joueur2IV);
        listePlayerTarot.get(1).setNameJoueurTV(nameJoueur2TV);
        listePlayerTarot.get(1).setPointsRealises(Float.parseFloat(settings.getString(listePlayerTarot.get(1).getName(), "0")));
        setTextInPlayerTextView(nameJoueur2TV, listePlayerTarot.get(1).getName());
        listePlayerTarot.get(2).setCarteJoueeIV(humanIV);
        listePlayerTarot.get(2).setNameJoueurTV(nameHumanTV);
        listePlayerTarot.get(2).setPointsRealises(Float.parseFloat(settings.getString("scoreHuman", "0")));
        setTextInPlayerTextView(nameHumanTV, namePlayer);
        listePlayerTarot.get(3).setCarteJoueeIV(joueur4IV);
        listePlayerTarot.get(3).setNameJoueurTV(nameJoueur4TV);
        listePlayerTarot.get(3).setPointsRealises(Float.parseFloat(settings.getString(listePlayerTarot.get(3).getName(), "0")));
        setTextInPlayerTextView(nameJoueur4TV, listePlayerTarot.get(3).getName());
        // pour le dernier de la liste le joueur suivant est le premier :
        listePlayerTarot.get(listePlayerTarot.size() - 1).setIdNextPlayer(0);
        // on initailise le donneur
        listePlayerTarot.get(0).setDonneur(true);

        initGame();

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);
        int startup_mode = prefs.getInt(MODE, STATUS_NEW);
        if (startup_mode == STATUS_CONTINUE) {
            String s = prefs.getString("gamestate", "");
            JSONObject o;
            try {
                o = new JSONObject(s);
                initFromSnapshot(o);
            } catch (JSONException e) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.remove("gamestate");
                editor.remove(MODE);
                editor.commit();
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    protected void onStart() {
        Log.d("TarotDroid", "debut onStart");
        super.onStart();
        play();
        Log.d("TarotDroid", "fin onStart");
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.optionNewGame:
                launchNewGame();
                return true;
            case R.id.optionDernierPli:
                presentationDernierPli();
                return true;
            case R.id.optionScores:
                presentationScoresGlobal();
                return true;
            case R.id.optionStats:
                presentationDialogStats();
                return true;
            case R.id.optionResetScores:
                reInitScores();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.inforesetscores), Toast.LENGTH_LONG).show();
                return true;
            case R.id.optionChangeName:
                presentationDialogChangeName();
                return true;
            case R.id.optionInfos:
                presentationInfo();
                return true;
            case R.id.optionQuit:
                askHumanOkForQuit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void constructItemsEncheres(int base) {
        List<String> listItems = new ArrayList<String>();
        listItems.add(getResources().getString(R.string.passe));
        if (base < TarotReferentiel.getIdPrise()) {
            listItems.add(getResources().getString(R.string.prise));
        }
        if (base < TarotReferentiel.getIdGarde()) {
            listItems.add(getResources().getString(R.string.garde));
        }
        if (base < TarotReferentiel.getIdGardeSans()) {
            listItems.add(getResources().getString(R.string.gardesans));
        }
        if (base < TarotReferentiel.getIdGardeContre()) {
            listItems.add(getResources().getString(R.string.gardecontre));
        }

        items = new CharSequence[listItems.size()];
        for (int i = 0; i < listItems.size(); i++) {
            items[i] = listItems.get(i);
        }
    }

    private void launchNewGame() {
        int indiceDonneur = -1;
        int id = 0;
        // reinit partielle des joueurs
        for (PlayerTarot player : listePlayerTarot) {
            if (player.isDonneur()) {
                indiceDonneur = id;
            }
            player.reInit();
            id++;
        }
        if (indiceDonneur == -1) {
            indiceDonneur = 0;
        }
        // gestion du donneur
        listePlayerTarot.get(indiceDonneur).setDonneur(false);
        listePlayerTarot.get(indiceDonneur == (listePlayerTarot.size() - 1) ? 0 : indiceDonneur + 1).setDonneur(true);
        listePlayerTarotFlottante = new ListePlayerTarot();
        for (int i = 0; i < listePlayerTarot.size(); i++) {
            PlayerTarot player = listePlayerTarot.get(i);
            if (player.isDonneur()) {
                donneur = i;
                Logs.info(player.getName() + " est donneur.");
            }
        }
        initGame();
        play();
    }

    public void play() {
        // le fait d'etre a une etape indique qu'il faut faire la suivante
        // on revient depuis l'interaction avec l'utilisateur ou depuis l'async,
        // il faut regarder la taille du pli
        PlayerTarot currentPlayer = null;
        MajCartePlayer majAsync;
        int idImage;
        int idCarte;
        boolean doSwitch = true;
        snapshot = getSnapshot();
        if (pli != null) {
            if (pli.getCards().size() < 4) {
                // il reste a jouer
                currentPlayer = listePlayerTarotFlottante.get(indiceTourDeJeu);
                if (pli.getCards().size() == 0) {
                    currentPlayer.getNameJoueurTV().setTextColor(Color.RED);
                }
                if (currentPlayer.isHuman()) {
                    if (humanWantsPoignee) {
                        CardImageWithCheckBoxForPoigneeAdapter adapterPoigneeBox = new CardImageWithCheckBoxForPoigneeAdapter(this, humanPlayer.getJeu().getJeuAtout());
                        jeuJoueurGW.setAdapter(adapterPoigneeBox);
                        jeuJoueurGW.invalidateViews();
                        // le joueur doit faire son ecart, selection multiple?
                        btnMain.setText(getResources().getString(R.string.btnvaliderpoignee));
                        btnMain.setOnClickListener(getListenerValidePoignee());
                        btnMain.setVisibility(View.VISIBLE);
                        humanWantsPoignee = false;
                        doSwitch = false;
                    }
                    if (fromPresentationPoignee) {
                        // le joueur vient de choisir ces cartes pour la poignee
                        CardImageWithCheckBoxForPoigneeAdapter adapterPoigneeBox = (CardImageWithCheckBoxForPoigneeAdapter) jeuJoueurGW.getAdapter();
                        List<Carte> liste = new ArrayList<Carte>();
                        for (int z = adapterPoigneeBox.getItemsChecked().size() - 1; z >= 0; z--) {
                            CarteTarot atoutInPoignee = (CarteTarot) listePlayerTarotFlottante.getPreneur().getJeu().getHand().get(adapterPoigneeBox.getItemsChecked().get(z).intValue());
                            liste.add(atoutInPoignee);
                        }
                        presentePoignee(currentPlayer, liste);
                        adapter = new CardAdapter(this, humanPlayer.getJeu().getHand());
                        jeuJoueurGW.setAdapter(adapter);
                        jeuJoueurGW.invalidateViews();
                        btnMain.setVisibility(View.INVISIBLE);
                        refreshHumanGame();
                        fromPresentationPoignee = false;
                        doSwitch = false;
                    } else if (etape != ETAPE_JEU_HUMAIN_AFTER) {
                        // le joueur doit jouer
                        etape = ETAPE_JEU_HUMAIN_BEFORE;
                    }
                } else {
                    if (nbPlis == 1 && !fromPresentationPoignee && currentPlayer.hasPoignee()) {
                        // presentation de la poignee
                        presentePoignee(currentPlayer);
                        fromPresentationPoignee = true;
                        doSwitch = false;
                    }
                    etape = ETAPE_JEU_ROBOT;
                }
            } else {
                gereFinPli();
                if (listePlayerTarotFlottante.get(0).getSizeOfGame() != 0) {
                    // on entame un autre tour
                    authorizeToDisplayLastPli = true;
                    refreshLastPli();
                    pli = new PliTarot();
                    indiceTourDeJeu = 0;
                    etape = ETAPE_JEU_ROBOT;
                    majAsync = new MajCartePlayer(true);
                    majAsync.execute();
                } else {
                    // jeu fini
                    authorizeToDisplayLastPli = false;
                    calculScores();
                    pli = null;
                    btnMain.setText(getResources().getString(R.string.btnscores));
                    btnMain.setOnClickListener(getListenerFinGameDisplayScores());
                    btnMain.setVisibility(View.VISIBLE);
                }
                doSwitch = false;
            }
        }
        if (doSwitch) {
            switch (etape) {
                case ETAPE_DONNE:
                    // il faut faire les encheres
                    // TODO Tenir compte du donneur pour les encheres, le premier a parler est celui qui est juste apres
                    for (int i = indiceEncheres; i < listePlayerTarotFlottante.size(); i++) {
                        indiceEncheres = i;
                        PlayerTarot playerTarot = listePlayerTarotFlottante.get(i);
                        if (!playerTarot.isHuman()) {
                            enchere = playerTarot.takeDecision(lastEnchere);
                            setTextInPlayerTextView(playerTarot.getNameJoueurTV(), playerTarot.getName() + "\r\n" + TarotReferentiel.getContrat(enchere).getName());
                            mRedrawHandler.sendEmptyMessage(0);
                            if (enchere > lastEnchere) {
                                idPreneur = playerTarot.getIdPlayer();
                                lastEnchere = enchere;
                            }
                        } else {
                            etape = ETAPE_ENCHERES_HUMAIN;
                            // chargement du gridview avec le jeu du joueur
                            adapter = new CardAdapter(this, humanPlayer.getJeu().getHand());
                            jeuJoueurGW.setAdapter(adapter);
                            refreshHumanGame();
                            btnMain.setText(getResources().getString(R.string.btnencheres));
                            constructItemsEncheres(lastEnchere);
                            btnMain.setOnClickListener(getListenerValideEnchere());
                            btnMain.setVisibility(View.VISIBLE);
                            break;
                        }
                    }
                    break;
                case ETAPE_ENCHERES_ROBOTS:
                    // si toutes les encheres sont faites, on doit montrer le chien,
                    // ou pas. Sinon on continue
                    break;
                case ETAPE_ENCHERES_HUMAIN:
                    break;
                case ETAPE_ENCHERES_HUMAIN_AFTER:
                    // l'humain vient d'annoncer son contrat. Si toutes les encheres
                    // sont faites, on doit montrer le chien, ou pas. Sinon on
                    // continue
                    btnMain.setVisibility(View.INVISIBLE);
                    enchere = TarotReferentiel.getContrat(items[choixEnchere].toString(), getResources()).getId();
                    humanPlayer.setEnchere(enchere);
                    complement = "\r\n" + TarotReferentiel.getContrat(enchere).getName();
                    setTextInPlayerTextView(humanPlayer.getNameJoueurTV(), namePlayer + complement);
                    mRedrawHandler.sendEmptyMessage(0);
                    if (enchere > lastEnchere) {
                        idPreneur = humanPlayer.getIdPlayer();
                        lastEnchere = enchere;
                    }
                    if (indiceEncheres == NB_PLAYERS_TAROT - 1) {
                        // on a fini les encheres
                        if (lastEnchere != TarotReferentiel.getIdPasse()) {
                            gereChien();
                            mRedrawHandler.sendEmptyMessage(0);
                        } else {
                            gereNonPrise();
                        }
                    } else {
                        indiceEncheres++;
                        // Il n'y a plus d'humain
                        for (int i = indiceEncheres; i < listePlayerTarotFlottante.size(); i++) {
                            indiceEncheres = i;
                            PlayerTarot playerTarot = listePlayerTarotFlottante.get(i);
                            enchere = playerTarot.takeDecision(lastEnchere);
                            complement = "\r\n" + TarotReferentiel.getContrat(enchere).getName();
                            setTextInPlayerTextView(playerTarot.getNameJoueurTV(), playerTarot.getName() + complement);
                            mRedrawHandler.sendEmptyMessage(0);
                            if (enchere > lastEnchere) {
                                idPreneur = playerTarot.getIdPlayer();
                                lastEnchere = enchere;
                            }
                        }

                        // on a fini les encheres
                        if (lastEnchere != TarotReferentiel.getIdPasse()) {
                            gereChien();
                            mRedrawHandler.sendEmptyMessage(0);
                        } else {
                            gereNonPrise();
                        }
                    }
                    break;
                case ETAPE_PRESENTATION_CHIEN_AFTER:
                    // si on est ici c'est que quelqu'un prend et que c'est une prise
                    // ou une garde
                    // on doit faire l'ecart
                    listePlayerTarotFlottante.getPreneur().addCartes(chien);
                    if (listePlayerTarotFlottante.getPreneur().isHuman()) {
                        CardImageWithCheckBoxAdapter adapterBox = new CardImageWithCheckBoxAdapter(this, humanPlayer.getJeu().getHand());
                        jeuJoueurGW.setAdapter(adapterBox);
                        jeuJoueurGW.invalidateViews();
                        // le joueur doit faire son ecart, selection multiple?
                        btnMain.setText(getResources().getString(R.string.btnvaliderecart));
                        btnMain.setOnClickListener(getListenerValideEcart());
                        btnMain.setVisibility(View.VISIBLE);
                        etape = ETAPE_ECART;
                        break;
                    } else {
                        // on sait qu'on est dans le cas d'une prise ou garde
                        plisAttaque.addAll(listePlayerTarotFlottante.getPreneur().gereEcart());
                        etape = ETAPE_ECART_AFTER;
                    }
                case ETAPE_ECART_AFTER: // ecart done, on joue
                    // si le joueur est humain alors on doit mettre ses cartes
                    // ecartees dans son tas et modifier son jeu
                    if (listePlayerTarotFlottante.getPreneur().isHuman()) {
                        if (plisAttaque.size() == 0 && plisDefense.size() == 0) {
                            // on est pas dans le cas d'une garde sans ou contre
                            CardImageWithCheckBoxAdapter adapterBox = (CardImageWithCheckBoxAdapter) jeuJoueurGW.getAdapter();
                            for (int z = adapterBox.getItemsChecked().size() - 1; z >= 0; z--) {
                                CarteTarot carteEcartee = (CarteTarot) listePlayerTarotFlottante.getPreneur().getJeu().getHand().get(adapterBox.getItemsChecked().get(z));
                                listePlayerTarotFlottante.getPreneur().getJeu().remove(carteEcartee);
                            }
                        }
                        adapter = new CardAdapter(this, humanPlayer.getJeu().getHand());
                        jeuJoueurGW.setAdapter(adapter);
                        btnMain.setVisibility(View.INVISIBLE);
                        refreshHumanGame();
                    }
                    pli = new PliTarot();
                    play();
                    break;
                case ETAPE_JEU_ROBOT: // if everybody played then the pli is finished
                    // on fait jouer le currentPlayer
                    fromPresentationPoignee = false;
                    CarteTarot cartePlayed = currentPlayer.play(pli);
                    idImage = currentPlayer.getCarteJoueeIV().getId();
                    idCarte = cartePlayed.getResource();
                    listPlayersToRefresh.add(currentPlayer);
                    majAsync = new MajCartePlayer(idImage, idCarte);
                    majAsync.execute();
                    currentPlayer.playCard(pli, cartePlayed);
                    if (pli.getVainqueurPli() == currentPlayer.getIdPlayer()) {
                        indexPositionVainqueurPli = indiceTourDeJeu;
                    }
                    if (nbPlis == 1) {
                        // on alimente le compteur des poignees pour les decomptes finaux
                        pointsPoignees += TarotReferentiel.getPointsPoignees(currentPlayer.getPoignee());
                    }
                    indiceTourDeJeu++;
                    // on s'arrete ici, la suite c'est l'async qui appelle play.
                    break;
                case ETAPE_JEU_HUMAIN_BEFORE: // c'est a l'humain de jouer, on lui
                    // donne la main
                    if (!humanAskedForPoignee && nbPlis == 1 && currentPlayer.hasPoignee()) {
                        askHumanForPoignee(currentPlayer);
                    } else {
                        jeuJoueurGW.setOnItemClickListener(getListenerForPlay());
                    }
                    break;
                case ETAPE_JEU_HUMAIN_AFTER: // si tout le monde a jou alors le pli
                    // est fini
                    // on doit tester la carte jouee
                    // si elle est correcte il la joue, sinon on lui rend la main
                    if (pli == null) {
                        pli = new PliTarot();
                    }
                    if (humanPlayer.testCarte(carteJouee, pli)) {
                        idImage = humanPlayer.getCarteJoueeIV().getId();
                        idCarte = carteJouee.getResource();
                        majAsync = new MajCartePlayer(idImage, idCarte);
                        // human.setImageResource(carteJouee.getResource());
                        // int idResource = carteJouee.getResource();
                        humanPlayer.playCard(pli, carteJouee);
                        majAsync.execute();
                        if (pli.getVainqueurPli() == humanPlayer.getIdPlayer()) {
                            indexPositionVainqueurPli = indiceTourDeJeu;
                        }
                        if (nbPlis == 1) {
                            // on alimente le compteur des poignees pour les
                            // decomptes finaux
                            pointsPoignees += TarotReferentiel.getPointsPoignees(humanPlayer.getPoignee());
                        }
                        indiceTourDeJeu++;
                        adapter = new CardAdapter(this, humanPlayer.getJeu().getHand());
                        jeuJoueurGW.setAdapter(adapter);
                        jeuJoueurGW.setOnItemClickListener(null);
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.errorcantplaycarte), Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void gereNonPrise() {
        // personne ne prend
        int indiceDonneur = -1;
        int id = 0;
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.infonobodytakes), Toast.LENGTH_LONG).show();
        // reinit partielle des joueurs
        for (PlayerTarot player : listePlayerTarot) {
            if (player.isDonneur()) {
                indiceDonneur = id;
            }
            player.reInit();
            id++;
        }
        if (indiceDonneur == -1) {
            indiceDonneur = 0;
        }
        // gestion du donneur
        listePlayerTarot.get(indiceDonneur).setDonneur(false);
        listePlayerTarot.get(indiceDonneur == (listePlayerTarot.size() - 1) ? 0 : indiceDonneur + 1).setDonneur(true);
        listePlayerTarotFlottante = new ListePlayerTarot();
        for (int i = 0; i < listePlayerTarot.size(); i++) {
            PlayerTarot player = listePlayerTarot.get(i);
            if (player.isDonneur()) {
                donneur = i;
                Logs.info(player.getName() + " est donneur.");
            }
        }
        initGame();
        play();
    }

    /**
     * Methode qui va decrementer d'une carte basse le tas de carte "from" et
     * incrementer le tas "to".
     * @return true Si l'echange a pu se faire
     */
    private boolean echangeCarte(List<Carte> from, List<Carte> to) {
        boolean carteBasseFound = false;
        int index;
        for (index = 0; index < from.size(); index++) {
            CarteTarot carteADonner = (CarteTarot) from.get(index);
            if (carteADonner.isBasse()) {
                carteBasseFound = true;
                // on a la carte basse, on l'echange avec l'excuse
                to.add(from.get(index));
                from.remove(index);
                break;
            }
        }
        return carteBasseFound;
    }

    private void calculScores() {
        txtSB = new StringBuilder();
        int chuteReussite = 1;
        Contrat contratAnnonce;
        PlayerTarot attaquant = listePlayerTarotFlottante.getPreneur();
        CompteurPoints compteur = new CompteurPoints(plisAttaque);
        double pointsAttaquant = compteur.comptePointsPlis();
        int primePetitAuBout;

        // recuperation du contrat du joueur
        contratAnnonce = TarotReferentiel.getContrat(attaquant.getEnchere());
        txtSB.append(Utils.buildMessage(getResources().getString(R.string.txtcalcul1), attaquant.getName(), contratAnnonce.getName()));

        // calcul de la difference
        double difference = pointsAttaquant - TarotReferentiel.getPointsToDo(compteur.getNbBouts());
        if (difference < 0) {
            chuteReussite = -1;
        }

        // difference n'est pas un entier? si le preneur realise son contrat, on
        // arrondit son total a l'entier superieur
        if (difference != ((int) difference)) {
            if (chuteReussite > 0) {
                pointsAttaquant = Math.ceil(pointsAttaquant);
                difference = Math.floor(difference);
            } else {
                pointsAttaquant = Math.floor(pointsAttaquant);
                difference = Math.ceil(difference);
            }
        }
        txtSB.append(Utils.buildMessage(getResources().getString(R.string.txtcalcul2), attaquant.getName(),
                Double.toString(pointsAttaquant),
                Integer.toString(TarotReferentiel.getPointsToDo(compteur.getNbBouts())),
                Integer.toString(compteur.getNbBouts())));
        if (chuteReussite < 0) {
            txtSB.append(Utils.buildMessage(getResources().getString(R.string.txtcalcul3chute), attaquant.getName(), Double.toString(Math.abs(difference))));
        } else {
            txtSB.append(Utils.buildMessage(getResources().getString(R.string.txtcalcul3passe), attaquant.getName(), Double.toString(Math.abs(difference))));
        }

        // correspond au score de chaque defenseur, le prenant marquant +/- 3x
        pointsRealises = chuteReussite * contratAnnonce.getMultiplicateur() * (Math.abs(difference) + 25 + pointsPoignees);

        txtSB.append(Utils.buildMessage(getResources().getString(R.string.txtcalcul4), Integer.toString(pointsPoignees)));

        txtSB.append(Utils.buildMessage(getResources().getString(R.string.txtcalcul5), chuteReussite < 0 ? " -" : "",
                Integer.toString(contratAnnonce.getMultiplicateur()), Double.toString(Math.abs(difference)),
                Integer.toString(pointsPoignees), Double.toString(pointsRealises)));
        // Petit au bout
        // La prime est acquise au camp vainqueur du dernier pli
        if (petitBoutAttaque || petitBoutDefense) {
            primePetitAuBout = contratAnnonce.getMultiplicateur() * 10;
            Logs.info("Prime de petit au bout : " + primePetitAuBout);
            txtSB.append(Utils.buildMessage(getResources().getString(R.string.txtcalcul6petitbout),
                    Integer.toString(primePetitAuBout), petitBoutAttaque ?
                            getResources().getString(R.string.lepreneur)
                            : getResources().getString(R.string.ladefense)));
            pointsRealises += petitBoutAttaque ? primePetitAuBout : -primePetitAuBout;
        }
        Logs.info("Points r�alis�s par le preneur : " + (3 * pointsRealises));

        for (PlayerTarot player : listePlayerTarotFlottante) {
            if (player.isPreneur()) {
                player.addPoints(3 * pointsRealises);
                txtSB.append(Utils.buildMessage(getResources().getString(R.string.txtcalculscore), player.getName(), Double.toString(3 * pointsRealises), Double.toString(player.getPointsRealises())));
            } else {
                // on multiplie par -1 pour avoir l'inverse du score du preneur
                player.addPoints(-1 * pointsRealises);
                txtSB.append(Utils.buildMessage(getResources().getString(R.string.txtcalculscore), player.getName(), Double.toString(-1 * pointsRealises), Double.toString(player.getPointsRealises())));
            }
            // on enregistre les scores
            if (!player.isHuman()) {
                writeScore(player.getName(), player.getPointsRealises());
            } else {
                writeScore("scoreHuman", player.getPointsRealises());
            }
        }
        //boolean humanVainqueur = (listePlayerTarotFlottante.getPreneur().isHuman() && chuteReussite > 0) || (!listePlayerTarotFlottante.getPreneur().isHuman() && chuteReussite < 0);
        //ajoutStats(humanPlayer.isPreneur(), humanVainqueur, contratAnnonce.getId());
        // TODO Le chelem : dans le cas du chelem l'excuse jouee au dernier pli
        // le remporte, et le petit est considere au bout si il est joue a
        // l'avant dernier pli
    }

    private void gereChien() {
        complement = null;
        setTextInPlayerTextView(nameJoueur1TV, listePlayerTarot.get(0).getName());
        setTextInPlayerTextView(nameJoueur2TV, listePlayerTarot.get(1).getName());
        setTextInPlayerTextView(nameHumanTV, namePlayer);
        setTextInPlayerTextView(nameJoueur4TV, listePlayerTarot.get(3).getName());
        // quelqu'un prend, presentation du chien?
        listePlayerTarotFlottante.setPreneur(idPreneur);
        if (listePlayerTarotFlottante.getPreneur().isHuman()) {
            complement = "\r\n(" + TarotReferentiel.getContrat(lastEnchere).getShortName() + ")";
        }
        setTextInPlayerTextView(listePlayerTarotFlottante
                .getPreneur()
                .getNameJoueurTV(), listePlayerTarotFlottante.getPreneur().getName() + "\r\n(" + TarotReferentiel.getContrat(lastEnchere).getShortName() + ")");
        // que faire du chien?
        if (lastEnchere == TarotReferentiel.getIdPrise() || lastEnchere == TarotReferentiel.getIdGarde()) {
            etape = ETAPE_PRESENTATION_CHIEN;
            presentationChien();
        } else if (lastEnchere == TarotReferentiel.getIdGardeSans()) {
            plisAttaque.addAll(chien);
            etape = ETAPE_ECART_AFTER;
            play();
        } else {
            plisDefense.addAll(chien);
            etape = ETAPE_ECART_AFTER;
            play();
        }
    }

    /*
     * ################################# DIALOG
     * #################################
     */
    private void presentationChien() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.cartes);
        // dialog.setTitle("Voici le chien : ");
        adapter = new CardAdapter(this, chien);
        GridView gvChien = (GridView) dialog.findViewById(R.id.cartes);
        gvChien.setAdapter(adapter);
        TextView title = (TextView) dialog.findViewById(R.id.title);
        title.setText(Utils.buildMessage(getResources().getString(R.string.textchien), listePlayerTarotFlottante.getPreneur().getName()) + " " + getResources().getString(R.string.dialogchien));
        Button button = (Button) dialog.findViewById(R.id.btnCloseDialog);
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                etape = ETAPE_PRESENTATION_CHIEN_AFTER;
                dialog.dismiss();
                mRedrawHandler.sendEmptyMessage(0);
                play();
            }
        });
        // now that the dialog is set up, it's time to show it
        dialog.show();
    }

    private void presentationScore() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.score);
        TextView txtScore = (TextView) dialog.findViewById(R.id.txtScore);
        txtScore.setText(txtSB.toString());
        Button button = (Button) dialog.findViewById(R.id.btnScore);
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
                launchNewGame();
            }
        });
        // now that the dialog is set up, it's time to show it
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.FILL_PARENT;
        lp.height = WindowManager.LayoutParams.FILL_PARENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void presentationDernierPli() {
        if (authorizeToDisplayLastPli) {
            dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.lastpli);

            ImageView lastCardJ1IV = (ImageView) dialog.findViewById(R.id.lastCarteJ1);
            lastCardJ1IV.setImageDrawable(lastCardJ1);
            lastCardJ1IV.invalidate();
            ImageView lastCardJ2IV = (ImageView) dialog.findViewById(R.id.lastCarteJ2);
            lastCardJ2IV.setImageDrawable(lastCardJ2);
            lastCardJ2IV.invalidate();
            ImageView lastCardJ4IV = (ImageView) dialog.findViewById(R.id.lastCarteJ4);
            lastCardJ4IV.setImageDrawable(lastCardJ4);
            lastCardJ4IV.invalidate();
            ImageView lastCardHumanIV = (ImageView) dialog.findViewById(R.id.lastCarteHuman);
            lastCardHumanIV.setImageDrawable(lastCardJHuman);
            lastCardHumanIV.invalidate();

            Button btnStatClose = (Button) dialog.findViewById(R.id.btnCloseLastPli);
            btnStatClose.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            // now that the dialog is set up, it's time to show it
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            dialog.show();
            dialog.getWindow().setAttributes(lp);
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.erroroptionnotavailable), Toast.LENGTH_SHORT).show();
        }

    }

    private void refreshLastPli() {
        lastCardJ1 = joueur1IV.getDrawable();
        lastCardJ2 = joueur2IV.getDrawable();
        lastCardJ4 = joueur4IV.getDrawable();
        lastCardJHuman = humanIV.getDrawable();
    }

    private void presentationScoresGlobal() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.scores);
        TextView txtScoreJ1 = (TextView) dialog.findViewById(R.id.txtJ1);
        txtScoreJ1.setText("Mireille : " + settings.getString("Mireille", "-"));
        TextView txtScoreJ2 = (TextView) dialog.findViewById(R.id.txtJ2);
        txtScoreJ2.setText("Lise : " + settings.getString("Lise", "-"));
        TextView txtScoreJH = (TextView) dialog.findViewById(R.id.txtHuman);
        txtScoreJH.setText(settings.getString("nameHuman", "Joueur") + " : " + settings.getString("scoreHuman", "-"));
        TextView txtScoreJ4 = (TextView) dialog.findViewById(R.id.txtJ4);
        txtScoreJ4.setText("Simon : " + settings.getString("Simon", "-"));
        Button button = (Button) dialog.findViewById(R.id.btnListeScores);
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.FILL_PARENT;
        lp.height = WindowManager.LayoutParams.FILL_PARENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void presentePoignee(PlayerTarot currentPlayer, List<Carte> poignee) {
        GridView gvPoignee;
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.cartes);
        TextView title = (TextView) dialog.findViewById(R.id.title);
        title.setText(Utils.buildMessage(getResources().getString(R.string.dialogpoignee), currentPlayer.getName(), TarotReferentiel.getPoignee(currentPlayer.getPoigneeType()).getName()));
        adapter = new CardAdapter(this, poignee);
        gvPoignee = (GridView) dialog.findViewById(R.id.cartes);
        gvPoignee.setAdapter(adapter);
        Button button = (Button) dialog.findViewById(R.id.btnCloseDialog);
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
                play();
            }
        });
        // now that the dialog is set up, it's time to show it
        dialog.show();
    }

    private void presentePoignee(PlayerTarot currentPlayer) {
        presentePoignee(currentPlayer, currentPlayer.getCartesPoignee());
    }

    private void askHumanForPoignee(PlayerTarot currentPlayer) {
        // GridView gvPoignee;
        AlertDialog dialogAsk = new AlertDialog.Builder(this).create();
        dialogAsk.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAsk.setMessage(Utils.buildMessage(getResources().getString(R.string.dialogaskpoignee), TarotReferentiel.getPoignee(currentPlayer.getPoigneeType()).getName()));
        dialogAsk.setButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                humanWantsPoignee = true;
                humanAskedForPoignee = true;
                play();
            }
        });
        dialogAsk.setButton2(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                humanWantsPoignee = false;
                humanAskedForPoignee = true;
                play();
            }
        });
        dialogAsk.setIcon(R.drawable.icone);
        dialogAsk.show();
    }

    private void askHumanOkForQuit() {
        AlertDialog dialogAsk = new AlertDialog.Builder(this).create();
        dialogAsk.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAsk.setMessage(Utils.buildMessage(getResources().getString(R.string.dialogaskquit)));
        dialogAsk.setButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        dialogAsk.setButton2(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialogAsk.setIcon(R.drawable.icone);
        dialogAsk.show();
    }

    private void presentationInfo() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.infos);
        dialog.show();
    }

    private void presentationDialogChangeName() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.changename);
        EditText edit = (EditText) dialog.findViewById(R.id.txtNamePlayer);
        edit.setText(namePlayer);
        Button btnChangeName = (Button) dialog.findViewById(R.id.btnValideName);
        btnChangeName.setOnClickListener(getListenerValideNewName());
        dialog.show();
    }

    private void presentationDialogStats() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.stats);

        TextView txtGeneral = (TextView) dialog.findViewById(R.id.txtStatsTotal);
        txtGeneral.setText(Integer.toString(settings.getInt("nbparties", 0)));
        TextView txtWin = (TextView) dialog.findViewById(R.id.txtStatsWin);
        txtWin.setText(Integer.toString(settings.getInt("nbvictoires", 0)));
        TextView txtWinTeam = (TextView) dialog.findViewById(R.id.txtStatsWinTeam);
        txtWinTeam.setText(Integer.toString(settings.getInt("nbvictoiresgroupe", 0)));
        TextView txtLost = (TextView) dialog.findViewById(R.id.txtStatsLost);
        txtLost.setText(Integer.toString(settings.getInt("nbdefaites", 0)));
        TextView txtLostTeam = (TextView) dialog.findViewById(R.id.txtStatsLostTeam);
        txtLostTeam.setText(Integer.toString(settings.getInt("nbdefaitesgroupe", 0)));

        Button btnStatClose = (Button) dialog.findViewById(R.id.btnStatClose);
        btnStatClose.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button btnStatReset = (Button) dialog.findViewById(R.id.btnStatReset);
        btnStatReset.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                reInitScores();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.inforesetstats), Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void gereFinPli() {
        GameDatas.getGameDatas().finDePli(pli);
        vainqueurPli = listePlayerTarotFlottante.get(indexPositionVainqueurPli);
        // TODO CAS DE L'EXCUSE : ELLE DOIT RETOURNER DANS LE TAS DE CELUI QUI L'A JOUEE
        if (pli.isExcusePlayed()) {
            joueurExcuse = listePlayerTarotFlottante.getPlayerById(pli.getWhoPlayedExcuse());
            if ((!joueurExcuse.isPreneur() && vainqueurPli.isPreneur()) || joueurExcuse.isPreneur()) {
                // Excuse is not played by camp that won the trick
                // boolean excuseFound = false;
                for (int i = 0; i < pli.getCards().size(); i++) {
                    CarteTarot carte = (CarteTarot) pli.getCards().get(i);
                    if (carte.isExcuse()) {
                        // excuseFound = true;
                        if (vainqueurPli.isPreneur()) {
                            // l'attaquant fait le pli, la defense lui doit une
                            // carte
                            if (!echangeCarte(plisDefense, pli.getCards())) {
                                // TODO la defense doit une carte basse
                                defenseOwsAttaque = true;
                            }
                            plisDefense.add(pli.getCards().get(i));
                            pli.getCards().remove(i);
                        } else {
                            // la defense fait le plis, l'attaquant doit une
                            // carte
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
        if (listePlayerTarotFlottante.get(0).getSizeOfGame() == 0 && pli.isPetitMaitre()) {
            // petit au bout, pour qui?
            if (vainqueurPli.isPreneur()) {
                petitBoutAttaque = true;
            } else {
                petitBoutDefense = true;
            }
        }
        // TODO FAIRE ATTENTION AUX PLACES QUI TOURNENT DANS LA LISTE
        if (vainqueurPli.isPreneur()) {
            if (attaqueOwsDefense) {
                attaqueOwsDefense = echangeCarte(plisAttaque, pli.getCards());
            }
            Logs.info("Ajout du pli pour l'Attaque.");
            // Logs.info("Valeur du pli " + pli.getValeur());
            plisAttaque.addAll(pli.getCards());
            // pointsRealises += pli.getValeur();
        } else {
            if (defenseOwsAttaque) {
                defenseOwsAttaque = echangeCarte(plisDefense, pli.getCards());
            }
            Logs.info("Ajout du pli pour la Defense.");
            plisDefense.addAll(pli.getCards());
        }
        // on reorganise la liste des joueurs pour permettre de placer celui qui
        // vient de gagner en tete de liste
        Collections.rotate(listePlayerTarotFlottante, 0 - indexPositionVainqueurPli);
        indexPositionVainqueurPli = -1;
        Logs.info("##################################################");
        nbPlis++;
    }

    private class MajCartePlayer extends AsyncTask<Void, Integer, Void> {
        int idImage = -1;
        int idCarte = -1;
        boolean forAll = false;

        public MajCartePlayer(int idImage, int idCarte) {
            this.idImage = idImage;
            this.idCarte = idCarte;
            forAll = false;
        }

        public MajCartePlayer(boolean forAll) {
            this.forAll = forAll;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (!forAll) {
                ImageView imageTemp = (ImageView) findViewById(idImage);
                imageTemp.setImageResource(idCarte);
                imageTemp.setTag(idCarte);
            } else {
                joueur1IV.setImageResource(R.drawable.carte_dos);
                joueur1IV.setTag(R.drawable.carte_dos);
                nameJoueur1TV.setTextColor(Color.BLACK);
                nameJoueur1TV.setTypeface(Typeface.DEFAULT);
                joueur2IV.setImageResource(R.drawable.carte_dos);
                joueur2IV.setTag(R.drawable.carte_dos);
                nameJoueur2TV.setTextColor(Color.BLACK);
                nameJoueur2TV.setTypeface(Typeface.DEFAULT);
                joueur4IV.setImageResource(R.drawable.carte_dos);
                joueur4IV.setTag(R.drawable.carte_dos);
                nameJoueur4TV.setTextColor(Color.BLACK);
                nameJoueur4TV.setTypeface(Typeface.DEFAULT);
                humanIV.setImageResource(R.drawable.carte_dos);
                humanIV.setTag(R.drawable.carte_dos);
                nameHumanTV.setTextColor(Color.BLACK);
                nameHumanTV.setTypeface(Typeface.DEFAULT);
            }
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            publishProgress(0);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            play();
        }
    }

    /**
     * Methode qui va enregistrer le nom du joueur dans les preferences
     */
    private void saveNomJoueur(String name) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("nameHuman", name);
        editor.commit();
        namePlayer = name;
        setTextInPlayerTextView(nameHumanTV, name + (complement == null ? "" : "\r\n" + complement));
    }

    private void ajoutStats(boolean humanPreneur, boolean vainqueur, int typeContrat) {
        /*typeContrat++;
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        // si il n'a pas pris et qu'il a gagne, c'est une victoire en groupe
        // contrat_0 a contrat_5, la passe qui vaut -1 est dans contrat_0
        Integer nbParties = settings.getInt("nbparties", 0);
        Integer nbPartiesPrises = settings.getInt("nbpartiesprises", 0);
        Integer nbContrats = settings.getInt("contrat" + typeContrat, 0);
        Integer nbVictoires = settings.getInt("nbvictoires", 0);
        Integer nbVictoiresGroupe = settings.getInt("nbvictoiresgroupe", 0);
        Integer nbDefaites = settings.getInt("nbdefaites", 0);
        Integer nbDefaitesGroupe = settings.getInt("nbdefaitesgroupe", 0);
        nbParties++;
        if (humanPreneur) {
            nbPartiesPrises++;
            nbContrats++;
            if (vainqueur) {
                nbVictoires++;
            } else {
                nbDefaites++;
            }
        } else {
            if (vainqueur) {
                nbVictoiresGroupe++;
            } else {
                nbDefaitesGroupe++;
            }
        }
        editor.putInt("nbparties", nbParties);
        editor.putInt("nbpartiesprises", nbPartiesPrises);
        editor.putInt("contrat" + typeContrat, nbContrats);
        editor.putInt("nbvictoires", nbVictoires);
        editor.putInt("nbvictoiresgroupe", nbVictoiresGroupe);
        editor.putInt("nbdefaites", nbDefaites);
        editor.putInt("nbdefaitesgroupe", nbDefaitesGroupe);
        editor.commit();*/
    }

    private void reInitScores() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("Mireille", "0");
        editor.putString("Lise", "0");
        editor.putString("scoreHuman", "0");
        editor.putString("Simon", "0");
        if (listePlayerTarotFlottante != null) {
            for (PlayerTarot player : listePlayerTarotFlottante) {
                player.setPointsRealises(0);
            }
        }
        editor.commit();
    }

    private void reInitStats() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("nbparties", 0);
        editor.putInt("nbpartiesprises", 0);
        editor.putInt("contrat0", 0);
        editor.putInt("contrat1", 0);
        editor.putInt("contrat2", 0);
        editor.putInt("contrat3", 0);
        editor.putInt("contrat4", 0);
        editor.putInt("nbvictoires", 0);
        editor.putInt("nbvictoiresgroupe", 0);
        editor.putInt("nbdefaites", 0);
        editor.commit();
    }

    private void writeScore(String name, double score) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(name, Double.toString(score));
        editor.commit();
    }

    /*
     * ##########################################################################
     * ############ GESTION DE TOUS LES LISTENERS
     * ###############################
     * #######################################################
     */
    private OnItemClickListener getListenerForPlay() {
        // ici on veut gerer le fait que le joueur joue ses cartes
        return new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                carteJouee = (CarteTarot) humanPlayer.getJeu().getHand().get(position);
                Log.d("TarotDroid", "Le joueur a choisi le " + carteJouee.toString());
                // etape = ETAPE_JEU_HUMAIN;
                etape = ETAPE_JEU_HUMAIN_AFTER;
                play();
                // carteJoueur.setImageDrawable(((ImageView)v).getDrawable());
                // Toast.makeText(TarotDroidActivity.this, "" + position,
                // Toast.LENGTH_SHORT).show();
            }
        };
    }

    private OnClickListener getListenerValideEnchere() {
        return new OnClickListener() {
            public void onClick(View v) {
                builder.setTitle(getResources().getString(R.string.txtannonce));
                builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        dialog.dismiss();
                        choixEnchere = item;
                        etape = ETAPE_ENCHERES_HUMAIN_AFTER;
                        play();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        };
    }

    private OnClickListener getListenerValideEcart() {
        return new OnClickListener() {
            public void onClick(View v) {
                GridView jeuJoueur = (GridView) findViewById(R.id.jeuHuman);
                CardImageWithCheckBoxAdapter adapterBox = (CardImageWithCheckBoxAdapter) jeuJoueur.getAdapter();
                if (adapterBox.getNbCartesEcartees() == 6) {
                    etape = ETAPE_ECART_AFTER;
                    play();
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.errorchien),
                            Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    private OnClickListener getListenerValidePoignee() {
        return new OnClickListener() {
            public void onClick(View v) {
                GridView jeuJoueur = (GridView) findViewById(R.id.jeuHuman);
                CardImageWithCheckBoxForPoigneeAdapter adapterBox = (CardImageWithCheckBoxForPoigneeAdapter) jeuJoueur
                        .getAdapter();
                if (adapterBox.getNbCartesInPoignee() >= 10) {
                    fromPresentationPoignee = true;
                    play();
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.errorpoignee),
                            Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    private OnClickListener getListenerValideNewName() {
        return new OnClickListener() {
            public void onClick(View v) {
                TextView txtName = (TextView) dialog.findViewById(R.id.txtNamePlayer);
                saveNomJoueur(txtName.getText().toString());
                dialog.dismiss();
            }
        };
    }

    private OnClickListener getListenerFinGameDisplayScores() {
        return new OnClickListener() {
            public void onClick(View v) {
                presentationScore();
            }
        };
    }
}