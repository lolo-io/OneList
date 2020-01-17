package com.lolo.io.onelist.widget

import android.app.Activity
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
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
        val NEXT = "next_action"
        val PREV = "previous_action"
        val PREFERENCE = "appWidgetId"
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
            deleteTitlePref(context, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(context: Context?, intent: Intent) {
       // super.onReceive(context, intent) //add this line

        val p = PersistenceHelper(Activity())
        context?.let { p.setC(it) }

        val maxlist = p.getAllLists().size-1

        val appWidgetId = intent.getIntExtra("appwidgetid",0)

        val sp = context?.getSharedPreferences(PREFERENCE,Context.MODE_PRIVATE)
        var int = sp?.getInt(appWidgetId.toString(),0) ?: 0


        if (NEXT.equals(intent.action)) { // your onClick action is here
            int++
            if(int>maxlist){
                int=0
            }
        } else if (PREV.equals(intent.action)) {
            int--
            if(int<0){
                int=maxlist
            }
        }

        Log.e("widgetbutton", "test: $int  ${intent.action}")
        if (sp != null) {
            sp.edit().putInt(appWidgetId.toString(),int).commit()
            Log.e("widgetbutton", "test: $int  ${intent.action}")
        }


        val appWidgetManager = AppWidgetManager.getInstance(context)
        val remoteViews = RemoteViews(context!!.packageName, R.layout.simple_list_widget)
        val thisWidget = ComponentName(context, SimpleListWidget::class.java)
        appWidgetManager.updateAppWidget(thisWidget, remoteViews)

        val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)


        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.list_view)
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.appwidget_text)

        super.onReceive(context, intent)
        //appWidgetManager.updateAppWidget(appWidgetId, views)
    }

}

internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
    val widgetText = loadTitlePref(context, appWidgetId)
    // Construct the RemoteViews object
    //https://stackoverflow.com/questions/21379949/how-to-load-items-in-android-homescreen-listview-widget
    val views = RemoteViews(context.packageName, R.layout.simple_list_widget)


    val p = PersistenceHelper(Activity())
    p.setC(context)
    p.getAllLists()

    Log.e("test123", "allLists ${p.getAllLists().size}")
    val sp = context.getSharedPreferences(SimpleListWidget.PREFERENCE,Context.MODE_PRIVATE)
    val int = sp.getInt(appWidgetId.toString(),0)



    Log.e("test123", "appwidge $int")


    views.setOnClickPendingIntent(R.id.previousListButton, getPendingSelfIntent(context, SimpleListWidget.PREV, appWidgetId))
    views.setOnClickPendingIntent(R.id.nextListButton, getPendingSelfIntent(context, SimpleListWidget.NEXT, appWidgetId))


    views.setTextViewText(R.id.appwidget_text, p.getAllLists()[int].title)


    val intent = Intent(context, WidgetListViewService::class.java)
    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
    intent.data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))

    views.setRemoteAdapter(R.id.list_view, intent)

    //appWidgetManager.updateAppWidget(appWidgetId, views)
    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)


}

internal fun getPendingSelfIntent(context: Context, action: String, id: Int): PendingIntent? {
    val intent = Intent(context, SimpleListWidget::class.java)
    intent.action = action
    intent.putExtra("appwidgetid",id)
    return PendingIntent.getBroadcast(context, 0, intent, 0)
}