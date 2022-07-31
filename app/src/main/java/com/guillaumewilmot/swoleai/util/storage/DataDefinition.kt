package com.guillaumewilmot.swoleai.util.storage

enum class DataDefinition(
    val key: String
) {
    USER("storage_user"),
    PROGRAM("storage_program"),
    EXERCISE_BOOK("storage_exercise_book"),
    SELECTED_SESSION_ID("storage_selected_session_id");
}