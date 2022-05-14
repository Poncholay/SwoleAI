package com.guillaumewilmot.swoleai.controller

import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class ParentDialog<T : ViewBinding> : BottomSheetDialogFragment() {

    protected var binding: T? = null

    //TODO : screen tracking

    fun name(): String = this.javaClass.simpleName

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}