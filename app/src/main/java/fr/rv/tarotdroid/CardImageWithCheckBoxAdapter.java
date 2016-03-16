package fr.rv.tarotdroid;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import fr.commun.game.Carte;
import fr.tarot.utils.TarotReferentiel;

public class CardImageWithCheckBoxAdapter extends TarotDroidBaseAdapter {
    private LayoutInflater mInflater;
    private int compteur = 0;
    List<Carte> listeCartes;
    
    public CardImageWithCheckBoxAdapter(Context c, List<Carte> listeCartes) {
        this.listeCartes = listeCartes;
        mContext = c;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mThumbIds = new Integer[listeCartes.size()];
        for (int i=0;i<listeCartes.size();i++){
            mThumbIds[i] = listeCartes.get(i).getResource();
            thumbnailsselection[i] = false;
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
                if (thumbnailsselection[id]){
                    Log.d("TarotDroid", "Le joueur retire le "+ listeCartes.get(id).toString());
                    cb.setChecked(false);
                    thumbnailsselection[id] = false;
                    compteur--;
                } else {
                    if (compteur<6){
                        Log.d("TarotDroid", "Le joueur a choisi le "+ listeCartes.get(id).toString());
                        cb.setChecked(true);
                        thumbnailsselection[id] = true;
                        compteur++;
                    } else {
                        cb.setChecked(thumbnailsselection[id]);
                    }
                }
            }
        });
        holder.imageview.setImageResource(mThumbIds[position]);
        holder.checkbox.setChecked(thumbnailsselection[position]);
        holder.id = position;
        
        if (listeCartes.get(position).getIdCouleur()==TarotReferentiel.getIdAtout()
                || "R".equals(listeCartes.get(position).getValeurFaciale())){
            holder.checkbox.setEnabled(false);
        }
            
        return convertView;
    }
    
    // references to our images
    private Integer[] mThumbIds = { R.drawable.carte_dos, R.drawable.carte_dos, R.drawable.carte_dos, R.drawable.carte_dos, R.drawable.carte_dos,
            R.drawable.carte_dos, R.drawable.carte_dos, R.drawable.carte_dos, R.drawable.carte_dos, R.drawable.carte_dos, R.drawable.carte_dos,
            R.drawable.carte_dos, R.drawable.carte_dos, R.drawable.carte_dos, R.drawable.carte_dos, R.drawable.carte_dos, R.drawable.carte_dos,
            R.drawable.carte_dos, R.drawable.carte_dos, R.drawable.carte_dos, R.drawable.carte_dos, R.drawable.carte_dos, R.drawable.carte_dos, 
            R.drawable.carte_dos};
    private boolean[] thumbnailsselection = { false, false, false, false,
            false, false, false, false, false, false, false,
            false, false, false, false, false, false, false,
            false, false, false, false, false, false};

    public class ViewHolder {
        ImageView imageview;
        CheckBox checkbox;
        int id;
    }
    
    public int getNbCartesEcartees(){
        return compteur;
    }
    public List<Integer> getItemsChecked(){
        List<Integer> itemsChecked = new ArrayList<Integer>();
        for(int i=0;i<thumbnailsselection.length;i++){
            if (thumbnailsselection[i]) itemsChecked.add(i);
        }
        return itemsChecked;
    }
}