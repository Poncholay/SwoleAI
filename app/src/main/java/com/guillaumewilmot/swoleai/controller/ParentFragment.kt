package com.guillaumewilmot.swoleai.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.viewbinding.ViewBinding
import com.trello.lifecycle4.android.lifecycle.AndroidLifecycle
import com.trello.rxlifecycle4.LifecycleProvider

abstract class ParentFragment<T : ViewBinding>(
    private val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> T
) : Fragment() {

    protected val lifecycleProvider: LifecycleProvider<Lifecycle.Event> =
        AndroidLifecycle.createLifecycleProvider(this)

    protected var binding: T? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = bindingInflater(inflater, container, false)
        return binding?.root
    }


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