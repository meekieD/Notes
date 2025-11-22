package com.dyusov.notes.presentation.screens.editing

import com.dyusov.notes.domain.Note

sealed interface EditNoteState {

    data object Initial : EditNoteState

    data class Editing(val note: Note) : EditNoteState {
        val isSaveEnabled
            get() = note.title.isNotBlank() && note.content.isNotBlank()
    }

    data object Finished : EditNoteState
}