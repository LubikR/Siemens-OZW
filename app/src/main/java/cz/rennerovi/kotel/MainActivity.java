package cz.rennerovi.kotel;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.rennerovi.kotel.model.DataPointDescriptionModel;
import cz.rennerovi.kotel.model.WidgetItems;

public class MainActivity extends AppCompatActivity {

    //private static String sServer_IP = "http://192.168.0.253";
    protected static String sServer_IP;
    //private static String sUserName = "Administrator";
    protected static String sUserName;
    //private static String sPassword = "fb458345350b468c74382c1cfa7b8c7e";
    protected static String sPassword;
    protected static final String PROTOCOL = "http://";
    protected static final String ROOT_PATH = "/api/auth/login.json?";
    protected static final String LIST_PATH = "/api/devicelist/list.json?SessionId=";
    protected static final String DEVICE_ROOT_PATH = "/api/menutree/device_root.json?SessionId=";
    protected static final String MENU_TREE_PATH = "/api/menutree/list.json?SessionId=";
    protected static final String TREE_NAME_PATH = "&TreeName=Mobile";
    protected static final String SERIAL_NUMBER_PATH = "&SerialNumber=";
    protected static final String DATAPOINT_DESC_PATH = "/api/menutree/datapoint_desc.json?SessionId=";
    protected static final String ID_PATH = "&Id=";

    private static final String PREFS = "prefs";
    private static final String PREF_NAME = "name";
    private static final String PREF_USER = "username";
    private static final String PREFS_IP = "ip";
    private static final String PREF_PORT = "port";

    private String url;
    private String SessionId;
    private String SerialNr;
    private String DeviceId;

