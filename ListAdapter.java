package com.getepay.smartcitycheckin;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by aber on 18/05/15.
 */
public class ListAdapter extends ArrayAdapter<JSONObject> {

    public ListAdapter(Context context, ArrayList<JSONObject> list) {
        super(context, R.layout.list_item, list);
    }

    private TextView name;
    private TextView id;
    private TextView date;

    private TextView id_text;
    private TextView purchased_text;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        JSONObject json = getItem(position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        RelativeLayout rl = (RelativeLayout)convertView.findViewById(R.id.rl);
        if (position%2!=0) {
            rl.setBackgroundColor(0xffebeff2);
        } else {
            convertView.setBackgroundColor(0xffffffff);
        }

        name = (TextView) convertView.findViewById(R.id.name);
        id = (TextView) convertView.findViewById(R.id.id);
        date = (TextView) convertView.findViewById(R.id.date);
        id_text = (TextView) convertView.findViewById(R.id.id_text);
        purchased_text = (TextView) convertView.findViewById(R.id.purchased_text);
        try {
            name.setText(Html.fromHtml(json.getString("buyer_first") + " " +json.getString("buyer_last")));
            id.setText(Html.fromHtml(json.getString("transaction_id")));
            date.setText(Html.fromHtml(json.getString("payment_date")));
        }catch (JSONException e){
            e.printStackTrace();
        }

        id_text.setText(Html.fromHtml(Globals.getTranslation("ID")+":"));
        purchased_text.setText(Html.fromHtml(Globals.getTranslation("PURCHASED")+":"));

        return convertView;
    }



}
