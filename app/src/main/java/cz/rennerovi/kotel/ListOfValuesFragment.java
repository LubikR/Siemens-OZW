package cz.rennerovi.kotel;

import android.app.ListFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cz.rennerovi.kotel.model.WidgetItems;

/**
 * Created by User on 9.12.2015.
 */
public class ListOfValuesFragment extends ListFragment {

    String url;
    String SessionId;
    String SerialNr;
    String DeviceId;
    JSONArray dataJsonArr;
    WidgetItems[] widgetItems;
    HashMap<String, String> data = new HashMap<>();
    Database db = new Database(getActivity());

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        url = MainActivity.PROTOCOL + MainActivity.sServer_IP + MainActivity.ROOT_PATH + "user="
                + MainActivity.sUserName + "&pwd=" + MainActivity.sPassword;

        new getSessionID().execute(url);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.preferences, container);
        return null;

    }

    private class getSessionID extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            //mDialog.show();
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
            url = MainActivity.PROTOCOL + MainActivity.sServer_IP + MainActivity.LIST_PATH + SessionId;
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
            url = MainActivity.PROTOCOL + MainActivity.sServer_IP + MainActivity.DEVICE_ROOT_PATH
                    + SessionId + MainActivity.SERIAL_NUMBER_PATH + SerialNr + MainActivity.TREE_NAME_PATH;
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
            url = MainActivity.PROTOCOL + MainActivity.sServer_IP + MainActivity.MENU_TREE_PATH + SessionId + MainActivity.ID_PATH + DeviceId;
            //Log.d("Kotel:getMenuTree", "URL=" + url);
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

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            for (WidgetItems v : widgetItems) {
                if (!(v.Text.Long.equals(""))) {

                    db.addRecord(v.Id, v.Text.Long); //, "true");
                    data.put(v.Text.Long, "true");
                    //Log.d("Kotel:getMenuTree:", WidgetItemsObject.toString());
                    MyBaseAdapter myBaseAdapter = new MyBaseAdapter(data);
                    setListAdapter(myBaseAdapter);
                }
            }
        }
    }
}
