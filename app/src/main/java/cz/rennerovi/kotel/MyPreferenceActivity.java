package cz.rennerovi.kotel;

import android.os.Bundle;

/**
 * Created by User on 7.12.2015.
 */
public class MyPreferenceActivity  extends android.preference.PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }
}
