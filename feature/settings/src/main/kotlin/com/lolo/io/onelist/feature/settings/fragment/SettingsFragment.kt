package com.lolo.io.onelist.feature.settings.fragment

import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import androidx.preference.get
import com.anggrayudi.storage.SimpleStorageHelper
import com.anggrayudi.storage.file.DocumentFileCompat
import com.anggrayudi.storage.file.getAbsolutePath
import com.anggrayudi.storage.file.toTreeDocumentFile
import com.lolo.io.onelist.core.data.shared_preferences.SharedPreferencesHelper
import com.lolo.io.onelist.core.domain.utils.isNotNullOrEmpty
import com.lolo.io.onelist.feature.settings.R
import com.lolo.io.onelist.feature.settings.databinding.FragmentSettingsBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.getViewModel

class SettingsFragment : PreferenceFragmentCompat() {

    companion object {
        const val SETTING_VERSION = "version"
        const val SETTING_STORAGE = "storage"

        const val SETTING_IMPORT = "import"
        const val SETTING_BACKUP_ALL = "backup_all"
        const val SETTING_RELEASE_NOTE = "releaseNote"

        const val SETTING_CATEGORY_BACKUP = "cat_backup"

    }

    private val viewModel by lazy { getViewModel<SettingsFragmentViewModel>() }


    private var onSharedPreferenceChangeListener: OnSharedPreferenceChangeListener? = null

    private var binding: FragmentSettingsBinding? = null

    var onClickOnShowReleaseNote: () -> Unit = {}
    var storageHelper: SimpleStorageHelper? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
        setBackupOptionsVisible(viewModel.backupDisplayPath.value != null)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        try {
            binding = FragmentSettingsBinding.bind(view)
        } catch(e: Exception) {
            // No binding provided in tests
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? AppCompatActivity)?.let { activity ->
            binding?.customToolbar.let { activity.setSupportActionBar(it) }
            activity.supportActionBar?.apply {
                title = getString(R.string.settings)
                setDisplayShowHomeEnabled(true)
                setDisplayHomeAsUpEnabled(true)
            }
        }


        this.preferenceScreen.get<Preference>(SETTING_VERSION)?.summary = viewModel.version

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.backupDisplayPath.collect {
                    (this@SettingsFragment.preferenceScreen.get<Preference>(SETTING_STORAGE)
                            as? SwitchPreference)?.isChecked =
                        it != null
                    displayDefaultPath()
                    setBackupOptionsVisible(it.isNotNullOrEmpty())
                    (this@SettingsFragment.preferenceScreen
                        .get<Preference>(SharedPreferencesHelper.PREFER_USE_FILES_PREF)
                            as? SwitchPreference)?.isChecked =
                        viewModel.preferUseFiles
                }
            }
        }

        onSharedPreferenceChangeListener =
            OnSharedPreferenceChangeListener { _, key ->
                when (key) {
                    SharedPreferencesHelper.THEME_PREF -> activity?.recreate()
                }
            }

        preferenceManager.sharedPreferences?.registerOnSharedPreferenceChangeListener(
            onSharedPreferenceChangeListener
        )
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        when (preference.key) {
            SETTING_STORAGE -> {
                if ((preference as? SwitchPreference)?.isChecked == true) {

                    if (activity?.checkSelfPermission("READ_EXTERNAL_STORAGE") == PackageManager.PERMISSION_DENIED) {
                        activity?.requestPermissions(
                            listOf("READ_EXTERNAL_STORAGE").toTypedArray(),
                            99
                        )
                        (preference as? SwitchPreference)?.isChecked = false
                    }

                    storageHelper?.openFolderPicker()
                    storageHelper?.onFolderSelected = { _, folder ->
                        viewModel.setBackupPath(
                            uri = folder.uri,
                            displayPath = folder
                                .getAbsolutePath(this@SettingsFragment.requireContext())
                        )
                    }
                } else {
                    viewModel.setBackupPath(null)
                }
            }

            SETTING_IMPORT -> {
                storageHelper?.openFilePicker()
                storageHelper?.onFileSelected = { _, files ->
                    lifecycleScope.launch {
                        try {
                            val treeUriIfPossible =
                                DocumentFileCompat.fromUri(requireContext(), files[0].uri)
                                    ?.toTreeDocumentFile(requireContext())?.uri
                                    ?: files[0].uri

                            viewModel.importList(treeUriIfPossible)

                            Toast.makeText(
                                activity,
                                getString(R.string.list_imported), Toast.LENGTH_SHORT
                            ).show()
                        } catch (e: Exception) {
                            Toast.makeText(
                                activity,
                                getString(R.string.error_import_list),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }

            SETTING_BACKUP_ALL -> {
                lifecycleScope.launch {
                    try {
                        viewModel.backupAllListsOnDevice()
                        Toast.makeText(
                            activity,
                            getString(R.string.success_all_backup), Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: Exception) {
                        Toast.makeText(
                            activity,
                            getString(R.string.error_all_backup), Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            SharedPreferencesHelper.PREFER_USE_FILES_PREF -> {
                viewModel.onPreferUseFiles()
            }

            SETTING_RELEASE_NOTE -> {
                onClickOnShowReleaseNote()
            }
        }
        return true
    }

    private fun displayDefaultPath() {

        this.preferenceScreen.get<Preference>(SETTING_STORAGE)
            ?.summary = viewModel.backupDisplayPath.value?.takeIf { it.isNotEmpty() }
            ?: getString(R.string.settings_backup_select)

        if (viewModel.syncFolderNotAccessible) {
            this.preferenceScreen.get<Preference>(SETTING_STORAGE)?.icon =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_settings_error_triangle)
            this.preferenceScreen.get<Preference>(SETTING_STORAGE)
                ?.summary = this.preferenceScreen.get<Preference>(SETTING_STORAGE)
                ?.summary.toString() + "\n\n" + getString(R.string.settings_error_try_switch_option)

            this.preferenceScreen.get<PreferenceCategory>(SETTING_CATEGORY_BACKUP)?.icon =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_settings_error_triangle)

        } else {
            this.preferenceScreen.get<Preference>(SETTING_STORAGE)?.icon =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_settings_save_24dp)

            this.preferenceScreen.get<PreferenceCategory>(SETTING_CATEGORY_BACKUP)?.icon = null
        }
    }

    private fun setBackupOptionsVisible(visible: Boolean) {
        this.preferenceScreen.get<Preference>(SharedPreferencesHelper.PREFER_USE_FILES_PREF)
            ?.isVisible = visible
        this.preferenceScreen.get<Preference>(SETTING_BACKUP_ALL)?.isVisible = visible
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        onSharedPreferenceChangeListener?.let {
            preferenceManager.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(it)
        }
    }
}

