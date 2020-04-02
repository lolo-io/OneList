package com.lolo.io.onelist.widgets.configurator;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.lolo.io.onelist.model.ItemList;
import com.lolo.io.onelist.widgets.SingleListWidget;
import com.lolo.io.onelist.widgets.SingleListWidgetConfigureActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2019  Felix Nüsse
 * Created on 01.04.20 - 21:49
 * <p>
 * Edited by: Felix Nüsse felix.nuesse(at)t-online.de
 * <p>
 * OneList_new
 * <p>
 * This program is released under the GPLv3 license
 * <p>
 * <p>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 */
public class CustomAdapter extends ArrayAdapter<ItemList> implements View.OnClickListener{

    private List<ItemList> dataSet;
    Context mContext;
    int appWidgetId;

    public CustomAdapter(List<ItemList> data, Context context, int appWidgetId) {
        super(context, android.R.layout.simple_list_item_1, data);
        this.dataSet = data;
        this.mContext=context;
        this.appWidgetId=appWidgetId;

    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        ItemList dataModel=(ItemList)object;

        Log.e("CustomAdapter", "CLICK "+dataModel.getTitle()+" "+dataModel.getStableId());

        ((SingleListWidgetConfigureActivity) mContext).saveTitlePref(mContext, appWidgetId, dataModel.getStableId());

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView; // view lookup cache stored in tag

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(mContext);
            v = vi.inflate(android.R.layout.simple_list_item_1, null);
        }

        ItemList il = getItem(position);

        TextView l = v.findViewById(android.R.id.text1);
        l.setTag(position);
        l.setText(il.getTitle());

        l.setOnClickListener(this);

        return v;
    }
}