package com.guillaumewilmot.swoleai.util.validation

interface ValidatesString {
    fun validate(string: String): Boolean
    fun error(): String
}