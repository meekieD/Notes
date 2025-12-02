package com.dyusov.notes.presentation.screens.editing

import com.dyusov.notes.domain.ContentItem
import com.dyusov.notes.domain.Note

sealed interface EditNoteState {

    data object Initial : EditNoteState

    data class Editing(val note: Note) : EditNoteState {
        val isSaveEnabled: Boolean
            get() {
                return when {
                    note.title.isBlank() -> false
                    note.content.isEmpty() -> false
                    else -> {
                        note.content.any {
                            // если только тексты и хотя бы один из них не пустой
                            it !is ContentItem.Text || it.content.isNotBlank()
                        }
                    }
                }
            }
    }

    data object Finished : EditNoteState
}