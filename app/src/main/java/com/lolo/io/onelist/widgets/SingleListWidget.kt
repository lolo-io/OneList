package com.lolo.io.onelist.widgets

import android.app.Activity
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import com.lolo.io.onelist.PersistenceHelper
import com.lolo.io.onelist.R
import com.lolo.io.onelist.model.switchItemStatus

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in [SingleListWidgetConfigureActivity]
 */

class SingleListWidget : AppWidgetProvider() {

    companion object{
        const val APPWIDGET_ID_EXTRA = "appwidgetid"
        const val TAG: String = "SimpleListWidget"

        const val UPDATE_SERVICE_WIDGET_ID: String = "UPDATE_SERVICE_WIDGET_ID"
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

        val appWidgetId = intent.getIntExtra(APPWIDGET_ID_EXTRA,0)

        val appWidgetManager = AppWidgetManager.getInstance(context)

        if(intent.action == SingleListWidgetService.ACTION_CLICK_LIST_ITEM){
            val stable = intent.getLongExtra(SingleListWidgetService.INTENT_STABLE_ID, 0)

            val p = PersistenceHelper(Activity())
            if (context != null) {
                p.setContextInsteadOfActivity(context)
                val list = p.getListByStableID(loadTitlePref(context,appWidgetId))

                val item = list?.items?.find { it.stableId == stable }
                item?.let {
                    list.switchItemStatus(it)
                    p.saveList(list)
                }
            }
        }

        val remoteViews = RemoteViews(context!!.packageName, R.layout.single_list_widget)

        val thisWidget = ComponentName(context, SingleListWidget::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.list_view)

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews)

        super.onReceive(context, intent)
    }
}

fun getPendingSelfIntent(context: Context, action: String, id: Int): PendingIntent? {
    val intent = Intent(context, SingleListWidget::class.java)
    intent.action = action
    intent.putExtra(SingleListWidget.APPWIDGET_ID_EXTRA, id)
    return PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT)
}

fun getListId(context: Context, appWidgetId: Int): Long {
    val p = PersistenceHelper(Activity())
    p.setContextInsteadOfActivity(context)
    return p.getAllLists()[0].stableId;
}

fun getListTitle(context: Context, listid: Long): String {
    val p = PersistenceHelper(Activity())
    p.setContextInsteadOfActivity(context)

    return p.getListByStableID(listid)?.title ?: "Delete"
}

fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        val widgetText = loadTitlePref(context.applicationContext, appWidgetId)

        // Construct the RemoteViews object
        val views = RemoteViews(context.packageName, R.layout.single_list_widget)
        //views.setTextViewText(R.id.appwidget_text, widgetText)

        views.setTextViewText(R.id.textview_listname, getListTitle(context, widgetText))


        val intent = Intent(context, SingleListWidgetService::class.java)
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)

        //https://stackoverflow.com/questions/50992720/same-widgets-with-different-listview-data
        //set random data to initialize new Factory
        intent.data = Uri.fromParts("", appWidgetId.toString(), null)

        views.setRemoteAdapter(R.id.list_view, intent)
        appWidgetManager.updateAppWidget(appWidgetId, views)

        val clickPendingIntentTemplate = getPendingSelfIntent(context, SingleListWidgetService.ACTION_CLICK_LIST_ITEM, appWidgetId)
        views.setPendingIntentTemplate(R.id.list_view, clickPendingIntentTemplate)

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

