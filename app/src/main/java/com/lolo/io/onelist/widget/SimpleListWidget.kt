package com.lolo.io.onelist.widget

import android.app.Activity
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import com.lolo.io.onelist.PersistenceHelper
import com.lolo.io.onelist.R


/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in [SimpleListWidgetConfigureActivity]
 */
class SimpleListWidget : AppWidgetProvider() {


    companion object{
        private val APPWIDGET_EXTRA = "appwidgetid"
        private val TAG: String = "SimpleListWidget"

        val NEXT = "next_action"
        val PREV = "previous_action"
        val UPDATE_ACTION = "update_action"
        val CONSUME_UNUSED = "CONSUME_UNUSED"
        val PREFERENCE = "appWidgetId_preference"
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        // When the user deletes the widget, delete the preference associated with it.
        for (appWidgetId in appWidgetIds) {
            //deleteTitlePref(context, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(context: Context?, intent: Intent) {
        super.onReceive(context, intent) //add this line

        val p = PersistenceHelper(Activity())
        context?.let { p.setC(it) }

        val maxlist = p.getAllLists().size-1

        val appWidgetId = intent.getIntExtra(APPWIDGET_EXTRA,0)

        val appWidgetManager = AppWidgetManager.getInstance(context)

        var int = getListID(context!!, appWidgetId)

        if (intent.action == NEXT) {
            int++
            if(int>maxlist){
                int=0
            }
        } else if (intent.action == PREV) {
            int--
            if(int<0){
                int=maxlist
            }
        } else if (intent.action == UPDATE_ACTION) {
            var l = p.getAllLists()[int]

            for (i in l.items){
                if(i.stableId==intent.getLongExtra(WidgetListViewService.INTENT_STABLE_ID,0)){
                    i.done = !i.done
                    Log.e(TAG, "Save: ${i.title}  ${i.done}")
                }
            }

            p.saveList(l)

        } else {
            Log.e(TAG, "unknown action: ${intent.action}")
        }


        if(intent.action == NEXT || intent.action == PREV){
            Log.e(TAG, "action: ${intent.action}")

        }

        val sp = context?.getSharedPreferences(PREFERENCE,Context.MODE_PRIVATE)
        if (sp != null) {
            sp.edit().putInt(appWidgetId.toString(),int).apply()
            Log.e(TAG, "save listid($appWidgetId): $int")
        }

        Log.e(TAG, "got  listid: ${getListID(context!!, appWidgetId)}")
        Log.e(TAG, "got  listid: ${
        appWidgetManager.getAppWidgetIds(ComponentName(context, SimpleListWidget::class.java)).size}")



        val remoteViews = RemoteViews(context!!.packageName, R.layout.simple_list_widget)


        remoteViews.setTextViewText(R.id.appwidget_text, getTitle(context, appWidgetId))

        val thisWidget = ComponentName(context, SimpleListWidget::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)

        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.list_view)
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
        appWidgetManager.updateAppWidget(thisWidget, remoteViews)
    }



    fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        val views = RemoteViews(context.packageName, R.layout.simple_list_widget)

        views.setOnClickPendingIntent(R.id.previousListButton, getPendingSelfIntent(context, PREV, appWidgetId))
        views.setOnClickPendingIntent(R.id.nextListButton, getPendingSelfIntent(context, NEXT, appWidgetId))
        views.setOnClickPendingIntent(R.id.main_layout, getPendingSelfIntent(context, CONSUME_UNUSED, appWidgetId))

        views.setTextViewText(R.id.appwidget_text, getTitle(context, appWidgetId))



        val intent = Intent(context, WidgetListViewService::class.java)
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        views.setRemoteAdapter(R.id.list_view, intent)
        appWidgetManager.updateAppWidget(appWidgetId, views)

        val clickPendingIntentTemplate = getPendingSelfIntent(context, UPDATE_ACTION, appWidgetId)
        views.setPendingIntentTemplate(R.id.list_view, clickPendingIntentTemplate)

        appWidgetManager.updateAppWidget(appWidgetId, views)


    }

    fun getListID(context: Context, appWidgetId: Int): Int{
        val sp = context.getSharedPreferences(PREFERENCE,Context.MODE_PRIVATE)
        return sp?.getInt(appWidgetId.toString(),0) ?: 0

    }

    fun getTitle(context: Context, appWidgetId: Int): String{
        return getTitleID(context, getListID(context, appWidgetId))
    }

    fun getTitleID(context: Context, id: Int): String{
        val p = PersistenceHelper(Activity())
        p.setC(context)
        return p.getAllLists()[id].title
    }

    fun getPendingSelfIntent(context: Context, action: String, id: Int): PendingIntent? {
        val intent = Intent(context, SimpleListWidget::class.java)
        intent.action = action
        intent.putExtra(APPWIDGET_EXTRA, id)
        return PendingIntent.getBroadcast(context, 0, intent, 0)
    }

}