package com.guillaumewilmot.swoleai.controller.factory

import android.app.Application
import android.content.res.Resources
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory(
    private val application: Application,
    private val r: Resources
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = modelClass.getConstructor(
        Application::class.java,
        Resources::class.java
    ).newInstance(application, r)
}