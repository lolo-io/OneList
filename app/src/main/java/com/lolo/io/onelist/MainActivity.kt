package com.lolo.io.onelist

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.anggrayudi.storage.SimpleStorageHelper
import com.lolo.io.onelist.core.data.shared_preferences.SharedPreferencesHelper
import com.lolo.io.onelist.core.design.OneListTheme
import com.lolo.io.onelist.core.ui.Config
import com.lolo.io.onelist.feature.lists.navigation.LISTS_SCREEN_ROUTE
import com.lolo.io.onelist.feature.lists.utils.StorageHelperHolder
import com.lolo.io.onelist.navigation.OneListNavHost
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity(), StorageHelperHolder {

    override val storageHelper = SimpleStorageHelper(this)

    private val preferences by inject<SharedPreferencesHelper>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)

        Config.init(applicationContext)

        super.onCreate(savedInstanceState)

        setContent {
            OneListTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    OneListNavHost(
                        startDestination = LISTS_SCREEN_ROUTE
                    )
                }
            }
        }


        /* val fragment = OneListFragment().apply {
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
             .commit()*/

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

    interface OnDispatchTouchEvent {
        fun onDispatchTouchEvent(ev: MotionEvent)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun attachBaseContext(newBase: Context) {
        val context: Context = updateThemeConfiguration(newBase)
        super.attachBaseContext(context)
    }

    private fun updateThemeConfiguration(context: Context): Context {
        var mode = context.resources.configuration.uiMode
        when (preferences.theme) {
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
        return context.createConfigurationContext(config)
    }
}