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
class HomeActivity : ParentActivity() {

    @Inject
    lateinit var fragmentBackstack: FragmentBackstack

    private lateinit var binding: ActivityHomeBinding
    private lateinit var viewModel: HomeActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        configureSplashscreen()
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[HomeActivityViewModel::class.java]

        initBottomNavigationBar()
        handleSelectTab(FragmentBackstack.Tab.DASHBOARD)
    }

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
        startActivity(Intent(this, OnboardingActivity::class.java))
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
        binding.bottomNavigationView.apply {
            setOnItemSelectedListener { item ->
                handleSelectTab(FragmentBackstack.Tab.valueOf(item.itemId))
                true
            }
            setOnItemReselectedListener {
                fragmentBackstack.popOrHandle(supportFragmentManager, null)
            }
        }
    }

    /** Programmatically select a tab in the bottomNavigationBar */
    fun selectTab(tab: FragmentBackstack.FragmentTab) {
        binding.bottomNavigationView.selectedItemId = tab.navId()
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
                override fun finishFragment(currentTab: FragmentBackstack.FragmentTab): FragmentBackstack.Finishresult {
                    return if (currentTab != FragmentBackstack.Tab.DASHBOARD) {
                        selectTab(FragmentBackstack.Tab.DASHBOARD)
                        FragmentBackstack.Finishresult.WENT_BACK
                    } else {
                        FragmentBackstack.Finishresult.DID_NO_GO_BACK
                    }
                }
            }
        )

        if (finishResult == FragmentBackstack.Finishresult.DID_NO_GO_BACK) {
            //No more more fragments, already in dashboard, quit the app
            finish()
        }
    }
}