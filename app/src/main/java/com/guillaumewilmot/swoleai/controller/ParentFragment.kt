package com.guillaumewilmot.swoleai.controller

import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import kotlin.reflect.KFunction2

abstract class ParentFragment<T : ViewBinding> : Fragment() {

    protected var binding: T? = null

    protected val adapterCallbackWrapper = object : ParentActivity.AdapterCallbackWrapper {
        override fun wrap(onClickFunction: KFunction2<ParentActivity, ParentFragment<*>, Unit>) {
            (activity as? ParentActivity)?.let { activity ->
                onClickFunction.invoke(activity, this@ParentFragment)
            }
        }
    }

    //TODO : screen tracking

    fun name(): String = this.javaClass.simpleName

    /**
     * @return false if the back press has been taken care of
     */
    open fun onBackPressed(): BackResult = BackResult.NOT_HANDLED

    enum class BackResult {
        HANDLED,
        NOT_HANDLED
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}