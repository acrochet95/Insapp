package fr.insapp.insapp;

import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by Antoine on 16/10/2016.
 */
public abstract class FragmentRefreshable extends Fragment {

    protected boolean init = false;
    protected boolean allowRefresh = true;
    protected View view = null;

    public abstract void refresh(final boolean refreshFromSwipe);
    public abstract boolean isInitialised();
}
