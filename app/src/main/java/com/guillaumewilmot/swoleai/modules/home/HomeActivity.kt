package com.guillaumewilmot.swoleai.modules.home

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.splashscreen.SplashScreenViewProvider
import androidx.lifecycle.ViewModelProvider
import com.guillaumewilmot.swoleai.controller.ParentActivity
import com.guillaumewilmot.swoleai.databinding.ActivityHomeBinding
import com.guillaumewilmot.swoleai.modules.onboarding.OnboardingActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class HomeActivity :
    ParentActivity<ActivityHomeBinding>(ActivityHomeBinding::inflate) {

    private lateinit var viewModel: HomeActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        configureSplashscreen()
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[HomeActivityViewModel::class.java].apply {
            titleText.compose(lifecycleProvider.bindToLifecycle())
                .subscribe { binding.title.text = it }
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.apply {
            redirectToOnboarding.compose(lifecycleProvider.bindToLifecycle())
                .subscribe(
                    { redirectToOnboarding() },
                    { error -> error.printStackTrace() }
                )

            userTimer.compose(lifecycleProvider.bindToLifecycle())
                .subscribe {
                    updateUser(it)
                }
        }
    }

    /** Called once after onResume */
    private fun splashFinished(splashScreenProvider: SplashScreenViewProvider) {
        splashScreenProvider.remove()

    }

    private fun redirectToOnboarding() {
        startActivity(Intent(this, OnboardingActivity::class.java))
    }

    private fun configureSplashscreen() {
        installSplashScreen().apply {
            setOnExitAnimationListener { splashScreenProvider ->
                val iconView = (splashScreenProvider.view as? ViewGroup)?.getChildAt(0)
                if (iconView != null) {
                    val duration = 1000L
                    //Animate icon
                    ObjectAnimator.ofFloat(
                        iconView,
                        View.TRANSLATION_Y,
                        0f,
                        splashScreenProvider.view.height.toFloat()
                    ).apply {
                        this.duration = duration
                    }.start()

                    //Fade background
                    ObjectAnimator.ofFloat(
                        splashScreenProvider.view,
                        View.ALPHA,
                        1f,
                        0f
                    ).apply {
                        this.duration = duration
                        doOnEnd {
                            // Call SplashScreenView.remove at the end of your custom animation.
                            splashFinished(splashScreenProvider)
                        }
                    }.start()
                } else {
                    // Can't access the icon so we skip the animation
                    splashFinished(splashScreenProvider)
                }
            }
        }
    }
}