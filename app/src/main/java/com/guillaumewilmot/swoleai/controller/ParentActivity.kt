package com.guillaumewilmot.swoleai.controller

import androidx.appcompat.app.AppCompatActivity
import kotlin.reflect.KFunction2


abstract class ParentActivity : AppCompatActivity() {

    //TODO : screen tracking

    interface AdapterCallbackWrapper {
        fun wrap(onClickFunction: KFunction2<ParentActivity, ParentFragment<*>, Unit>)
    }

    interface AdapterCallback {
        fun onClick(activity: ParentActivity, fragment: ParentFragment<*>) {
            //Implement or leave empty
        }
    }
}