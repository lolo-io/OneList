package com.lolo.io.onelist.widgets

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.lolo.io.onelist.PersistenceHelper
import com.lolo.io.onelist.R
import com.lolo.io.onelist.widgets.SingleListWidget.Companion.UPDATE_SERVICE_WIDGET_ID
import kotlin.properties.Delegates

/**
 * Copyright (C) 2019  Felix Nüsse
 * Created on 01.04.20 - 20:10
 *
 * Edited by: Felix Nüsse felix.nuesse(at)t-online.de
 *
 * OneList_new
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

class SingleListWidgetService: RemoteViewsService() {

    companion object {
        val INTENT_STABLE_ID = "INTENT_STABLE_ID"
        val INTENT_TYPE = "INTENT_TYPE"
        val INTENT_TYPE_CLICK = "INTENT_TYPE_CLICK"
        val ACTION_CLICK_LIST_ITEM = "ACTION_CLICK_LIST_ITEM"
    }


    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory{
        return ListViewRemoteViewsFactory(this.applicationContext, intent)
    }

    internal class ListViewRemoteViewsFactory() : RemoteViewsFactory, BroadcastReceiver() {
        private var TAG = "SingleListWidgetService"
        private var context: Context? = null
        private lateinit var persistence: PersistenceHelper
        private var id by Delegates.notNull<Long>()


        constructor(context: Context, intent: Intent?): this() {

            this.context=context
            persistence = PersistenceHelper(Activity())
            persistence.setContextInsteadOfActivity(context)

            val intentID = intent!!.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0)

            id = getListId(context, intentID)
            TAG +=  intent!!.data

            setupIntentListener()
        }

        override fun getCount(): Int {
            if(persistence.getListByStableID(id)!!.items.size>0){
                return persistence.getListByStableID(id)!!.items.size
            }
            return 1
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getLoadingView(): RemoteViews? {
            return null
        }

        override fun getViewAt(position: Int): RemoteViews? {

            val widgetList= persistence.getListByStableID(id)
            if(widgetList?.items?.size==0){
                var remoteView = RemoteViews(context!!.packageName, R.layout.listview_row_item_empty)
                remoteView.setOnClickFillInIntent(R.id.row_empty, getFillInIntent(-1))
                return remoteView
            }
            var remoteView = RemoteViews(context!!.packageName, R.layout.listview_row_item)

            if(widgetList!!.items[position].done){
                remoteView = RemoteViews(context!!.packageName, R.layout.listview_row_item_done)

                val content1 = widgetList.items[position].title
                val spannableString1 = SpannableString(content1)
                spannableString1.setSpan(StrikethroughSpan(),0,content1.length,0)


                remoteView.setTextViewText(R.id.tv,  spannableString1)
                remoteView.setOnClickFillInIntent(R.id.row_done, getFillInIntent(widgetList.items[position].stableId))
            }else{
                remoteView.setTextViewText(R.id.tv, widgetList.items[position].title)
                remoteView.setOnClickFillInIntent(R.id.row, getFillInIntent(widgetList.items[position].stableId))
            }
            return remoteView
        }

        fun getFillInIntent(id: Long): Intent {
            val extras = Bundle()
            extras.putLong(INTENT_STABLE_ID, id)
            extras.putString(INTENT_TYPE, INTENT_TYPE_CLICK)

            val intent = Intent()
            intent.action=ACTION_CLICK_LIST_ITEM
            intent.putExtras(extras)
            return intent
        }

        override fun getViewTypeCount(): Int {
            return 3
        }

        override fun hasStableIds(): Boolean {
            return false
        }

        override fun onCreate() {

        }

        override fun onDataSetChanged() {
        }

        override fun onDestroy() {

        }

        override fun onReceive(context: Context?, intent: Intent?) {
        }

        private fun getListId(context: Context, appWidgetId: Int): Long {
            val p = PersistenceHelper(Activity())
            p.setContextInsteadOfActivity(context)
            return loadTitlePref(context, appWidgetId)
        }

        private var mIntentListener: BroadcastReceiver? = null

        private fun setupIntentListener() {
            if (mIntentListener == null) {
                mIntentListener = object : BroadcastReceiver() {
                    override fun onReceive(context: Context, intent: Intent) { // Update mUrl through BroadCast Intent
                    }
                }
                val filter = IntentFilter()
                filter.addAction(UPDATE_SERVICE_WIDGET_ID)
                context?.registerReceiver(mIntentListener, filter)
            }
        }

        private fun teardownIntentListener() {
            if (mIntentListener != null) {
                context?.unregisterReceiver(mIntentListener)
                mIntentListener = null
            }
        }

    }

}