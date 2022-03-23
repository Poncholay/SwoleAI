package com.guillaumewilmot.swoleai.util.fragmentBackstack

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.guillaumewilmot.swoleai.R
import com.guillaumewilmot.swoleai.controller.ParentFragment
import com.guillaumewilmot.swoleai.modules.onboarding.greeting.OnboardingGreetingFragment
import com.guillaumewilmot.swoleai.modules.onboarding.username.OnboardingUsernameFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.io.Serializable
import java.util.*
import kotlin.reflect.KClass

@ExperimentalCoroutinesApi
class FragmentBackstack {

    interface OnNoPreviousFragment {
        fun finishFragment(): Finishresult
    }

    interface FragmentTab : Serializable {
        fun root(): KClass<out ParentFragment>
        fun navId(): Int
    }

    enum class Tab(
        val root: KClass<out ParentFragment>,
        val navId: Int
    ) : FragmentTab {

        //TODO : Create the real fragments

        DASHBOARD(OnboardingGreetingFragment::class, R.id.navigation_dashboard),
        WORKOUT(OnboardingUsernameFragment::class, R.id.navigation_workout);

        override fun root(): KClass<out ParentFragment> = root
        override fun navId(): Int = navId

        companion object {
            fun valueOf(navId: Int): Tab = values().find { tab ->
                tab.navId == navId
            } ?: throw Exception("Invalid navId", null)
        }
    }

    enum class Animate {
        NONE,
        FORWARD,
        BACKWARD
    }

    enum class AnimType {
        FADE, SLIDE
    }

    enum class Finishresult {
        WENT_BACK,
        DID_NO_GO_BACK
    }

    private var _currentFragment: ParentFragment? = null
    private var _currentTab: FragmentTab = Tab.DASHBOARD
    private val _pool: Bundle = Bundle()
    private val _tagStacks = mapOf<FragmentTab, Stack<String>>(
        Tab.DASHBOARD to Stack(),
        Tab.WORKOUT to Stack()
    )

    val currentTab: FragmentTab
        get() = _currentTab

    val currentFragment: ParentFragment?
        get() = _currentFragment

    /**
     * Starts a new fragment in the current tab
     */
    fun push(
        fm: FragmentManager,
        newFragment: ParentFragment,
        animate: Animate,
        destinationTab: FragmentTab? = null,
        addToBackStack: Boolean = true
    ) {
        if (addToBackStack) {
            _currentFragment?.let { oldFragment ->
                try {
                    val tag = oldFragment.name() + _currentTab.toString()
                    fm.putFragment(_pool, tag, oldFragment)
                    _tagStacks[_currentTab]?.push(tag)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        }

        val newTab = destinationTab ?: _currentTab
        val animType = if (destinationTab == null) AnimType.SLIDE else AnimType.FADE
        transaction(
            fm,
            newFragment,
            newFragment.name() + newTab.toString(),
            animate,
            animType,
            addToBackStack
        )
        _currentFragment = newFragment
    }

    /**
     * Finishes the fragment without calling its onBackPressed implementation
     */
    fun forcePop(fm: FragmentManager): Finishresult = popOrHandle(
        fm,
        onNoPreviousFragment = null,
        skipFragmentOnBackPressed = true
    )

    /**
     * Performs the equivalent of a back button press.
     * Nothing happens if the fragment handles it.
     * Otherwise goes back to previous fragment.
     * If no previous fragment and home, executes callback.
     */
    fun popOrHandle(
        fm: FragmentManager,
        onNoPreviousFragment: OnNoPreviousFragment? = null,
        skipFragmentOnBackPressed: Boolean = false
    ): Finishresult {
        val currentTab = _currentTab

        val previousTag = try {
            _tagStacks[currentTab]?.peek()
        } catch (e: EmptyStackException) {
            null
        }

        if (!skipFragmentOnBackPressed) {
            if (_currentFragment?.onBackPressed() == ParentFragment.BackResult.HANDLED) {
                return Finishresult.WENT_BACK
            }
        }

        if (previousTag != null) {
            try {
                (fm.getFragment(_pool, previousTag) as? ParentFragment)?.let { previousFragment ->
                    _currentFragment = previousFragment
                    fm.popBackStack()
                    transaction(
                        fm,
                        previousFragment,
                        previousTag,
                        animate = Animate.BACKWARD,
                        addToBackStack = false
                    )
                    _tagStacks[currentTab]?.pop()
                    return Finishresult.WENT_BACK
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            return onNoPreviousFragment?.finishFragment() ?: Finishresult.DID_NO_GO_BACK
        }
        return Finishresult.DID_NO_GO_BACK
    }

    /**
     * Switch to another tab
     */
    fun selectTab(fm: FragmentManager, newTab: FragmentTab) {
        val newFragment = getFragmentForTab(fm, newTab)
        push(fm, newFragment, Animate.FORWARD, newTab)
        _currentTab = newTab
        try {
            _tagStacks[newTab]?.pop()?.let { lastTag ->
                //Remove last tag because it is now the current fragment
                _pool.remove(lastTag)
            }
        } catch (e: EmptyStackException) {
        }
    }

    /**
     * @param newFragment: new fragment
     */
    private fun transaction(
        fm: FragmentManager,
        newFragment: ParentFragment,
        newFragmentTag: String,
        animate: Animate,
        animType: AnimType = AnimType.SLIDE,
        addToBackStack: Boolean = true
    ) {
        fm.beginTransaction()
            .apply {
                when (animate) {
                    Animate.FORWARD -> when (animType) {
                        AnimType.FADE -> setCustomAnimations(
                            R.anim.fade_in,
                            R.anim.fade_out,
                            R.anim.fade_in,
                            R.anim.fade_out
                        )
                        AnimType.SLIDE -> setCustomAnimations(
                            R.anim.slide_left_in,
                            R.anim.slide_left_out,
                            R.anim.slide_right_in,
                            R.anim.slide_right_out
                        )
                    }
                    Animate.BACKWARD -> when (animType) {
                        AnimType.FADE -> setCustomAnimations(
                            R.anim.fade_in,
                            R.anim.fade_out,
                            R.anim.fade_in,
                            R.anim.fade_out
                        )
                        AnimType.SLIDE -> setCustomAnimations(
                            R.anim.slide_right_in,
                            R.anim.slide_right_out,
                            R.anim.slide_left_in,
                            R.anim.slide_left_out
                        )
                    }
                    else -> Unit
                }
            }
            .replace(R.id.fragmentContainer, newFragment, newFragmentTag)
            .apply {
                if (addToBackStack) {
                    addToBackStack(null)
                }
            }
            .commit()
    }

    private fun getFragmentForTab(fm: FragmentManager, tab: FragmentTab): ParentFragment {
        val tag = try {
            _tagStacks[tab]?.peek()
        } catch (e: EmptyStackException) {
            null
        }

        if (tag != null) {
            try {
                fm.getFragment(_pool, tag)?.let { fragment ->
                    (fragment as? ParentFragment)?.let { backableFragment ->
                        return backableFragment
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return tab.root().java.newInstance()
    }
}