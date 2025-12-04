package com.dyusov.notes.presentation.screens.creation

import android.net.Uri

sealed interface CreateNoteCommand {

    data class InputTitle(val title: String) : CreateNoteCommand

    data class InputContent(val content: String, val index: Int) : CreateNoteCommand

    data class AddImage(val uri: Uri) : CreateNoteCommand

    data class DeleteImage(val index: Int) : CreateNoteCommand

    data object Save : CreateNoteCommand

    data object Back : CreateNoteCommand
}