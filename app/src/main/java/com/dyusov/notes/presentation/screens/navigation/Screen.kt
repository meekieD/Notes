package com.dyusov.notes.presentation.screens.navigation

sealed interface Screen {

    data object Notes : Screen

    data object CreateNote : Screen

    data class EditNote(val noteId: Int) : Screen
}