package fr.rv.tarotdroid;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.BaseAdapter;

public abstract class TarotDroidBaseAdapter extends BaseAdapter {
    protected Context mContext;

    protected boolean isExtraDensity() {
        DisplayMetrics metricsBis = new DisplayMetrics();
        WindowManager wm = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metricsBis);    
        
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        return metrics.densityDpi == DisplayMetrics.DENSITY_XHIGH;
    }
}
