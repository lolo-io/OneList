package com.lolo.io.onelist.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.lolo.io.onelist.PersistenceHelper
import com.lolo.io.onelist.R
import com.lolo.io.onelist.model.ItemList


/**
 * Copyright (C) 2020  Felix Nüsse
 * Created on 15.01.20 - 18:17
 *
 * Edited by: Felix Nüsse felix.nuesse(at)t-online.de
 *
 *
 * This program is released under the GPLv3 license
 *
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 *
 *
 *
 */
class WidgetListViewService: RemoteViewsService() {

    //http://www.worldbestlearningcenter.com/answers/1524/using-listview-in-home-screen-widget-in-android

    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        Log.d("test123", "123" )
        return ListViewRemoteViewsFactory(this.applicationContext, intent)
    }

    internal class ListViewRemoteViewsFactory() : RemoteViewsFactory {
        private var context: Context? = null
        private var appWidgetId = 0

        private lateinit var persistence: PersistenceHelper


        private var widgetList: ItemList = ItemList()

        constructor(context: Context, intent: Intent?): this() {
            this.context=context
            appWidgetId = intent?.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID) ?:
                    Log.d("test123", "455$appWidgetId")

            Log.d("test123", "455$appWidgetId")


            persistence = PersistenceHelper(Activity())
            persistence.setC(context)
            persistence.getAllLists()
        }


        private fun updateWidgetListView() {
            val sp = context?.getSharedPreferences(SimpleListWidget.PREFERENCE,Context.MODE_PRIVATE)
            val int = sp?.getInt(appWidgetId.toString(),0) ?: 0
            widgetList = persistence.getAllLists()[int]
        }

        override fun getCount(): Int {
            return widgetList.items.size
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getLoadingView(): RemoteViews? {
            return null
        }

        override fun getViewAt(position: Int): RemoteViews? {
            Log.d("WidgetCreatingView", "WidgetCreatingView")

            Log.d("Loading", widgetList.items[position].title)
            var remoteView = RemoteViews(context!!.packageName, R.layout.listview_row_item)

            if(widgetList.items[position].done){
                remoteView = RemoteViews(context!!.packageName, R.layout.listview_row_item_done)

                val content1 = widgetList.items[position].title
                val spannableString1 = SpannableString(content1)
                spannableString1.setSpan(StrikethroughSpan(),0,content1.length,0)


                remoteView.setTextViewText(R.id.tv,  spannableString1)
            }else{
                remoteView.setTextViewText(R.id.tv, widgetList.items[position].title)
            }

            return remoteView
        }

        override fun getViewTypeCount(): Int {
            return 2
        }

        override fun hasStableIds(): Boolean {
            return false
        }

        override fun onCreate() {
            updateWidgetListView()
        }

        override fun onDataSetChanged() {
            updateWidgetListView()
        }

        override fun onDestroy() {
            widgetList = ItemList()
        }
    }

}