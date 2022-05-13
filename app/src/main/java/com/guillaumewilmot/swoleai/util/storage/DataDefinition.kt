package com.guillaumewilmot.swoleai.util.storage

enum class DataDefinition(
    val key: String
) {
    USER("storage_user"),
    PROGRAM("storage_program"),
    SELECTED_SESSION("storage_selected_session"),
    ACTIVE_SESSION("storage_active_session");
}