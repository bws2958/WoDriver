package com.wodriver.AWS;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

/**
 * Created by user on 2016-11-24.
 */

public class FragmentBase extends Fragment {

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final UserSettings userSettings = UserSettings.getInstance(view.getContext());
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                userSettings.loadFromDataset();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                final View view = getView();
                if(view != null)
                    view.setBackgroundColor(userSettings.getBackgroudColor());
            }
        }.execute();
    }
}
