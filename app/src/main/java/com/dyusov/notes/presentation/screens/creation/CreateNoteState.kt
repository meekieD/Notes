package com.dyusov.notes.presentation.screens.creation

sealed interface CreateNoteState {

    data class Creation(
        val title: String = "",
        val content: String = "",
        val isSavedEnabled: Boolean = false
    ) : CreateNoteState

    data object Finished : CreateNoteState
}