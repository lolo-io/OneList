package com.lolo.io.onelist

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.lolo.io.onelist.updates.ReleaseNote
import com.lolo.io.onelist.updates.show
import com.lolo.io.onelist.util.REQUEST_CODE_OPEN_DOCUMENT
import com.lolo.io.onelist.util.REQUEST_CODE_OPEN_DOCUMENT_TREE
import io.github.tonnyl.whatsnew.WhatsNew

class MainActivity : AppCompatActivity() {

    val persistence: PersistenceHelper by lazy { PersistenceHelper(this) }

    // On some devices, displaying storage chooser fragment before activity is resumed leads to a crash.
    // This is a workaround.
    var whenResumed = {}
        set(value) {
            if (this.isResumed) value()
            else field = value
        }
    private var isResumed = false

    var onPathChosenActivityResult: (String) -> Any? = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        App.instance.mainContext = this.baseContext
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        val fragment = OneListFragment().apply {
            if (intent.action == "android.intent.action.VIEW")
                arguments = Bundle().apply {
                    putParcelable("EXT_FILE_URI", intent.data)
                }
        }

        savedInstanceState ?: supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.zoom_in, R.anim.zoom_out, R.anim.zoom_in, R.anim.zoom_out)
                .replace(R.id.fragmentContainer, fragment, "OneListFragment")
                .commit()

        // WORKAROUND FOR WHATSNEW LIB NOT HANDLING WELL CONFIG CHANGES
        if (savedInstanceState != null) {
            supportFragmentManager.findFragmentByTag(WhatsNew.TAG)
                    ?.let { supportFragmentManager.beginTransaction().remove(it).commit() }
                    ?.let { ReleaseNote.releasesNotes.entries.last().value().show(this) }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        supportFragmentManager.fragments.filterIsInstance<OnDispatchTouchEvent>().forEach {
            it.onDispatchTouchEvent(ev)
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onResume() {
        super.onResume()
        whenResumed()
        whenResumed = {}
        isResumed = true
    }

    override fun onPause() {
        super.onPause()
        isResumed = false
    }

    interface OnDispatchTouchEvent {
        fun onDispatchTouchEvent(ev: MotionEvent)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            if (requestCode == REQUEST_CODE_OPEN_DOCUMENT_TREE || requestCode == REQUEST_CODE_OPEN_DOCUMENT)
                data?.data?.let { uri ->
                    contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                    onPathChosenActivityResult(uri.toString())
                    onPathChosenActivityResult = { }
                }
        }
    }

    override fun attachBaseContext(newBase: Context) {
        val context: Context = updateThemeConfiguration(newBase)
        super.attachBaseContext(context)
    }

    private fun updateThemeConfiguration(context: Context): Context {
        var mode = context.resources.configuration.uiMode
        when (persistence.getTheme(context)) {
            "light" -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                mode = Configuration.UI_MODE_NIGHT_NO;
            }
            "dark" -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                mode = Configuration.UI_MODE_NIGHT_YES;
            }
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }

        val config = Configuration(context.resources.configuration)
        config.uiMode = mode
        var ctx = context
        if (Build.VERSION.SDK_INT >= 17) {
            ctx = context.createConfigurationContext(config)
        } else {
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
        }
        return ctx
    }
}