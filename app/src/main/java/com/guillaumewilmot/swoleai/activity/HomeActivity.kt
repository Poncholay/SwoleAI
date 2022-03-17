package com.guillaumewilmot.swoleai.activity

import android.os.Bundle
import android.os.PersistableBundle
import com.guillaumewilmot.swoleai.R

class HomeActivity : ParentActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        setContentView(R.layout.activity_home)
    }
}