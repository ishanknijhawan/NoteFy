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

    @Query("DELETE FROM note_table WHERE deleted=1")
    fun deleteAll()

    @Query("SELECT * FROM note_table WHERE archive=0 AND pinned=0 AND deleted=0 ORDER BY id DESC")
    fun getAllNotes(): LiveData<List<Note>>

    @Query("SELECT * FROM note_table WHERE archive=0 AND pinned=1 AND deleted=0 ORDER BY id DESC")
    fun getPinnedNotes(): LiveData<List<Note>>

    @Query("SELECT * FROM note_table WHERE archive=1 AND pinned=0 AND deleted=0 ORDER BY id DESC")
    fun getArchivedNotes(): LiveData<List<Note>>

    @Query("SELECT * FROM note_table WHERE archive=0 AND pinned=0 AND deleted=1 ORDER BY id DESC")
    fun getDeletedNotes(): LiveData<List<Note>>

}