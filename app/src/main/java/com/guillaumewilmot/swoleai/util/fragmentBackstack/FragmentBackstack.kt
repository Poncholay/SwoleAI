package com.guillaumewilmot.swoleai.util.fragmentBackstack

import androidx.fragment.app.FragmentManager
import com.guillaumewilmot.swoleai.R
import com.guillaumewilmot.swoleai.controller.ParentFragment
import com.guillaumewilmot.swoleai.modules.home.dashboard.HomeDashboardFragment
import com.guillaumewilmot.swoleai.modules.home.sessionsummary.HomeSessionSummaryFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.io.Serializable
import kotlin.reflect.KClass

@ExperimentalCoroutinesApi
interface FragmentBackstack {

    interface OnNoPreviousFragment {
        fun handleNoPreviousFragment(currentTab: FragmentTab): Finishresult
    }

    interface FragmentTab : Serializable {
        fun root(): KClass<out ParentFragment<*>>
        fun navId(): Int
    }

    enum class Tab(
        val root: KClass<out ParentFragment<*>>,
        val navId: Int
    ) : FragmentTab {

        DASHBOARD(HomeDashboardFragment::class, R.id.navigation_dashboard),
        SESSION(HomeSessionSummaryFragment::class, R.id.navigation_session);

        override fun root(): KClass<out ParentFragment<*>> = root
        override fun navId(): Int = navId

        companion object {
            fun valueOf(navId: Int): Tab = values().find { tab ->
                tab.navId == navId
            } ?: throw Exception("Invalid navId", null)
        }
    }

    enum class Finishresult {
        HANDLED,
        NOT_HANDLED
    }

    val currentTab: FragmentTab

    /**
     * Starts a new fragment in the current tab
     */
    fun push(
        fragmentManager: FragmentManager,
        newFragment: ParentFragment<*>,
        addToBackStack: Boolean = true
    )

    /**
     * Finishes the fragment without calling its onBackPressed implementation
     */
    fun forcePop(fragmentManager: FragmentManager): Finishresult

    /**
     * Performs the equivalent of a back button press.
     * Nothing happens if the fragment handles it.
     * Otherwise goes back to previous fragment.
     * If no previous fragment and home, executes callback.
     */
    fun popOrHandle(
        fragmentManager: FragmentManager,
        onNoPreviousFragment: OnNoPreviousFragment? = null,
        skipFragmentOnBackPressed: Boolean = false
    ): Finishresult

    /**
     * Switch to another tab
     */
    fun selectTab(fragmentManager: FragmentManager, newTab: FragmentTab)

    /**
     * Switch to another tab and go back to the root of that tab
     */
    fun goToTabRoot(fragmentManager: FragmentManager)
}