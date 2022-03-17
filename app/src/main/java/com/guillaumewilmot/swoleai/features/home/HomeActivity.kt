package com.guillaumewilmot.swoleai.features.home

import android.os.Bundle
import android.os.PersistableBundle
import com.guillaumewilmot.swoleai.R
import com.guillaumewilmot.swoleai.controller.ParentActivity

class HomeActivity : ParentActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        setContentView(R.layout.activity_home)
    }
}