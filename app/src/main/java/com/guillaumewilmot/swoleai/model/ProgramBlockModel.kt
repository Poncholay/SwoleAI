package com.guillaumewilmot.swoleai.model

import com.google.gson.annotations.SerializedName
import com.guillaumewilmot.swoleai.R
import java.io.Serializable

data class ProgramBlockModel(
    @SerializedName("id")
    val id: Int,
    @SerializedName("type")
    val type: BlockType,
    @SerializedName("weeks")
    val weeks: List<ProgramWeekModel>
) : Serializable {
    enum class BlockType(val nameId: Int, val colorId: Int) {
        HYPERTROPHY(R.string.app_block_type_hypertrophy, R.color.hypertrophy),
        STRENGTH(R.string.app_block_type_strength, R.color.strength),
        PEAKING(R.string.app_block_type_peaking, R.color.peaking);
    }

    companion object {
        private const val serialVersionUID = 2L
    }
}