    private ProgressDialog mDialog;
    private JSONArray dataJsonArr = null;
    private WidgetItems[] widgetItems = null;
    private HashMap<Integer, String> values = new HashMap<>();
    private List<Integer> asyncTasks = new ArrayList<Integer>();
    private DataPointDescriptionModel dataPointDescriptions = null;
    private HashMap<String, String> data = new HashMap<>();
    private SharedPreferences mSharedPreferences;
    //Database db = new Database(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDialog = new ProgressDialog(this);
        mDialog.setMessage(getApplicationContext().getString(R.string.loading1));
        mDialog.setCancelable(false);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                url = PROTOCOL + sServer_IP + ROOT_PATH + "user=" + sUserName + "&pwd=" + sPassword;
                new getSessionID().execute(url);
                Log.d("Kotel:getSessionId", "URL=" + url);
            }
        });

        mSharedPreferences = getSharedPreferences(PREFS, MODE_PRIVATE);
        sUserName = mSharedPreferences.getString(PREF_NAME, "");

        if (sUserName.length() > 0) {
            sPassword = mSharedPreferences.getString(PREF_USER, "");
            sServer_IP = mSharedPreferences.getString(PREFS_IP, "");
        } else {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setMessage(R.string.alert_first_time_message);
            alert.setTitle(R.string.alert_firt_time_title);
            //TODO naformatovat alert dialog, nekdy, mozna

            //GET layout inflater
            final LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.connection, null);
            alert.setView(view);
            alert.setCancelable(false);

            final EditText user = ((EditText)  view.findViewById(R.id.username));
            final EditText ip = ((EditText) view.findViewById(R.id.ip));
            final EditText pass = ((EditText) view.findViewById(R.id.password));

            //Make OK to save the input
            alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    //Put it into memory and commit
                    SharedPreferences.Editor e = mSharedPreferences.edit();
                    e.putString(PREF_NAME, user.getText().toString());
                    e.putString(PREF_USER, pass.getText().toString());
                    e.putString(PREFS_IP, ip.getText().toString());
                    e.commit();

                    sUserName = user.getText().toString();
                    sPassword = pass.getText().toString();
                    sServer_IP = ip.getText().toString();

                    Toast.makeText(MainActivity.this, "Nastavení uloženo", Toast.LENGTH_LONG).show();

                }
            });
            // make a Cancel button
           /* alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            ));*/
            alert.show();
        }
        //Zde si nactu intent kde budou checkboxy s tim, co chci zobrazovat
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, ListofValuesActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class getSessionID extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            mDialog.show();
        }

        protected String doInBackground(String... urls) {
            try {
                JsonParser jParser = new JsonParser();
                JSONObject json = jParser.getJSONFromURL(urls[0]);

                SessionId = (String) json.get("SessionId");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            url = PROTOCOL + sServer_IP + LIST_PATH + SessionId;
            Log.d("Kotel:getSerialNr", "URL=" + url);
            new getSerialNr().execute(url);
        }
    }

    private class getSerialNr extends AsyncTask<String, String, String> {
        String type;

        @Override
        protected String doInBackground(String... urls) {
            try {
                JsonParser jParser = new JsonParser();
                JSONObject json = jParser.getJSONFromURL(urls[0]);

                dataJsonArr = json.getJSONArray("Devices");

                int i = 0;
                while (SerialNr == null) {
                    JSONObject jObj = dataJsonArr.getJSONObject(i);

                    type = (String) jObj.get("Type");

                    if (type.equals("LMS14.001A236    ")) {
                        SerialNr = (String) jObj.get("SerialNr");
                    }
                    i++;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            url = PROTOCOL + sServer_IP + DEVICE_ROOT_PATH + SessionId + SERIAL_NUMBER_PATH + SerialNr + TREE_NAME_PATH;
            Log.d("Kotel:getDeviceId", "URL=" + url);
            new getDeviceId().execute(url);
        }
    }

    private class getDeviceId extends AsyncTask<String, String, String> {

        private JSONObject treeItem;

        protected String doInBackground(String... urls) {
            try {
                JsonParser jParser = new JsonParser();
                JSONObject json = jParser.getJSONFromURL(urls[0]);

                treeItem = (JSONObject) json.get("TreeItem");
                DeviceId = (String) treeItem.get("Id");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            url = PROTOCOL + sServer_IP + MENU_TREE_PATH + SessionId + ID_PATH + DeviceId;
            Log.d("Kotel:getMenuTree", "URL=" + url);
            new getMenuTree().execute(url);
        }
    }

    private class getMenuTree extends AsyncTask<String, String, String> {

        private JSONArray WidgetItemsObject;

        protected String doInBackground(String... urls) {
            //mDialog.setMessage(getApplicationContext().getString(R.string.loading2));
            try {
                JsonParser jParser = new JsonParser();
                JSONObject json = jParser.getJSONFromURL(urls[0]);

                WidgetItemsObject = (JSONArray) json.get("WidgetItems");

                Gson gson = new Gson();
                //pouzivam jen widget items, protoze v jinych objektech jsem nic nenasel
                widgetItems = gson.fromJson(WidgetItemsObject.toString(), WidgetItems[].class);
                Log.d("Kotel:getMenuTree:", WidgetItemsObject.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            url = PROTOCOL + sServer_IP + DATAPOINT_DESC_PATH + SessionId + ID_PATH;
            //spoustim getData pro vsechny DataPoints kde je text.Short
            // a zapisuji si seznam tasku, co jsem spustil
            //TODO a zaroven si ukladam NAME A ID do DB
            for (WidgetItems v : widgetItems) {
                if (!v.Text.Short.isEmpty()) {
                    //values.put(v.Datapoint.Id, v.Text.Long);
                    new getData().execute(url + v.Datapoint.Id);
                    Log.d("Kotel:getMenuTree", "URL=" + url + v.Datapoint.Id);
                    asyncTasks.add(v.Datapoint.Id);

                    //Ulozim si do DB
                    //db.addRecord(v.Text.Long, v.Id);
                }
            }
        }
    }

    private class getData extends AsyncTask<String, String, String> {
        //metoda spolupracuje s DataPointDescriptionModelem
        //http://192.168.0.253/api/menutree/datapoint_desc.json?SessionId=" + SessionID + "&Id=" + DataLists.Datapoint.Id

        private JSONObject DescriptionObject;
        private int id;

        protected String doInBackground(String... urls) {
            id = Integer.parseInt(urls[0].substring(urls[0].lastIndexOf("&") + 4, urls[0].length()));
            // + 4 je tam kvuli tomu, protoze jsem nasel neco jako Id=1027
            // takze s plus 4 se dostavam na INT 1027
            try {
                JsonParser jParser = new JsonParser();
                JSONObject json = jParser.getJSONFromURL(urls[0]);

                DescriptionObject = (JSONObject) json.get("Description");

                Gson gson = new Gson();
                dataPointDescriptions = gson.fromJson(DescriptionObject.toString(), DataPointDescriptionModel.class);
                //set final data set
                String tmp = dataPointDescriptions.getValue();
                tmp = tmp.substring(0,tmp.lastIndexOf(".")+3);
                data.put(dataPointDescriptions.getName(), tmp + " " + dataPointDescriptions.getUnit());
                //TODO Jeste toto / je tam navic jeste Podle Hours, zasranej Siemenes :)
                //{"Type":"TimeOfDay","Name":"Provozní hodiny TV","Unit":"h","Hours":{"Value_hh":"1005","Min":"0","Max":"199999","Resolution":"1"},"IsAbsolut":"false","HasValid":"false","IsValid":"true"}
                Log.d("Kotel:getData", id + ":" + dataPointDescriptions.getName() + ":" + dataPointDescriptions.getValue());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            asyncTasks.remove(asyncTasks.indexOf(id));

            // kontrola kolik je jeste asyncTasku\
            //kdyz uz neni zadny, pak poslu vysledek do adapteru na viewcko
            if (asyncTasks.isEmpty()) {
                mDialog.dismiss();
                MyBaseAdapter myBaseAdapter = new MyBaseAdapter(data);
                ListView myList=(ListView)findViewById(R.id.list);
                myList.setAdapter(myBaseAdapter);
            }
        }
    }

}
