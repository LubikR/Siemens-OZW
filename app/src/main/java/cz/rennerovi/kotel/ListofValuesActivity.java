package cz.rennerovi.kotel;

import android.app.ListActivity;
import android.os.Bundle;

/**
 * Created by User on 9.12.2015.
 */
public class ListofValuesActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new ListOfValuesFragment()).commit();
    }
}
