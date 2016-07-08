package fr.rv.tarotdroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.commun.game.Carte;
import fr.commun.utils.Logs;
import fr.tarot.game.CarteTarot;
import fr.tarot.game.GameDatas;
import fr.tarot.game.HumanPlayerTarot;
import fr.tarot.game.ListePlayerTarot;
import fr.tarot.game.PlayerTarot;
import fr.tarot.game.PliTarot;
import fr.tarot.utils.TarotReferentiel;

/**
 * Created by Romain on 29/05/2016.
 */
public class BaseActivity extends Activity {
    // la carte jouee par l'humain
    protected CarteTarot carteJouee = null;
    protected CharSequence[] items = null;
    protected int choixEnchere = -1;
    protected AlertDialog.Builder builder = null;
    protected String complement = null;
    protected StringBuilder txtSB = null;

    // le dernier pli
    protected Drawable lastCardJ1 = null;
    protected Drawable lastCardJ2 = null;
    protected Drawable lastCardJ4 = null;
    protected Drawable lastCardJHuman = null;
    protected boolean authorizeToDisplayLastPli = false;

    public static final String PREFS_NAME = "TarotDroidPrefsFile";
    protected static final int STATUS_NEW = 0;
    protected static final int STATUS_CONTINUE = 1;
    protected static final String MODE = "MODE";
    protected static final int NB_PLAYERS_TAROT = 4;
    protected static final int ETAPE_DONNE = 0;
    protected static final int ETAPE_ENCHERES_ROBOTS = 1;
    protected static final int ETAPE_ENCHERES_HUMAIN = 2;
    protected static final int ETAPE_ENCHERES_HUMAIN_AFTER = 3;
    protected static final int ETAPE_PRESENTATION_CHIEN = 4;
    protected static final int ETAPE_PRESENTATION_CHIEN_AFTER = 5;
    protected static final int ETAPE_ECART = 6;
    protected static final int ETAPE_ECART_AFTER = 7;
    protected static final int ETAPE_JEU_ROBOT = 8;
    protected static final int ETAPE_JEU_HUMAIN_BEFORE = 9;
    protected static final int ETAPE_JEU_HUMAIN_AFTER = 10;
    protected static final int ETAPE_FIN_TOUR = 11;
    protected GridView jeuJoueurGW;
    protected ImageView joueur1IV;
    protected TextView nameJoueur1TV;
    protected ImageView joueur2IV;
    protected TextView nameJoueur2TV;
    protected ImageView joueur4IV;
    protected TextView nameJoueur4TV;
    protected ImageView humanIV;
    protected TextView nameHumanTV;
    protected CardAdapter adapter;
    protected Button btnPlay;
    protected Button btnMain;
    protected Dialog dialog;
    protected static ListePlayerTarot listePlayerTarot = null;
    protected ListePlayerTarot listePlayerTarotFlottante = null;
    protected HumanPlayerTarot humanPlayer = null;
    protected int donneur = -1;
    protected List<Carte> chien = null;
    protected List<Carte> plisAttaque = null;
    protected List<Carte> plisDefense = null;
    protected double pointsRealises = 0;
    protected boolean defenseOwsAttaque = false, attaqueOwsDefense = false;
    protected int pointsPoignees = 0;
    protected boolean petitBoutDefense = false;
    protected boolean petitBoutAttaque = false;
    protected int indiceEncheres = 0;
    protected int indiceTourDeJeu = 0;
    protected int enchere = TarotReferentiel.getIdPasse();
    protected int lastEnchere = TarotReferentiel.getIdPasse();
    protected int idPreneur = -1;
    protected PlayerTarot vainqueurPli = null;
    protected PlayerTarot joueurExcuse = null;
    protected int indexPositionVainqueurPli = -1;
    protected int nbPlis = 1;
    protected PliTarot pli;
    protected boolean fromPresentationPoignee = false;
    protected boolean humanWantsPoignee = false;
    protected boolean humanAskedForPoignee = false;
    protected String namePlayer = null;
    protected List<PlayerTarot> listPlayersToRefresh = new ArrayList<PlayerTarot>();

