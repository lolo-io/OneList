package com.lolo.io.onelist.widgets

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ListView
import com.lolo.io.onelist.PersistenceHelper
import com.lolo.io.onelist.R
import com.lolo.io.onelist.widgets.SingleListWidgetConfigureActivity.Companion.PREFS_NAME
import com.lolo.io.onelist.widgets.SingleListWidgetConfigureActivity.Companion.PREF_PREFIX_KEY
import com.lolo.io.onelist.widgets.configurator.CustomAdapter


const val TAG = "SLWConfigureActivity"
/**
 * The configuration screen for the [SingleListWidget] AppWidget.
 */
class SingleListWidgetConfigureActivity : Activity() {

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    private var onClickListener = View.OnClickListener {
        finalize();
    }

    private fun finalize(){
        val context = this@SingleListWidgetConfigureActivity

        // When the button is clicked, store the string locally
        //val widgetText = appWidgetText.text.toString()
        //saveTitlePref(context, appWidgetId, widgetText)

        // It is the responsibility of the configuration activity to update the app widget
        val appWidgetManager = AppWidgetManager.getInstance(context)
        updateAppWidget(context, appWidgetManager, appWidgetId)

        // Make sure we pass back the original appWidgetId
        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        setResult(RESULT_OK, resultValue)
        finish()
    }

    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED)

        setContentView(R.layout.single_list_widget_configure)

        // Find the widget id from the intent.
        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        }

        val myListView = findViewById<ListView>(R.id.lists_list_view)

        val p = PersistenceHelper(this);
        p.setContextInsteadOfActivity(this.applicationContext);

        val adapter = CustomAdapter(p.getAllLists(), this, appWidgetId);
        myListView.adapter = adapter

        findViewById<View>(R.id.add_button).setOnClickListener(onClickListener)


        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }
    }


    // Write the prefix to the SharedPreferences object for this widget
    fun saveTitlePref(context: Context, appWidgetId: Int, value: Long) {
        val prefs = context.applicationContext.getSharedPreferences(Companion.PREFS_NAME, Context.MODE_PRIVATE).edit()
        prefs.putLong(PREF_PREFIX_KEY + appWidgetId, value)
        prefs.apply()
    }

    companion object {
        const val PREFS_NAME = "com.lolo.io.onelist.widgets.SingleListWidget"
        const val PREF_PREFIX_KEY = "appwidget_"
    }
}


internal fun deleteTitlePref(context: Context, appWidgetId: Int) {
    val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
    prefs.remove(PREF_PREFIX_KEY + appWidgetId)
    prefs.apply()
}

// Read the prefix from the SharedPreferences object for this widget.
// If there is no preference saved, get the default from a resource
internal fun loadTitlePref(context: Context, appWidgetId: Int): Long {
    val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
    val titleValue = prefs.getLong(PREF_PREFIX_KEY + appWidgetId, 0)
    return titleValue ?: 0
}