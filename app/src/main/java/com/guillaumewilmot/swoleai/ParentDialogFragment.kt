package com.guillaumewilmot.swoleai

import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class ParentDialogFragment<T : ViewBinding> : BottomSheetDialogFragment() {

    protected var binding: T? = null

    //TODO : screen tracking

    fun name(): String = this.javaClass.simpleName

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}