    protected int etape = -1;

    protected String snapshot = null;
    protected Handler customHandler = new Handler();

    protected RefreshHandler mRedrawHandler = new RefreshHandler();
    protected class RefreshHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            joueur1IV.invalidate();
            nameJoueur1TV.invalidate();
            joueur2IV.invalidate();
            nameJoueur2TV.invalidate();
            joueur4IV.invalidate();
            nameJoueur4TV.invalidate();
            humanIV.invalidate();
            nameHumanTV.invalidate();
            refreshHumanGame();
        }

        public void sleep(long delayMillis) {
            this.removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delayMillis);
        }
    }

    protected void refreshHumanGame() {
        jeuJoueurGW.invalidateViews();
    }

    protected void initGame() {
        donneur = -1;
        chien = null;
        pointsRealises = 0;
        defenseOwsAttaque = false;
        attaqueOwsDefense = false;
        pointsPoignees = 0;
        petitBoutDefense = false;
        petitBoutAttaque = false;
        indiceEncheres = 0;
        indiceTourDeJeu = 0;
        enchere = TarotReferentiel.getIdPasse();
        lastEnchere = TarotReferentiel.getIdPasse();
        idPreneur = -1;
        vainqueurPli = null;
        joueurExcuse = null;
        indexPositionVainqueurPli = -1;
        nbPlis = 1;
        pli = null;
        // listeEcart = null;
        carteJouee = null;
        items = null;
        choixEnchere = -1;
        complement = null;
        snapshot = null;

        GameDatas.reInitGameDatas();
        plisAttaque = new ArrayList<Carte>();
        plisDefense = new ArrayList<Carte>();
        // afficheJeuComplet("JEU ENTIER AVEC MELANGE : ");
        distribution(listePlayerTarot);
        listePlayerTarot.reInit();
        etape = ETAPE_DONNE;
        listePlayerTarot.get(0).setCarteJoueeIV(joueur1IV);
        listePlayerTarot.get(0).setNameJoueurTV(nameJoueur1TV);
        setTextInPlayerTextView(nameJoueur1TV, listePlayerTarot.get(0).getName());
        listePlayerTarot.get(1).setCarteJoueeIV(joueur2IV);
        listePlayerTarot.get(1).setNameJoueurTV(nameJoueur2TV);
        setTextInPlayerTextView(nameJoueur2TV, listePlayerTarot.get(1).getName());
        listePlayerTarot.get(2).setCarteJoueeIV(humanIV);
        listePlayerTarot.get(2).setNameJoueurTV(nameHumanTV);
        setTextInPlayerTextView(nameHumanTV, namePlayer);
        complement = null;
        listePlayerTarot.get(3).setCarteJoueeIV(joueur4IV);
        listePlayerTarot.get(3).setNameJoueurTV(nameJoueur4TV);
        setTextInPlayerTextView(nameJoueur4TV, listePlayerTarot.get(3).getName());
        // alimentation de la liste flottante, le premier de la liste est celui
        // qui est juste apres le donneur
        listePlayerTarotFlottante = new ListePlayerTarot();
        for (int i = 0; i < listePlayerTarot.size(); i++) {
            PlayerTarot player = listePlayerTarot.get(i);
            if (player.isDonneur()) {
                donneur = i;
                Logs.info(player.getName() + " est donneur.");
            }
        }
        int indice = donneur;
        do {
            indice++;
            if (indice == listePlayerTarot.size())
                indice = 0;
            listePlayerTarotFlottante.add(listePlayerTarot.get(indice));
            Logs.debug(listePlayerTarot.get(indice).getName() + " added in liste flottante");
        } while (indice != donneur);
        joueur1IV.setImageResource(R.drawable.carte_dos);
        joueur1IV.setTag(R.drawable.carte_dos);
        nameJoueur1TV.setTextColor(Color.BLACK);
        joueur2IV.setImageResource(R.drawable.carte_dos);
        joueur2IV.setTag(R.drawable.carte_dos);
        nameJoueur2TV.setTextColor(Color.BLACK);
        joueur4IV.setImageResource(R.drawable.carte_dos);
        joueur4IV.setTag(R.drawable.carte_dos);
        nameJoueur4TV.setTextColor(Color.BLACK);
        humanIV.setImageResource(R.drawable.carte_dos);
        humanIV.setTag(R.drawable.carte_dos);
        nameHumanTV.setTextColor(Color.BLACK);
        btnMain.setVisibility(View.INVISIBLE);
        mRedrawHandler.sendEmptyMessage(0);
        listPlayersToRefresh = new ArrayList<PlayerTarot>();
    }

    private void distribution(List<PlayerTarot> listePlayerTarot) {
        Log.w("TarotDroid", "DISTRIBUTION");
        // on commence par le chien
        // TODO : calculer le nombre de cartes � donner en fonction du nombre de
        // joueurs?
        TarotReferentiel.shuffleGame();
        if (donneur < 0) {
            donneur = 0;
        }
        chien = new ArrayList<Carte>();
        for (int i = 0; i < 6; i++) {
            Logs.debug("Au chien : " + TarotReferentiel.getGame().get(i).toString());
            chien.add(TarotReferentiel.getGame().get(i));
        }
        int indexCard = 6;
        for (PlayerTarot playerTarot : listePlayerTarot) {
            playerTarot.addCartes(TarotReferentiel.getGame().subList(indexCard, indexCard + 18));
            indexCard += 18;
        }
    }

    // activities menu
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    protected void onPause() {
        Log.d("TarotDroid", "debut onPause");
        super.onPause();
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("gamestate", snapshot);
        editor.putInt(MODE, STATUS_CONTINUE);
        editor.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("TarotDroid", "debut onResume");
    }

    protected String getSnapshot() {
        JSONObject o = new JSONObject();
        JSONArray a = new JSONArray();
        try {
            if (chien != null) {
                for (Carte carte : chien) {
                    if (carte != null) {
                        a.put(carte.toJSON());
                    }
                }
            }
            o.put("chien", a);
            a = new JSONArray();
            if (plisAttaque != null) {
                for (Carte carte : plisAttaque) {
                    a.put(carte.toJSON());
                }
            }
            o.put("plisAttaque", a);
            a = new JSONArray();
            if (plisDefense != null) {
                for (Carte carte : plisDefense) {
                    a.put(carte.toJSON());
                }
            }
            o.put("plisDefense", a);
            o.put("vainqueurPli", vainqueurPli != null ? vainqueurPli.toJSON() : null);
            o.put("joueurExcuse", joueurExcuse != null ? joueurExcuse.toJSON() : null);
            o.put("carteJouee", carteJouee != null ? carteJouee.toJSON() : null);
            o.put("pli", pli != null ? pli.toJSON() : null);
            o.put("donneur", donneur);
            o.put("pointsRealises", pointsRealises);
            o.put("defenseOwsAttaque", defenseOwsAttaque);
            o.put("attaqueOwsDefense", attaqueOwsDefense);
            o.put("pointsPoignees", pointsPoignees);
            o.put("petitBoutDefense", petitBoutDefense);
            o.put("petitBoutAttaque", petitBoutAttaque);
            o.put("indiceEncheres", indiceEncheres);
            o.put("indiceTourDeJeu", indiceTourDeJeu);
            o.put("enchere", enchere);
            o.put("lastEnchere", lastEnchere);
            o.put("idPreneur", idPreneur);
            o.put("indexPositionVainqueurPli", indexPositionVainqueurPli);
            o.put("nbPlis", nbPlis);
            o.put("fromPresentationPoignee", fromPresentationPoignee);
            o.put("humanWantsPoignee", humanWantsPoignee);
            o.put("humanAskedForPoignee", humanAskedForPoignee);
            o.put("etape", etape);
            o.put("choixEnchere", choixEnchere);
            o.put("complement", complement);
            for (int i = 0; i < listePlayerTarot.size(); i++) {
                PlayerTarot player = listePlayerTarot.get(i);
                o.put("joueur" + (i + 1), player.toJSON());
            }
            a = new JSONArray();
            for (int i = 0; i < listePlayerTarotFlottante.size(); i++) {
                PlayerTarot player = listePlayerTarot.get(i);
                a.put(new JSONObject("{\"id\":\"" + player.getIdPlayer() + "\"}"));
            }
            o.put("listeFlottante", a);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return o.toString();
    }

    protected void initFromSnapshot(JSONObject o) throws JSONException {
        JSONObject obj;
        JSONArray array = o.getJSONArray("chien");
        chien = new ArrayList<Carte>();
        for (int i = 0; i < array.length(); i++) {
            obj = array.getJSONObject(i);
            chien.add(TarotReferentiel.getCarteFromMapWithID(obj.getInt("id")));
        }
        array = o.getJSONArray("plisAttaque");
        plisAttaque = new ArrayList<Carte>();
        for (int i = 0; i < array.length(); i++) {
            obj = array.getJSONObject(i);
            plisAttaque.add(TarotReferentiel.getCarteFromMapWithID(obj.getInt("id")));
        }
        array = o.getJSONArray("plisDefense");
        plisDefense = new ArrayList<Carte>();
        for (int i = 0; i < array.length(); i++) {
            obj = array.getJSONObject(i);
            plisDefense.add(TarotReferentiel.getCarteFromMapWithID(obj.getInt("id")));
        }
        donneur = o.getInt("donneur");
        pointsRealises = (float) o.getDouble("pointsRealises");
        defenseOwsAttaque = o.getBoolean("defenseOwsAttaque");
        pointsPoignees = o.getInt("pointsPoignees");
        petitBoutDefense = o.getBoolean("petitBoutDefense");
        petitBoutAttaque = o.getBoolean("petitBoutAttaque");
        indiceEncheres = o.getInt("indiceEncheres");
        indiceTourDeJeu = o.getInt("indiceTourDeJeu");
        enchere = o.getInt("enchere");
        lastEnchere = o.getInt("lastEnchere");
        idPreneur = o.getInt("idPreneur");
        indexPositionVainqueurPli = o.getInt("indexPositionVainqueurPli");
        nbPlis = o.getInt("nbPlis");
        fromPresentationPoignee = o.getBoolean("fromPresentationPoignee");
        humanWantsPoignee = o.getBoolean("humanWantsPoignee");
        humanAskedForPoignee = o.getBoolean("humanAskedForPoignee");
        etape = o.getInt("etape");
        choixEnchere = o.getInt("choixEnchere");
        complement = o.has("complement") ? o.getString("complement") : null;
        // listePlayerTarot
        int i = 1;
        for (PlayerTarot player : listePlayerTarot) {
            player.initFromJson(o.getJSONObject("joueur" + i));
            if (player.isPreneur()) {
                listePlayerTarot.setPreneur(player.getIdPlayer());
            }
            i++;
        }
        // listePlayerTarotFlottante
        listePlayerTarotFlottante.clear();
        array = o.getJSONArray("listeFlottante");
        for (int j = 0; j < array.length(); j++) {
            obj = array.getJSONObject(j);
            PlayerTarot player = listePlayerTarot.getPlayerById(obj.getInt("id"));
            listePlayerTarotFlottante.add(player);
            if (player.isPreneur()) {
                listePlayerTarotFlottante.setPreneur(player.getIdPlayer());
            }
        }
    }

    protected void refreshRobotPlayersPlayedCard() {
        for (final PlayerTarot player : listPlayersToRefresh) {
            customHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    CarteTarot cardPlayed = player.getLastCardPlayed();
                    player.getCarteJoueeIV().setImageResource(cardPlayed.getResource());
                    player.getCarteJoueeIV().setTag(cardPlayed.getResource());
                }
            }, 0);
        }
    }

    protected void setTextInPlayerTextView(TextView textView, String value) {
        textView.setText(value);
    }
}
