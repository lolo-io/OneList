package com.lolo.io.onelist.feature.settings

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.anggrayudi.storage.file.getAbsolutePath
import com.lolo.io.onelist.R
import com.lolo.io.onelist.databinding.FragmentSettingsBinding
import com.lolo.io.onelist.feature.lists.utils.StorageHelperHolder
import isNotNullOrEmpty
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.getViewModel

class SettingsFragment() : PreferenceFragmentCompat() {

    private val viewModel by lazy { getViewModel<SettingsFragmentViewModel>() }

    private var storageHolder: StorageHelperHolder? = null

    private var _binding: FragmentSettingsBinding? = null
    private val binding: FragmentSettingsBinding
        get() = _binding!!

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
        setBackupOptionsVisible(viewModel.backupDisplayPath.value != null)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        storageHolder = activity as? StorageHelperHolder
    }

    override fun onDetach() {
        super.onDetach()
        storageHolder = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentSettingsBinding.bind(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? AppCompatActivity)?.let { activity ->
            binding.customToolbar.let { activity.setSupportActionBar(it) }
            activity.supportActionBar?.apply {
                title = getString(R.string.settings)
                setDisplayShowHomeEnabled(true)
                setDisplayHomeAsUpEnabled(true)
            }
        }


        this.preferenceScreen.get<Preference>("version")?.summary = viewModel.version

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.backupDisplayPath.collect {
                    (this@SettingsFragment.preferenceScreen.get<Preference>("storage") as? SwitchPreference)?.isChecked =
                        it != null
                    displayDefaultPath()
                    setBackupOptionsVisible(it.isNotNullOrEmpty())
                }
            }
        }

        preferenceManager.sharedPreferences?.registerOnSharedPreferenceChangeListener { _, key ->
            when (key) {
                "theme" -> activity?.recreate()
            }
        }
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        when (preference.key) {
            "storage" -> {
                if ((preference as? SwitchPreference)?.isChecked == true) {


                    if (activity?.checkSelfPermission("READ_EXTERNAL_STORAGE") == PackageManager.PERMISSION_DENIED) {
                        activity?.requestPermissions(
                            listOf("READ_EXTERNAL_STORAGE").toTypedArray(),
                            99
                        )
                        (preference as? SwitchPreference)?.isChecked = false
                    }

                    storageHolder?.storageHelper?.openFolderPicker()
                    storageHolder?.storageHelper?.onFolderSelected = { _, folder ->
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

            "import" -> {
                storageHolder?.storageHelper?.openFilePicker()
                storageHolder?.storageHelper?.onFileSelected = { _, files ->
                    lifecycleScope.launch {
                        viewModel.importList(files[0].uri)
                    }
                }

            }

            "backup_all" -> {
                viewModel.backupAllListsOnDevice()
            }

            "preferUseFiles" -> {
                viewModel.onPreferUseFiles()
            }


            "releaseNote" -> showReleaseNote(requireActivity())
        }
        return true
    }

    private fun displayDefaultPath() {

        this.preferenceScreen.get<Preference>("storage")
            ?.summary = viewModel.backupDisplayPath.value?.takeIf { it.isNotEmpty() }
            ?: getString(R.string.settings_backup_select)

        if (viewModel.syncFolderNotAccessible) {
            this.preferenceScreen.get<Preference>("storage")?.icon =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_error_triangle)
            this.preferenceScreen.get<Preference>("storage")
                ?.summary = this.preferenceScreen.get<Preference>("storage")
                ?.summary.toString() + "\n\n" + getString(R.string.settings_error_try_switch_option)

            this.preferenceScreen.get<PreferenceCategory>("cat_backup")?.icon =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_error_triangle)

        } else {
            this.preferenceScreen.get<Preference>("storage")?.icon =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_save_accent_24dp)

            this.preferenceScreen.get<PreferenceCategory>("cat_backup")?.icon = null
        }
    }

    private fun setBackupOptionsVisible(visible: Boolean) {
        this.preferenceScreen.get<Preference>("preferUseFiles")?.isVisible = visible
        this.preferenceScreen.get<Preference>("backup_all")?.isVisible = visible
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

