package com.dyusov.notes.presentation.screens.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dyusov.notes.domain.Note
import com.dyusov.notes.presentation.ui.theme.OtherNotesColors
import com.dyusov.notes.presentation.ui.theme.PinnedNotesColors
import com.dyusov.notes.presentation.ui.theme.Yellow200

@Composable
fun NotesScreen(
    modifier: Modifier = Modifier,
    viewModel: NotesViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState() // делегат
    // androidx.compose.runtime.getValue

    // Создаем ленивый список (важно не использовать vertical/horizontal scroll!)
    LazyColumn(
        modifier = modifier.padding(top = 48.dp)
    ) {
        // каждый элемент ленивого списка должен быть вызван внутри функции item
        item {
            Title(
                modifier = Modifier.padding(horizontal = 24.dp),
                text = "All Notes"
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            SearchBar(
                modifier = Modifier.padding(horizontal = 24.dp),
                query = state.query,
            ) {
                viewModel.processCommand(NotesCommand.InputSearchQuery(it))
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Subtitle(
                modifier = Modifier.padding(horizontal = 24.dp),
                text = "Pinned"
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                // отступ пропадает при скролле элементов
                contentPadding = PaddingValues(horizontal = 24.dp)
            ) {
                state.pinnedNotes.forEachIndexed { index, pinnedNote ->
                    // передаем ключ для связи item и заметки
                    item(key = pinnedNote.id) {
                        NoteCard(
                            note = pinnedNote,
                            onNoteClick = {
                                viewModel.processCommand(
                                    NotesCommand.EditNote(pinnedNote)
                                )
                            },
                            onLongNoteClick = {
                                viewModel.processCommand(
                                    NotesCommand.SwitchPinnedStatus(pinnedNote.id)
                                )
                            },
                            onDoubleNoteClick = {
                                viewModel.processCommand(
                                    NotesCommand.DeleteNote(pinnedNote.id)
                                )
                            },
                            backgroundColor = PinnedNotesColors[index % PinnedNotesColors.size] // цвет заметки
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Subtitle(
                modifier = Modifier.padding(horizontal = 24.dp),
                text = "Others"
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        // также можно использовать функцию itemsIndexed для передачи всей коллекции + индесы
        itemsIndexed(
            items = state.otherNotes,
            /*
                функция, которая в качестве параметра принимает заметку
                и возвращает ключ в зависимости от этой заметки
            */
            key = { _, note -> note.id }
        ) { index, otherNote ->
            NoteCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                note = otherNote,
                onNoteClick = {
                    viewModel.processCommand(
                        NotesCommand.EditNote(otherNote)
                    )
                },
                onLongNoteClick = {
                    viewModel.processCommand(
                        NotesCommand.SwitchPinnedStatus(otherNote.id)
                    )
                },
                onDoubleNoteClick = {
                    viewModel.processCommand(
                        NotesCommand.DeleteNote(otherNote.id)
                    )
                },
                backgroundColor = OtherNotesColors[index % OtherNotesColors.size] // цвет заметки
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun Title(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        modifier = modifier,
        text = text,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
private fun SearchBar(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String) -> Unit
) {
    TextField(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                shape = RoundedCornerShape(10.dp)
            ),
        value = query,
        onValueChange = onQueryChange, // можно и { onQueryChange(it) }
        // placeholder: @Composable (() -> Unit)? -> подход Slot API
        placeholder = {
            Text(
                text = "Search...",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        // аналогично для иконки поиска
        leadingIcon = {
            Icon(
                tint = MaterialTheme.colorScheme.onSurface,
                imageVector = Icons.Default.Search, // иконка поиска по умолчанию
                contentDescription = "Search notes" // обязательный параметр описания элемента
            )
        },
        // радиус скругления
        shape = RoundedCornerShape(10.dp),
        // цвета
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = Color.Transparent, // убирает полоску у поиска в фокусе
            unfocusedIndicatorColor = Color.Transparent, // убирает полоску у поиска вне фокуса

        )
    )
}

@Composable
private fun Subtitle(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        modifier = modifier,
        text = text,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun NoteCard(
    modifier: Modifier = Modifier,
    note: Note,
    backgroundColor: Color,
    // используем callback
    onNoteClick: (Note) -> Unit,
    onLongNoteClick: (Note) -> Unit,
    onDoubleNoteClick: (Note) -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            // для реагирования на разные виды нажатий/кликов
            .combinedClickable(
                onClick = {
                    onNoteClick(note)
                },
                onLongClick = {
                    onLongNoteClick(note)
                },
                onDoubleClick = {
                    onDoubleNoteClick(note)
                }
            )
            .padding(16.dp),

    ) {
        // Заголовок заметки
        Text(
            text = note.title,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        // Отступ между элементами
        Spacer(modifier = Modifier.height(8.dp))

        // Время последнего изменения заметки
        Text(
            text = note.updatedAt.toString(),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        // Отступ между элементами
        Spacer(modifier = Modifier.height(24.dp))

        // Контент заметки
        Text(
            text = note.content,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}