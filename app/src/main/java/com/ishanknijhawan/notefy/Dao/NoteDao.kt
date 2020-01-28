package com.ishanknijhawan.notefy.Dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ishanknijhawan.notefy.Entity.Note

@Dao
interface NoteDao {

    @Insert
    fun insert(note: Note): Long

    @Update
    fun update(note: Note)

    @Delete
    fun delete(note: Note)

    @Query("DELETE FROM note_table")
    fun deleteAll()

    @Query("SELECT * FROM note_table WHERE archive=0 ORDER BY id DESC")
    fun getAllNotes(): LiveData<List<Note>>


}