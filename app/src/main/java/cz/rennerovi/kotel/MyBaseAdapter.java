package cz.rennerovi.kotel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//Tato trida se stara o plneni ListView daty, je to pro ni adapter
public class MyBaseAdapter extends BaseAdapter {

    private ArrayList mData;

    public MyBaseAdapter(HashMap<String, String> map) {
        mData = new ArrayList();
        mData.addAll(map.entrySet());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View result;

        if (convertView == null) {
            result = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_main, parent, false);
        }
        else
        {
            result = convertView;
        }

        Map.Entry<String, String> item = getItem(position);

        // TODO replace findViewById by ViewHolder
        ((TextView) result.findViewById(android.R.id.text1)).setText(item.getKey());
        ((TextView) result.findViewById(android.R.id.text2)).setText(item.getValue());

        return result;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Map.Entry<String,String> getItem(int arg0) {
        return (Map.Entry) mData.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        /// TODO implement you own logic with ID
        return arg0;
    }

    /*final int pos = position;
    holder.txtName.setText("ABC");
    holder.chkTick.setOnCheckedChangeListener(new OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked){
                Toast.makeText(mContext, "Checked",
                        Toast.LENGTH_SHORT).show();
            }
        }
    });*/

    //take by slo zobrazovat vysledky prubezne
    /*public void updateData (JSONArray jsonArray) {
        mJsonArray = jsonArray;
        notifyDataSetChanged();
    }*/
}
