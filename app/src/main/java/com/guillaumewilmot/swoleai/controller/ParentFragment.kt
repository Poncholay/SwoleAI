package com.guillaumewilmot.swoleai.controller

import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class ParentFragment<T : ViewBinding> : Fragment() {

    protected var binding: T? = null

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