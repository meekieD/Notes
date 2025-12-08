package com.dyusov.notes.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.dyusov.notes.domain.ContentItem
import com.dyusov.notes.domain.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {
    /* Room будет автоматически оповещать о каждом изменении в БД, если используется Flow */

    @Transaction // лучше указать, т.к. здесь запрос из двух мест (таблица notes и content)
    @Query("SELECT * FROM notes ORDER BY updatedAt DESC")
    fun getAllNotes(): Flow<List<NoteWithContentDbModel>>

    @Transaction // лучше указать, т.к. здесь запрос из двух мест (таблица notes и content)
    @Query("SELECT * FROM notes WHERE id == :noteId")
    suspend fun getNote(noteId: Int): NoteWithContentDbModel

    @Transaction // лучше указать, т.к. здесь запрос из двух мест (таблица notes и content)
    @Query(
        """
        SELECT DISTINCT notes.* 
        FROM notes JOIN content ON notes.id == content.noteId
        WHERE title LIKE '%'||:query||'%' 
        OR content LIKE '%'||:query||'%' 
        AND contentType == 'TEXT'
        ORDER BY updatedAt DESC
    """
    ) // '||' - конкатенация строк
    fun searchNotes(query: String): Flow<List<NoteWithContentDbModel>>

    @Transaction // лучше указать, т.к. здесь запрос из двух мест (таблица notes и content)
    @Query("DELETE FROM notes WHERE id == :noteId")
    suspend fun deleteNote(noteId: Int)

    @Query("UPDATE notes SET isPinned = NOT isPinned WHERE id == :noteId")
    suspend fun switchPinnedStatus(noteId: Int)

    // для вставки запрос писать не нужно, при конфликте - обновление записи
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNote(note: NoteDbModel): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNoteContent(content: List<ContentItemDbModel>)

    @Query("DELETE FROM content WHERE noteId == :noteId")
    suspend fun deleteNoteContent(noteId: Int)

    /* Транзакции */

    @Transaction
    suspend fun addNoteWithContent(
        noteDbModel: NoteDbModel,
        content: List<ContentItem>
    ) {
        val noteId = addNote(noteDbModel).toInt() // добавляем заметку и получаем id
        val contentItems = content.toContentItemDbModels(noteId)
        addNoteContent(contentItems) // добавляем контент
    }

    @Transaction
    suspend fun editNoteWithContent(
        noteDbModel: NoteDbModel,
        content: List<ContentItemDbModel>
    ) {
        addNote(noteDbModel) // используем ранее созданный маппер
        // сначала удаляем весь прошлый контент, а затем добавляем новый
        deleteNoteContent(noteDbModel.id)
        addNoteContent(content)
    }

}