package com.guillaumewilmot.swoleai.modules.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import autodispose2.androidx.lifecycle.autoDispose
import com.afollestad.viewpagerdots.DotsIndicator
import com.guillaumewilmot.swoleai.controller.ParentActivity
import com.guillaumewilmot.swoleai.databinding.ActivityOnboardingBinding
import com.guillaumewilmot.swoleai.model.UserModel
import com.guillaumewilmot.swoleai.modules.home.HomeActivity
import com.guillaumewilmot.swoleai.modules.onboarding.greeting.OnboardingGreetingFragment
import com.guillaumewilmot.swoleai.modules.onboarding.username.OnboardingUsernameFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class OnboardingActivity :
    ParentActivity<ActivityOnboardingBinding>(ActivityOnboardingBinding::inflate),
    OnboardingGreetingFragment.OnboardingGreetingFragmentListener,
    OnboardingUsernameFragment.OnboardingUsernameFragmentListener {

    private lateinit var viewModel: OnboardingActivityViewModel
    private var pagerAdapter: ViewPagerAdapter? = null

//    val remainingSteps: List<Step> by lazy {
//        OnboardingActivity.onboardingSteps(user.value)
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[OnboardingActivityViewModel::class.java]

        viewModel.onboardingSteps
            .autoDispose(this, Lifecycle.Event.ON_DESTROY)
            .subscribe { remainingSteps ->
                pagerAdapter = ViewPagerAdapter(supportFragmentManager, remainingSteps)
                binding.viewPager.adapter = pagerAdapter
                binding.viewPager.offscreenPageLimit = remainingSteps.size
            }
    }

    override fun attachIndicator(dotsIndicator: DotsIndicator) {
        dotsIndicator.attachViewPager(binding.viewPager)
    }

    private fun navigateToNextFragment() = navigateToFragment(binding.viewPager.currentItem + 1)
    private fun navigateToPreviousFragment() = navigateToFragment(binding.viewPager.currentItem - 1)
    private fun navigateToFragment(position: Int) {
        binding.viewPager.let {
            if (position >= 0 && position < it.childCount) {
                try {
                    it.currentItem = position
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun navigateToHomeActivity() {
        startActivity(Intent(this, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
    }

    override fun onBackPressed() {
        if (binding.viewPager.currentItem > 0) {
            navigateToPreviousFragment()
        }
    }

    /**
     * Children interface
     */

    //TODO
    override fun userOnboardingGreetingNext() = navigateToNextFragment()
    override fun userOnboardingUsernameNext() {}

    /**
     * Subclasses
     */

    enum class Step {
        GREETING,
        ENTER_NAME,
        ENTER_HEIGHT_BODYWEIGHT,

        //        ENTER_BIRTHDATE,
        ENTER_GENDER
    }

    internal inner class ViewPagerAdapter(
        manager: FragmentManager,
        private val remainingSteps: List<Step>
    ) :
        FragmentPagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment = createFragment(position)
        override fun getCount() = remainingSteps.size

        private fun createFragment(i: Int): Fragment = when (remainingSteps.getOrNull(i)) {
            Step.GREETING -> OnboardingGreetingFragment()
            Step.ENTER_NAME -> OnboardingUsernameFragment()
            //TODO :
            else -> OnboardingGreetingFragment()
        }
    }

    companion object {
        private val REQUIRED_PROPERTIES = mapOf(
            Step.ENTER_NAME to listOf(UserModel::username),
        )

        fun onboardingSteps(user: UserModel): List<Step> = REQUIRED_PROPERTIES
            .map { mapEntry ->
                mapEntry.value.forEach { property ->
                    if (property.get(user) == null) {
                        return@map mapEntry.key
                    }
                }
                return@map null
            }
            .filterNotNull()
            .toMutableList()
            .apply {
                if (isNotEmpty()) {
                    add(0, Step.GREETING)
                }
            }
    }
}