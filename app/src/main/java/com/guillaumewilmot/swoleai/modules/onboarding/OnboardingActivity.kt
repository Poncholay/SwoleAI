package com.guillaumewilmot.swoleai.modules.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT
import autodispose2.androidx.lifecycle.autoDispose
import com.guillaumewilmot.swoleai.controller.ParentActivity
import com.guillaumewilmot.swoleai.databinding.ActivityOnboardingBinding
import com.guillaumewilmot.swoleai.model.UserModel
import com.guillaumewilmot.swoleai.modules.home.HomeActivity
import com.guillaumewilmot.swoleai.modules.onboarding.greeting.OnboardingGreetingFragment
import com.guillaumewilmot.swoleai.modules.onboarding.username.OnboardingUsernameFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import me.relex.circleindicator.CircleIndicator3

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class OnboardingActivity : ParentActivity<ActivityOnboardingBinding>(),
    OnboardingGreetingFragment.OnboardingGreetingFragmentListener,
    OnboardingUsernameFragment.OnboardingUsernameFragmentListener {

    private lateinit var viewModel: OnboardingActivityViewModel
    private val viewPagerAdapter: ViewPagerAdapter?
        get() = binding?.viewPager?.adapter as? ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        viewModel = ViewModelProvider(this)[OnboardingActivityViewModel::class.java]

        binding?.viewPager?.isUserInputEnabled = false
        binding?.viewPager?.adapter = ViewPagerAdapter(this)

        viewModel.onboardingSteps
            .autoDispose(this, Lifecycle.Event.ON_DESTROY)
            .subscribe { remainingSteps ->
                viewPagerAdapter?.remainingSteps = remainingSteps
                binding?.viewPager?.offscreenPageLimit = remainingSteps.size.takeIf {
                    it > 0
                } ?: OFFSCREEN_PAGE_LIMIT_DEFAULT
            }
    }

    override fun onDestroy() {
        binding?.viewPager?.adapter = null
        super.onDestroy()
    }

    override fun attachIndicator(indicator: CircleIndicator3) {
        indicator.setViewPager(binding?.viewPager)
    }

    private fun navigateToNextFragment() =
        navigateToFragment(binding?.viewPager?.currentItem?.plus(1) ?: 0)

    private fun navigateToPreviousFragment() =
        navigateToFragment(binding?.viewPager?.currentItem?.minus(1) ?: 0)

    private fun navigateToFragment(position: Int) {
        binding?.viewPager?.setCurrentItem(position, true)
    }

    private fun navigateToHomeActivity() {
        startActivity(Intent(this, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
    }

    override fun onBackPressed() {
        if ((binding?.viewPager?.currentItem ?: 0) > 0) {
            navigateToPreviousFragment()
        }
    }

    /**
     * Children interface
     */

    //TODO
    override fun userOnboardingGreetingNext() = navigateToNextFragment()
    override fun userOnboardingUsernameNext() = navigateToHomeActivity()

    /**
     * Subclasses
     */

    enum class Step {
        GREETING,
        ENTER_NAME,

        //TODO
        ENTER_HEIGHT_BODYWEIGHT,
        ENTER_BIRTHDATE,
        ENTER_GENDER
    }

    internal inner class ViewPagerAdapter(
        activity: FragmentActivity
    ) : FragmentStateAdapter(activity) {
        var remainingSteps: List<Step> = listOf()
            set(value) {
                field = value
                notifyDataSetChanged()
            }

        override fun getItemCount(): Int = remainingSteps.size
        override fun createFragment(i: Int): Fragment = when (remainingSteps.getOrNull(i)) {
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
                    if (property.get(user).isNullOrBlank()) {
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