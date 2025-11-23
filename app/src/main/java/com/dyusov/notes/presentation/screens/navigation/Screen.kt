package com.dyusov.notes.presentation.screens.navigation

// библиотека Jetpack Compose Navigation использует строки для навигации (route - направление)
sealed class Screen(val route: String) {

    data object Notes : Screen("notes")

    data object CreateNote : Screen("create_note")

    data object EditNote : Screen("edit_note")
}