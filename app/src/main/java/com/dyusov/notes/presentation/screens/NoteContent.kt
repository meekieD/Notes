package com.dyusov.notes.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.dyusov.notes.domain.ContentItem

@Composable
fun NoteContent(
    modifier: Modifier = Modifier,
    content: List<ContentItem>,
    onDeleteImageClick: (Int) -> Unit,
    onTextChanged: (Int, String) -> Unit
) {
    LazyColumn(
        modifier = modifier
    ) {
        content.forEachIndexed { index, item ->
            item(key = index) {
                when (item) {
                    is ContentItem.Image -> {
                        // если предыдущий элемент = картинка, значит текущий уже отображен!
                        val isAlreadyDisplayed =
                            index > 0 && content[index - 1] is ContentItem.Image

                        content.takeIf { !isAlreadyDisplayed } // фильтруем по условию (нулабельная коллекция на выходе)
                            ?.drop(index) // удаляем все элементы до текущего
                            ?.takeWhile { it is ContentItem.Image } // берем все, пока это картинки
                            ?.map { (it as ContentItem.Image).url } // выбираем url
                            ?.let { imageUrls ->
                                ImageGroup(
                                    imageUrls = imageUrls,
                                    onDeleteClick = {
                                        onDeleteImageClick(index)
                                    }
                                )
                            }
                    }

                    is ContentItem.Text -> {
                        TextContent(
                            text = item.content,
                            onTextChanged = { content ->
                                onTextChanged(index, content)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ImageGroup(
    modifier: Modifier = Modifier,
    imageUrls: List<String>,
    onDeleteClick: (Int) -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp) // одинаковое расстояние между картинками
    ) {
        imageUrls.forEachIndexed { index, url ->
            ImageContent(
                modifier = Modifier.weight(1f),
                imageUrl = url,
                onDeleteClick = {
                    onDeleteClick(index) // передаем индекс удаляемой картинки
                }
            )
        }
    }
}

@Composable
fun ImageContent(
    modifier: Modifier = Modifier,
    imageUrl: String,
    onDeleteClick: () -> Unit
) {
    Box(
        modifier = modifier
    ) {
        // отображаемая картинка
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp)), // закругленные края
            model = imageUrl, // ссылка на картинку
            contentDescription = "Image from gallery",
            contentScale = ContentScale.FillWidth // заполнить по ширине, сохраняя соотношение сторон
        )
        // иконка "удалить"
        Icon(
            modifier = Modifier
                .align(Alignment.TopEnd) // располагаем иконку в правом верхнем углу
                .padding(8.dp)
                .size(24.dp)
                .clickable {
                    onDeleteClick()
                },
            imageVector = Icons.Default.Close,
            contentDescription = "Delete image from note",
            tint = MaterialTheme.colorScheme.onSurface // цвет иконки
        )
    }
}

@Composable
fun TextContent(
    modifier: Modifier = Modifier,
    text: String,
    onTextChanged: (String) -> Unit
) {
    TextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        value = text,
        onValueChange = onTextChanged,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        textStyle = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurface
        ),
        placeholder = {
            Text(
                text = "Note something down...",
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface
                    .copy(alpha = 0.2f), // изменяем прозрачность, ставим 20%
            )
        }
    )
}