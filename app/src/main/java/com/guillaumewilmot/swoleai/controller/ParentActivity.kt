package com.guillaumewilmot.swoleai.controller

import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding


abstract class ParentActivity<T : ViewBinding> : AppCompatActivity() {

    protected var binding: T? = null

    //TODO : screen tracking

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}