package com.lolo.io.onelist

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.anggrayudi.storage.SimpleStorageHelper
import com.lolo.io.onelist.core.data.shared_preferences.SharedPreferencesHelper
import com.lolo.io.onelist.core.ui.Config
import com.lolo.io.onelist.core.ui.REQUEST_CODE_OPEN_DOCUMENT
import com.lolo.io.onelist.core.ui.REQUEST_CODE_OPEN_DOCUMENT_TREE
import com.lolo.io.onelist.feature.lists.OneListFragment
import com.lolo.io.onelist.feature.lists.utils.StorageHelperHolder
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity(), StorageHelperHolder  {

    override val storageHelper = SimpleStorageHelper(this)

    val persistence by inject<SharedPreferencesHelper>()

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
        setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Config.init(applicationContext)

        val fragment = OneListFragment().apply {
            arguments = Bundle().apply {
                if (intent.action == "android.intent.action.VIEW") {
                    putString(
                        OneListFragment.ARG_EXT_FILE_URI,
                        intent.data.toString()
                    )
                }
            }
        }

        savedInstanceState ?: supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.zoom_in,
                R.anim.zoom_out,
                R.anim.zoom_in,
                R.anim.zoom_out
            )
            .replace(R.id.fragmentContainer, fragment, "OneListFragment")
            .commit()


        //supportFragmentManager.beginTransaction().add<SettingsFragment>(R.id.fragmentContainer).commit()


        /* todo migrate whatsnew to a normal fragment
        // WORKAROUND FOR WHATSNEW LIB NOT HANDLING WELL CONFIG CHANGES
        if (savedInstanceState != null) {
            supportFragmentManager.findFragmentByTag(WhatsNew.TAG)
                ?.let { supportFragmentManager.beginTransaction().remove(it).commit() }
                ?.let { WhatsNew.releasesNotes.entries.last().value().show(this) }
        }
         */
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        println()
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
                    contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
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
        when (persistence.theme) {
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