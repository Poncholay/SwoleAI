package com.guillaumewilmot.swoleai.util.fragmentBackstack

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.guillaumewilmot.swoleai.R
import com.guillaumewilmot.swoleai.controller.ParentFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.lang.ref.WeakReference
import java.util.*

@ExperimentalCoroutinesApi
class FragmentBackstackImpl : FragmentBackstack {
    private var _currentFragment: WeakReference<ParentFragment<*>>? = null
    private var _currentTab: FragmentBackstack.FragmentTab = FragmentBackstack.Tab.DASHBOARD
    private val _pool: Bundle = Bundle()
    private val _tagStacks = mapOf<FragmentBackstack.FragmentTab, Stack<String>>(
        FragmentBackstack.Tab.DASHBOARD to Stack(),
        FragmentBackstack.Tab.SESSION to Stack()
    )

    override val currentTab: FragmentBackstack.FragmentTab
        get() = _currentTab

    /**
     * Starts a new fragment in the current tab
     */
    override fun push(
        fragmentManager: FragmentManager,
        newFragment: ParentFragment<*>,
        addToBackStack: Boolean,
    ) {
        val fragmentTag = "${newFragment.name()}$_currentTab"
        transaction(
            fragmentManager,
            newFragment,
            fragmentTag,
            addToBackStack
        )
        if (addToBackStack) {
            _tagStacks[_currentTab]?.push(fragmentTag)
            fragmentManager.putFragment(_pool, fragmentTag, newFragment)
        }

        _currentFragment = WeakReference(newFragment)
    }

    /**
     * Finishes the fragment without calling its onBackPressed implementation
     */
    override fun forcePop(
        fragmentManager: FragmentManager
    ): FragmentBackstack.Finishresult = popOrHandle(
        fragmentManager,
        onNoPreviousFragment = null,
        skipFragmentOnBackPressed = true
    )

    /**
     * Performs the equivalent of a back button press.
     * Nothing happens if the fragment handles it.
     * Otherwise goes back to previous fragment.
     * If no previous fragment and home, executes callback.
     */
    override fun popOrHandle(
        fragmentManager: FragmentManager,
        onNoPreviousFragment: FragmentBackstack.OnNoPreviousFragment?,
        skipFragmentOnBackPressed: Boolean
    ): FragmentBackstack.Finishresult {
        val currentTab = _currentTab
        val currentFragment = _currentFragment

        if (
            !skipFragmentOnBackPressed &&
            currentFragment?.get()?.onBackPressed() == ParentFragment.BackResult.HANDLED
        ) {
            return FragmentBackstack.Finishresult.HANDLED
        }

        //We never pop the root fragment of a tab.
        val currentStackSize = _tagStacks[currentTab]?.size ?: 0
        val previousTag = if (currentStackSize > 1) {
            try {
                _tagStacks[currentTab]?.peek()
            } catch (e: EmptyStackException) {
                null
            }
        } else {
            null
        }

        if (previousTag != null) {
            try {
                (fragmentManager.getFragment(
                    _pool,
                    previousTag
                ) as? ParentFragment<*>)?.let { previousFragment ->
                    _currentFragment = WeakReference(previousFragment)
                    fragmentManager.popBackStack()
                    _tagStacks[currentTab]?.pop()
                    return FragmentBackstack.Finishresult.HANDLED
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            return onNoPreviousFragment?.handleNoPreviousFragment(
                currentTab
            ) ?: FragmentBackstack.Finishresult.NOT_HANDLED
        }

        return FragmentBackstack.Finishresult.NOT_HANDLED
    }

    /**
     * Switch to another tab
     */
    override fun selectTab(
        fragmentManager: FragmentManager,
        newTab: FragmentBackstack.FragmentTab
    ) {
        val currentTabRootTag = _tagStacks[_currentTab]?.getOrNull(0)
        if (currentTabRootTag != null) {
            fragmentManager.saveBackStack(currentTabRootTag)
        }

        _currentTab = newTab
        val newTabRootTag = _tagStacks[newTab]?.getOrNull(0)
        if (newTabRootTag != null) {
            fragmentManager.restoreBackStack(newTabRootTag)
        } else {
            val rootFragment = newTab.root().java.newInstance()
            push(fragmentManager, rootFragment)
        }
    }

    /**
     * Switch to another tab and go back to the root of that tab
     */
    override fun goToTabRoot(fragmentManager: FragmentManager) {
        val tagStack = _tagStacks[_currentTab]
        tagStack?.getOrNull(0)?.let { currentTabRootTag ->
            fragmentManager.popBackStack(currentTabRootTag, 0)
            tagStack.clear()
            tagStack.push(currentTabRootTag)
        }
    }

    /**
     * @param fragment: new fragment
     */
    private fun transaction(
        fragmentManager: FragmentManager,
        fragment: ParentFragment<*>,
        fragmentTag: String,
        addToBackStack: Boolean
    ) {
        fragmentManager.commit {
            setReorderingAllowed(true)
            setCustomAnimations(
                R.anim.slide_left_in,
                R.anim.slide_left_out,
                R.anim.slide_right_in,
                R.anim.slide_right_out
            )
            if (addToBackStack) {
                addToBackStack(fragmentTag)
            }
            replace(R.id.fragmentContainer, fragment, fragmentTag)
        }
    }
}