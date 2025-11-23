package com.dyusov.notes.presentation.screens.navigation

sealed interface CustomScreen {

    data object Notes : CustomScreen

    data object CreateNote : CustomScreen

    data class EditNote(val noteId: Int) : CustomScreen
}