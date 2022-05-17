package com.guillaumewilmot.swoleai.modules.home

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.splashscreen.SplashScreenViewProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import autodispose2.androidx.lifecycle.autoDispose
import com.guillaumewilmot.swoleai.controller.ParentActivity
import com.guillaumewilmot.swoleai.databinding.ActivityHomeBinding
import com.guillaumewilmot.swoleai.modules.onboarding.OnboardingActivity
import com.guillaumewilmot.swoleai.util.fragmentBackstack.FragmentBackstack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class HomeActivity : ParentActivity<ActivityHomeBinding>() {

    @Inject
    lateinit var fragmentBackstack: FragmentBackstack

    private lateinit var viewModel: HomeActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        configureSplashscreen()
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        viewModel = ViewModelProvider(this)[HomeActivityViewModel::class.java]

        initBottomNavigationBar()
        selectTab(getTabNumber(savedInstanceState), firstSelect = true)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(CURRENT_TAB, fragmentBackstack.currentTab)
    }

    /**
     * Fetches tab number if provided by parent activity
     */
    private fun getTabNumber(savedInstanceState: Bundle?): FragmentBackstack.FragmentTab =
        (savedInstanceState?.getSerializable(CURRENT_TAB) as? FragmentBackstack.FragmentTab)
            ?: (intent?.extras?.getSerializable(CURRENT_TAB) as? FragmentBackstack.FragmentTab)?.also {
                intent?.removeExtra(CURRENT_TAB)
            } ?: FragmentBackstack.Tab.DASHBOARD


    /** Called once after onResume and the 1 second splash screen exit animation */
    private fun splashFinished(splashScreenProvider: SplashScreenViewProvider) {
        splashScreenProvider.remove()

        /**
         * We subscribe here to avoid redirecting before splash screen is done
         */
        viewModel.redirectToOnboarding
            .autoDispose(this, Lifecycle.Event.ON_DESTROY)
            .subscribe {
                redirectToOnboarding()
            }
    }

    private fun redirectToOnboarding() {
        startActivity(Intent(this, OnboardingActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
    }

    /** Configure splash screen animation */
    private fun configureSplashscreen() {
        installSplashScreen().apply {
            setOnExitAnimationListener { splashScreenProvider ->
                val iconView = (splashScreenProvider.view as? ViewGroup)?.getChildAt(0)
                if (iconView != null) {
                    val duration = 1000L
                    /** Animate icon */
                    ObjectAnimator.ofFloat(
                        iconView,
                        View.TRANSLATION_Y,
                        0f,
                        splashScreenProvider.view.height.toFloat()
                    ).apply {
                        this.duration = duration
                    }.start()

                    /** Fade background */
                    ObjectAnimator.ofFloat(
                        splashScreenProvider.view,
                        View.ALPHA,
                        1f,
                        0f
                    ).apply {
                        this.duration = duration
                        doOnEnd {
                            /** Call SplashScreenView.remove at the end of your custom animation. */
                            splashFinished(splashScreenProvider)
                        }
                    }.start()
                } else {
                    /** Can't access the icon so we skip the animation */
                    splashFinished(splashScreenProvider)
                }
            }
        }
    }

    /**
     * Navigation
     */

    private fun initBottomNavigationBar() {
        binding?.bottomNavigationView?.apply {
            setOnItemSelectedListener { item ->
                handleSelectTab(FragmentBackstack.Tab.valueOf(item.itemId))
                true
            }
            setOnItemReselectedListener {
                fragmentBackstack.popOrHandle(supportFragmentManager, onNoPreviousFragment = null)
            }
        }
    }

    /** Programmatically select a tab in the bottomNavigationBar */
    fun selectTab(tab: FragmentBackstack.FragmentTab, firstSelect: Boolean = false) {
        val currentTab = binding?.bottomNavigationView?.selectedItemId
        if (currentTab != tab.navId()) {
            binding?.bottomNavigationView?.selectedItemId = tab.navId()
        }

        //When we open the app the tab is selected by default and the fragment is not loaded
        if (firstSelect && tab == FragmentBackstack.Tab.DASHBOARD) {
            handleSelectTab(tab)
        }
    }

    /** Programmatically select a tab in the bottomNavigationBar */
    fun selectTabAndGoToRoot(tab: FragmentBackstack.FragmentTab) {
        val current = binding?.bottomNavigationView?.selectedItemId
        if (current != tab.navId()) {
            binding?.bottomNavigationView?.selectedItemId = tab.navId()
        }
        fragmentBackstack.goToTabRoot(supportFragmentManager)
    }

    private fun handleSelectTab(tab: FragmentBackstack.FragmentTab) {
        fragmentBackstack.selectTab(supportFragmentManager, tab)
    }

    /**
     * Go back one fragment in the backstack of the selected navigation tab.
     * If already at the root of the selected navigation tab, switch to the Dashboard tab.
     * If already at the root of the Dashboard tab, minimize the app.
     */
    override fun onBackPressed() {
        val finishResult = fragmentBackstack.popOrHandle(
            supportFragmentManager,
            onNoPreviousFragment = object : FragmentBackstack.OnNoPreviousFragment {
                override fun handleNoPreviousFragment(
                    currentTab: FragmentBackstack.FragmentTab
                ): FragmentBackstack.Finishresult = if (
                    currentTab != FragmentBackstack.Tab.DASHBOARD
                ) {
                    selectTab(FragmentBackstack.Tab.DASHBOARD)
                    FragmentBackstack.Finishresult.HANDLED
                } else {
                    FragmentBackstack.Finishresult.NOT_HANDLED
                }
            }
        )

        if (finishResult == FragmentBackstack.Finishresult.NOT_HANDLED) {
            //No more more fragments, already in dashboard, quit the app
            finish()
        }
    }

    companion object {
        const val CURRENT_TAB = "tab"
    }
}