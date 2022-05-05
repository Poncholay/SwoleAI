package com.guillaumewilmot.swoleai.modules.onboarding

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.guillaumewilmot.swoleai.modules.onboarding.greeting.OnboardingGreetingFragment
import com.guillaumewilmot.swoleai.modules.onboarding.stats.OnboardingStatsFragment
import com.guillaumewilmot.swoleai.modules.onboarding.username.OnboardingUsernameFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
internal class OnboardingViewpagerAdapter(
    activity: FragmentActivity
) : FragmentStateAdapter(activity) {
    var remainingSteps: List<OnboardingActivity.Step> = listOf()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = remainingSteps.size
    override fun createFragment(i: Int): Fragment = when (remainingSteps.getOrNull(i)) {
        OnboardingActivity.Step.GREETING -> OnboardingGreetingFragment()
        OnboardingActivity.Step.ENTER_NAME -> OnboardingUsernameFragment()
        OnboardingActivity.Step.ENTER_STATS -> OnboardingStatsFragment()
        //TODO :
        else -> OnboardingGreetingFragment()
    }
}