package com.guillaumewilmot.swoleai.util.storage

enum class DataDefinition(
    val key: String
) {
    USER("storage_user"),
    PROGRAM("storage_program"),
    SELECTED_SESSION_ID("storage_selected_session_id");
}