package com.guillaumewilmot.swoleai.features.onboarding

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.guillaumewilmot.swoleai.R
import com.guillaumewilmot.swoleai.features.home.HomeActivity
import com.guillaumewilmot.swoleai.controller.ParentActivity
import com.guillaumewilmot.swoleai.databinding.ActivityOnboardingBinding
import com.guillaumewilmot.swoleai.features.onboarding.greeting.OnboardingGreetingFragment

class OnboardingActivity : ParentActivity() {

    private lateinit var binding: ActivityOnboardingBinding

    private val pagerAdapter by lazy {
        ViewPagerAdapter(supportFragmentManager)
    }

    val remainingSteps: List<Step> by lazy {
        listOf(
            Step.GREETING,
            Step.ENTER_NAME,
            Step.ENTER_PHONE,
            Step.ENTER_BIRTHDATE,
            Step.ENTER_EMERGENCY
        )
    }

    enum class Step {
        GREETING,
        ENTER_NAME,
        ENTER_PHONE,
        ENTER_BIRTHDATE,
        ENTER_EMERGENCY
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        binding = ActivityOnboardingBinding.inflate(layoutInflater)

        //TODO : Just trying null safety
        binding.viewPager.offscreenPageLimit = Step.values().size

        setContentView(binding.root)

        setContentView(R.layout.activity_onboarding)
        setupViewpager()
    }

    private fun setupViewpager() {
        binding.viewPager.offscreenPageLimit = remainingSteps.size
        binding.viewPager.adapter = pagerAdapter
    }

    private fun navigateToNextFragment() {
        binding.viewPager.let {
            if (it.currentItem + 1 < it.childCount) {
                try {
                    it.setCurrentItem(it.currentItem + 1, false)
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun navigateToPreviousFragment() {
        binding.viewPager.let {
            if (it.currentItem - 1 >= 0) {
                it.setCurrentItem(it.currentItem - 1, false)
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

    fun createFragment(i: Int): Fragment = when (remainingSteps.getOrNull(i)) {
        Step.GREETING -> OnboardingGreetingFragment()
        else -> OnboardingGreetingFragment()
//        Step.ENTER_NAME -> UserOnboardingNameFragment()
//        Step.ENTER_PHONE -> UserOnboardingPhoneFragment()
//        Step.ENTER_BIRTHDATE -> UserOnboardingDateOfBirthFragment()
//        Step.ENTER_EMERGENCY -> UserOnboardingEmergencyFragment()
//        else -> OnboardingGreetingFragment().also { navigateToHomeActivity() }
    }

    internal inner class ViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment = createFragment(position)
        override fun getCount() = remainingSteps.size
    }
}