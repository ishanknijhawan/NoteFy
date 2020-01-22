package com.ishanknijhawan.notefy.Dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ishanknijhawan.notefy.Entity.Note

@Dao
interface NoteDao {

    @Insert
    fun insert(note: Note)

    @Update
    fun update(note: Note)

    @Delete
    fun delete(note: Note)

    @Query("DELETE FROM note_table")
    fun deleteAll()

    @Query("SELECT * FROM note_table ORDER BY id DESC")
    fun getAllNotes(): List<Note>


}