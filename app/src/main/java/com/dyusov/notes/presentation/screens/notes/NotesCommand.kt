package com.dyusov.notes.presentation.screens.notes

import com.dyusov.notes.domain.Note

sealed interface NotesCommand {
    data class InputSearchQuery(val query: String): NotesCommand
    data class SwitchPinnedStatus(val noteId: Int): NotesCommand
}