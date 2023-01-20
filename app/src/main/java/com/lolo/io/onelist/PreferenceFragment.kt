package com.lolo.io.onelist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.get
import com.lolo.io.onelist.dialogs.defaultPathDialog
import com.lolo.io.onelist.updates.ReleaseNote
import com.lolo.io.onelist.updates.show
import com.lolo.io.onelist.util.beautify
import com.lolo.io.onelist.util.toUri
import kotlinx.android.synthetic.main.fragment_settings.*


class PreferenceFragment : PreferenceFragmentCompat() {


    private val mainActivity: MainActivity
        get() {
            if (activity is MainActivity) return activity as MainActivity
            else throw IllegalStateException("Activity must be MainActivity")
        }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = activity as AppCompatActivity
        customToolbar?.let { activity.setSupportActionBar(it) }
        activity.supportActionBar?.apply {
            title = getString(R.string.settings)
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

       // this.preferenceScreen.get<Preference>("theme")?.summary = mainActivity.persistence.theme
        displayDefaultPath()
        this.preferenceScreen.get<Preference>("version")?.summary = mainActivity.persistence.version


        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener { _, key ->
            when(key) {
                "theme" -> activity.recreate()/*{
                    activity.finish()
                    activity.baseContext.packageManager.getLaunchIntentForPackage(activity.baseContext.packageName)?.let {
                        it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(it)
                    }
                }*/
            }
        }
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        when (preference?.key) {
            // Manage what actions to do when the clickable preferences options are clicked
            "storage" -> defaultPathDialog(mainActivity) { path ->
                mainActivity.persistence.defaultPath = path
                displayDefaultPath()
            }
            "storage_force" -> dialogYesNo(mainActivity, mainActivity.getString(R.string.dialog_confirm_force_storage_title), mainActivity.getString(R.string.dialog_confirm_force_storage_message))
            "releaseNote" -> ReleaseNote.releasesNotes.entries.last().value().show(mainActivity)
        }
        return true
    }

    private fun displayDefaultPath() {
        this.preferenceScreen.get<Preference>("storage")
                ?.summary =
                if (mainActivity.persistence.defaultPath.isNotBlank()) {
                    val uri = mainActivity.persistence.defaultPath.toUri
                    uri?.path?.beautify() ?: mainActivity.persistence.defaultPath
                }
                else getString(R.string.app_private_storage)

    }

    private fun dialogYesNo(activity: MainActivity, title: String, message: String) {
        // Generic Yes/No confirmation dialog
        Log.d("OneList", "Debugv dialogYesNo: $title $message")

        // Instantiate an AlertDialog.Builder with its constructor
        val builder: AlertDialog.Builder = activity.let {
            AlertDialog.Builder(it)
        }
        // Chain together various setter methods to set the dialog characteristics
        builder.setTitle(title)
                .setMessage(message)
        // Setup buttons
        builder.apply {
            setPositiveButton(R.string.ok) { dialog, id ->
                // User clicked OK button
                mainActivity.persistence.updateAllPathsToDefault()
                dialog.dismiss()
            }
            setNegativeButton(R.string.cancel) { dialog, id ->
                // User cancelled the dialog
                dialog.cancel()
            }
        }
        // Get the AlertDialog from create()
        val alertDialog: AlertDialog = builder.create()
        // Show it
        alertDialog.show()
    }
}

