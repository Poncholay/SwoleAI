package com.guillaumewilmot.swoleai.model

import android.content.Context
import android.util.Log
import com.google.gson.annotations.SerializedName
import com.guillaumewilmot.swoleai.util.DeserializerImpl
import com.guillaumewilmot.swoleai.util.DeserializerImpl.gsonDeserializeType
import java.io.Serializable

data class ExerciseBookModel(
    @SerializedName("id")
    val id: Int,
    @SerializedName("exercises")
    val exercises: List<ExerciseModel>,
) : Serializable {
    companion object {
        private const val serialVersionUID = 9L

        fun loadDefault(context: Context): ExerciseBookModel {
            DeserializerImpl.deserializeAsset<ExerciseBookModel>(
                context,
                "exerciseBook.json",
                gsonDeserializeType<ExerciseBookModel>()
            )?.let {
                Log.d("ExerciseBookModel", "Loaded default Exercise book")
                return it
            }
            Log.e("ExerciseBookModel", "Unable to load default Exercise book")
            return ExerciseBookModel(id = 1, exercises = listOf())
        }
    }
}