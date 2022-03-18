package com.guillaumewilmot.swoleai.controller

import android.app.Application
import android.content.res.Resources
import androidx.lifecycle.AndroidViewModel

abstract class ParentViewModel(
    application: Application
) : AndroidViewModel(application) {
    fun name(): String = this.javaClass.simpleName
}