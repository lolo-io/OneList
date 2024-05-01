package com.lolo.io.onelist

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.anggrayudi.storage.SimpleStorageHelper
import com.lolo.io.onelist.core.data.repository.OneListRepository
import com.lolo.io.onelist.core.data.shared_preferences.SharedPreferencesHelper
import com.lolo.io.onelist.core.designsystem.OneListTheme
import com.lolo.io.onelist.core.ui.Config
import com.lolo.io.onelist.feature.lists.navigation.LISTS_SCREEN_ROUTE
import com.lolo.io.onelist.feature.whatsnew.navigation.navigateToWhatsNewScreen
import com.lolo.io.onelist.navigation.OneListNavHost
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val storageHelper = SimpleStorageHelper(this)

    private val preferences by inject<SharedPreferencesHelper>()
    private val viewModel by inject<MainActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()


        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        Config.init(applicationContext)

        if (intent?.action == "android.intent.action.VIEW") {
            importListFromIntent(intent)
        }

        val sharedPreferencesHelper by inject<SharedPreferencesHelper>()

        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    return if (viewModel.listsLoaded.value) {
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else {
                        false
                    }
                }
            }
        )

        setContent {
            val navController = rememberNavController()
            val showWhatsNew = viewModel.showWhatsNew.collectAsStateWithLifecycle().value

            LaunchedEffect(showWhatsNew) {
                if(showWhatsNew) {
                    navController.navigateToWhatsNewScreen()
                }
            }

            OneListTheme(isDynamic = sharedPreferencesHelper.theme == SharedPreferencesHelper.THEME_DYNAMIC) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Surface(modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()) {
                        OneListNavHost(
                            navController =  navController,
                            simpleStorageHelper = storageHelper,
                            startDestination = LISTS_SCREEN_ROUTE
                        )
                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        println()
    }

    override fun attachBaseContext(newBase: Context) {
        val context: Context = updateThemeConfiguration(newBase)
        super.attachBaseContext(context)
    }

    private fun updateThemeConfiguration(context: Context): Context {
        var mode = context.resources.configuration.uiMode
        when (preferences.theme) {
            SharedPreferencesHelper.THEME_LIGHT -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                mode = Configuration.UI_MODE_NIGHT_NO;
            }

            SharedPreferencesHelper.THEME_DARK -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                mode = Configuration.UI_MODE_NIGHT_YES;
            }

            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }

        val config = Configuration(context.resources.configuration)
        config.uiMode = mode
        return context.createConfigurationContext(config)
    }

    private fun importListFromIntent(intent: Intent) {
        intent.data?.let { uri ->
            lifecycleScope.launch {
                try {
                    val oneListRepository by inject<OneListRepository>()
                    oneListRepository.importList(uri)
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.list_imported), Toast.LENGTH_SHORT
                    ).show()
                } catch (e: Exception) {
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.error_import_list), Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }
    }
}

