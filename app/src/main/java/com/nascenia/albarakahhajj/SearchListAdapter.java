package com.nascenia.albarakahhajj;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class SearchListAdapter extends ArrayAdapter<List<List<String>>> {

    private List<List<String>> searchList;
    private Context mContext;

//    public SearchListAdapter(@NonNull Context context,
//            int resource, @NonNull List<List<String>> searchList) {
//        this.mContext = context;
//        this.searchList = searchList;
//
//    }

    public SearchListAdapter(Context context, int resource, List searchList) {
        super(context, resource, searchList);
        this.mContext = context;
        this.searchList = searchList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        super.getView(position, convertView, parent);
        View rowView = convertView;
        ViewHolder holder = new ViewHolder();
        if (rowView == null) {
            LayoutInflater inflater = LayoutInflater.from(this.mContext);
            rowView = inflater.inflate(R.layout.list_search_item, null, true);
        }

        holder.idText = (TextView) rowView.findViewById(R.id.tv_user_id);
        holder.nameText = (TextView) rowView.findViewById(R.id.tv_user_name);
        holder.mobileText = (TextView) rowView.findViewById(R.id.tv_user_mobile);

        holder.idText.setText(searchList.get(position).get(0).toString());
        holder.nameText.setText(searchList.get(position).get(1).toString());
        holder.mobileText.setText(searchList.get(position).get(2).toString());

        return rowView;
    }

    static class ViewHolder
    {
        TextView idText;
        TextView nameText;
        TextView mobileText;
    }
}
