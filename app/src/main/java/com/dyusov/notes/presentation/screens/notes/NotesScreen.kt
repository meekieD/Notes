package com.dyusov.notes.presentation.screens.notes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dyusov.notes.domain.Note

@Composable
fun NotesScreen(
    modifier: Modifier = Modifier,
    viewModel: NotesViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState() // делегат
    // androidx.compose.runtime.getValue

    // Создаем ленивый список (важно не использовать vertical/horizontal scroll!)
    LazyColumn(
        modifier = Modifier
            .padding(top = 40.dp),
        // под капотом функция rememberSavable, которая сохраняет значение не только при
        // рекомпозиции @Composable функции, но и при пересоздании Activity
        // обычная функция remember {} - только при рекомпозиции
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // каждый элемент ленивого списка должен быть вызван внутри функции item
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.pinnedNotes) { pinnedNote ->
                    NotesCard(
                        note = pinnedNote
                    )
                    // последний параметр - лямбда, поэтому можем вне скобок передать действие на клик
                    {
                        viewModel.processCommand(
                            NotesCommand.SwitchPinnedStatus(pinnedNote.id)
                        )
                    }
                }
            }
        }

        // также можно использовать функцию items для передачи всей коллекции
        items(state.otherNotes) { otherNote ->
            NotesCard(
                note = otherNote,
                // можем передать любое действие на клик
                onNoteClick = {
                    viewModel.processCommand(
                        NotesCommand.SwitchPinnedStatus(otherNote.id)
                    )
                }
            )
        }
    }
}

@Composable
fun NotesCard(
    modifier: Modifier = Modifier,
    note: Note,
    onNoteClick: (Note) -> Unit // используем callback
) {
    Text(
        modifier = Modifier.clickable {
            onNoteClick(note)
        },
        text = "${note.title} - ${note.content}",
        fontSize = 24.sp
    )
}