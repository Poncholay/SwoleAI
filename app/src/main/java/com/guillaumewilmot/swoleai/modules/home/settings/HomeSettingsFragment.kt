package com.guillaumewilmot.swoleai.modules.home.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.fragment.app.viewModels
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider
import autodispose2.autoDispose
import com.guillaumewilmot.swoleai.BuildConfig
import com.guillaumewilmot.swoleai.R
import com.guillaumewilmot.swoleai.controller.ParentFragment
import com.guillaumewilmot.swoleai.databinding.FragmentHomeSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class HomeSettingsFragment : ParentFragment<FragmentHomeSettingsBinding>() {

    private val viewModel: HomeSettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentHomeSettingsBinding.inflate(
        inflater,
        container,
        false
    ).also { binding ->
        viewModel.loaderVisibility
            .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
            .subscribe {
                this.binding?.toolbarLayout?.toolbarContent?.loader?.visibility = it
            }

        this.binding = binding
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ui()
    }

    private fun ui() {
        binding?.toolbarLayout?.toolbarContent?.toolbarTitle?.text = getString(
            R.string.app_home_settings_title_text
        )
        binding?.toolbarLayout?.toolbarContent?.backButton?.visibility = View.VISIBLE
        binding?.toolbarLayout?.toolbarContent?.backButton?.setOnClickListener {
            activity?.onBackPressed()
        }

        binding?.optionDeleteAccount?.setOnClickListener {
            viewModel.deleteAccount()
                .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
                .subscribe {
                    activity?.let {
                        Toast.makeText(
                            it,
                            getString(R.string.app_home_settings_delete_account_success_text),
                            LENGTH_SHORT,
                        ).show()
                    }
                }
        }
        binding?.optionDarkMode?.setOnClickListener {
            //TODO : Dark mode screen
        }

        binding?.appVersion?.text = getString(
            R.string.app_home_settings_app_version_text,
            "${BuildConfig.VERSION_NAME} - ${BuildConfig.VERSION_CODE}"
        )
    }
}