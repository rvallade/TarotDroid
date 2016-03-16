package fr.rv.tarotdroid;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import fr.commun.game.Carte;

public class CardEcartAdapter extends TarotDroidBaseAdapter {
    public CardEcartAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return mThumbIds[position];
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            if (isExtraDensity()) {
                imageView.setLayoutParams(new GridView.LayoutParams(100, 184));
            } else {
                imageView.setLayoutParams(new GridView.LayoutParams(64, 118));
            }
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(1, 1, 1, 1);
        } else {
            imageView = (ImageView) convertView;
        }
        imageView.setImageResource(mThumbIds[position]);
        return imageView;
    }

    // references to our images
    private Integer[] mThumbIds = { R.drawable.carte_dos, R.drawable.carte_dos, R.drawable.carte_dos, R.drawable.carte_dos,
            R.drawable.carte_dos, R.drawable.carte_dos, R.drawable.carte_dos, R.drawable.carte_dos, R.drawable.carte_dos,
            R.drawable.carte_dos, R.drawable.carte_dos, R.drawable.carte_dos, R.drawable.carte_dos, R.drawable.carte_dos,
            R.drawable.carte_dos, R.drawable.carte_dos, R.drawable.carte_dos, R.drawable.carte_dos };

    public void setMThumbIdsFromList(List<Carte> listeIds){
        mThumbIds = new Integer[listeIds.size()];
        for (int i=0;i<listeIds.size();i++){
            mThumbIds[i] = listeIds.get(i).getResource();
        }
    }
}
