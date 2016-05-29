package fr.rv.tarotdroid;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import fr.tarot.game.CarteTarot;

public class CardImageWithCheckBoxForPoigneeAdapter extends TarotDroidBaseAdapter {
    private LayoutInflater mInflater;
    private int compteur = 0;
    List<CarteTarot> listeCartes;
    private boolean couldExcuseBeIn = false;

    /**
     * Constructeur pour l'adapter du gridview qui affiche les cartes.
     */
    public CardImageWithCheckBoxForPoigneeAdapter(Context c, List<CarteTarot> listeAtouts) {
        this.listeCartes = listeAtouts;
        mContext = c;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mThumbIds = new Integer[listeAtouts.size()];
        for (int i = 0; i < listeAtouts.size(); i++) {
            mThumbIds[i] = listeAtouts.get(i).getResource();
            thumbnailsselection[i] = false;
        }
        switch (listeAtouts.size()) {
            case 10:
                // simple poignee
                compteur = 10;
                couldExcuseBeIn = true;
                break;
            case 11:
            case 12:
                // simple poignee
                compteur = 10;
                couldExcuseBeIn = false;
                break;
            case 13:
                // double poignee
                compteur = 13;
                couldExcuseBeIn = true;
                break;
            case 14:
                // double poignee
                compteur = 13;
                couldExcuseBeIn = false;
                break;
            case 15:
                // triple poignee
                compteur = 15;
                couldExcuseBeIn = true;
                break;
            default:
                // triple poignee
                compteur = 15;
                couldExcuseBeIn = false;
                break;
        }
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return mThumbIds[position];
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.cardwithcheckbox, null);
            if (isExtraDensity()) {
                convertView.setLayoutParams(new GridView.LayoutParams(100, 184));
            } else {
                convertView.setLayoutParams(new GridView.LayoutParams(64, 118));
            }
            holder.imageview = (ImageView) convertView.findViewById(R.id.thumbImage);
            holder.checkbox = (CheckBox) convertView.findViewById(R.id.itemCheckBox);
            convertView.setPadding(1, 1, 1, 1);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.checkbox.setId(position);
        holder.imageview.setId(position);

        holder.checkbox.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                int id = cb.getId();
                CarteTarot carteChoisie = listeCartes.get(id);
                if (thumbnailsselection[id]) {
                    Log.d("TarotDroid", "Le joueur retire le " + carteChoisie.toString());
                    cb.setChecked(false);
                    thumbnailsselection[id] = false;
                    compteur++;
                } else {
                    if (compteur > 0) {
                        Log.d("TarotDroid", "Le joueur a choisi le " + carteChoisie.toString());
                        cb.setChecked(true);
                        thumbnailsselection[id] = true;
                        compteur--;
                    } else {
                        cb.setChecked(thumbnailsselection[id]);
                    }
                }
            }
        });
        holder.imageview.setImageResource(mThumbIds[position]);
        holder.checkbox.setChecked(thumbnailsselection[position]);
        holder.id = position;

        //pour la poignee l'excuse n'est autorisee que si il n'y a pas d'autres atouts
        if (listeCartes.get(position).isExcuse() && !couldExcuseBeIn) {
            holder.checkbox.setEnabled(false);
            holder.checkbox.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    // references to our images
    private Integer[] mThumbIds = {R.drawable.carte_dos, R.drawable.carte_dos, R.drawable.carte_dos, R.drawable.carte_dos, R.drawable.carte_dos,
            R.drawable.carte_dos, R.drawable.carte_dos, R.drawable.carte_dos, R.drawable.carte_dos, R.drawable.carte_dos, R.drawable.carte_dos,
            R.drawable.carte_dos, R.drawable.carte_dos, R.drawable.carte_dos, R.drawable.carte_dos, R.drawable.carte_dos, R.drawable.carte_dos,
            R.drawable.carte_dos};
    private boolean[] thumbnailsselection = {false, false, false, false,
            false, false, false, false, false, false, false,
            false, false, false, false, false, false, false};

    public class ViewHolder {
        ImageView imageview;
        CheckBox checkbox;
        int id;
    }

    public int getNbCartesInPoignee() {
        return getItemsChecked().size();
    }

    public List<Integer> getItemsChecked() {
        List<Integer> itemsChecked = new ArrayList<Integer>();
        for (int i = 0; i < thumbnailsselection.length; i++) {
            if (thumbnailsselection[i]) itemsChecked.add(i);
        }
        return itemsChecked;
    }
}