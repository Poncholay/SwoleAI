package com.guillaumewilmot.swoleai.controller

import androidx.fragment.app.Fragment

abstract class ParentFragment : Fragment() {

    //TODO : screen tracking

    /**
     * @return false if the back press has been taken care of
     */
    open fun onBackPressed(): BackResult = BackResult.NOT_HANDLED

    enum class BackResult {
        HANDLED,
        NOT_HANDLED
    }
}