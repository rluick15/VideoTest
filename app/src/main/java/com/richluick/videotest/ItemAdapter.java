package com.richluick.videotest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends ArrayAdapter<JSONObject> {

    private Context mContext;
    private ArrayList mList;

    public ItemAdapter(Context context, List<JSONObject> objects) {
        super(context, 0, objects);

        this.mContext = context;
        this.mList = (ArrayList) objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        JSONObject object = (JSONObject) mList.get(position);

        JSONObject snippet;
        String title = null;
        String channel = null;
        try {
            snippet = object.getJSONObject("snippet");
            title = snippet.getString("title");
            channel = snippet.getString("channelTitle");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_item, null);

            holder = new ViewHolder();
            holder.channelText = (TextView) convertView.findViewById(R.id.channel);
            holder.titleText = (TextView) convertView.findViewById(R.id.title);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.titleText.setText(title);
        holder.channelText.setText(channel);

        return convertView;
    }

    private static class ViewHolder {
        TextView titleText;
        TextView channelText;
    }
}
