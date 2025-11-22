package com.dyusov.notes.presentation.screens.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.dyusov.notes.presentation.screens.creation.CreateNoteScreen
import com.dyusov.notes.presentation.screens.editing.EditNoteScreen
import com.dyusov.notes.presentation.screens.notes.NotesScreen

@Composable
fun NavGraph() {

    val screen = remember {
        mutableStateOf<Screen>(Screen.Notes)
    }

    when (val currentScreen = screen.value) {
        Screen.CreateNote -> {
            CreateNoteScreen(
                onFinished = {
                    // при завершении создания заметки - открыть главный экран
                    screen.value = Screen.Notes
                }
            )
        }

        is Screen.EditNote -> {
            EditNoteScreen(
                onFinished = {
                    // при завершении редактирования - открыть главный экран
                    screen.value = Screen.Notes
                },
                noteId = currentScreen.noteId // передаем id заметки из состояния (smart cast)
            )
        }

        Screen.Notes -> {
            NotesScreen(
                onNoteClick = { note ->
                    // при нажатии на заметку - открыть экран редактирования
                    screen.value = Screen.EditNote(note.id)
                },
                onAddNoteClick = {
                    // при нажатии на кнопку добавления заметки - открыть экран добавления
                    screen.value = Screen.CreateNote
                }
            )
        }
    }
}