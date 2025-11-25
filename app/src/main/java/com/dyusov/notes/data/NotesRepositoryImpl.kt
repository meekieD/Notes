package com.dyusov.notes.data

import android.content.Context
import com.dyusov.notes.domain.Note
import com.dyusov.notes.domain.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NotesRepositoryImpl private constructor(context: Context) : NotesRepository {

    private val notesDatabase = NotesDatabase.getInstance(context) // TODO: temp
    private val notesDao = notesDatabase.notesDao()

    companion object {
        private val LOCK = Any()
        private var instance: NotesRepositoryImpl? = null

        fun getInstance(context: Context): NotesRepositoryImpl {
            instance?.let { return it }

            synchronized(LOCK) {
                instance?.let { return it }

                return NotesRepositoryImpl(context).also { instance = it }
            }
        }
    }

    override suspend fun addNote(
        title: String,
        content: String,
        updatedAt: Long,
        isPinned: Boolean
    ) {
        val noteDbModel = NoteDbModel(
            id = 0,
            title = title,
            content = content,
            updatedAt = updatedAt,
            isPinned = isPinned
        ) // если id = 0, БД сама сгенерирует id
        notesDao.addNote(noteDbModel)
    }

    override suspend fun deleteNote(noteId: Int) {
        notesDao.deleteNote(noteId)
    }

    override suspend fun editNote(note: Note) {
        notesDao.addNote(note.toDbModel()) // используем ранее созданный маппер
    }

    override fun getAllNotes(): Flow<List<Note>> {
        return notesDao.getAllNotes().map { it.toEntities() } // NotesDbModel -> Note
    }

    override suspend fun getNote(noteId: Int): Note {
        return notesDao.getNote(noteId).toEntity()
    }

    override fun searchNotes(query: String): Flow<List<Note>> {
        return notesDao.searchNotes(query).map { it.toEntities() }
    }

    override suspend fun switchPinStatus(noteId: Int) {
        notesDao.switchPinnedStatus(noteId)
    }
}