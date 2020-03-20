package com.ishanknijhawan.notefy.Repository

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import com.ishanknijhawan.notefy.Dao.NoteDao
import com.ishanknijhawan.notefy.Db.NoteDatabase
import com.ishanknijhawan.notefy.Entity.Note

class Repository(app: Application) {
    var noteDao: NoteDao? = NoteDatabase.getDatabase(app)?.noteDao()

    //function to insert note in database
    fun insert(note: Note): Long? {
        return insertAsync(
            noteDao
        ).execute(note).get()
    }

    //function to delete note in database
    fun delete(note: Note){
        deleteAsync(noteDao).execute(note)
    }

    //function to update note in database
    fun update(note: Note){
        updateAsync(noteDao).execute(note)
    }

    fun deleteAllNotes(){
        deleteAllAsync(noteDao).execute()
    }

    //function to get all notes in database
    fun getAllNotes(): LiveData<List<Note>> {
        return getAllNotesAsync(
            noteDao
        ).execute().get()
    }

    fun getPinnedNotes(): LiveData<List<Note>> {
        return getPinnedNotesAsync(
            noteDao
        ).execute().get()
    }

    fun getArchivedNotes(): LiveData<List<Note>> {
        return getArchivedNotesAsync(
            noteDao
        ).execute().get()
    }

    fun getDeletedNotes(): LiveData<List<Note>> {
        return getDeletedNotesAsync(
            noteDao
        ).execute().get()
    }

    class insertAsync(noteDao: NoteDao?): AsyncTask<Note, Void, Long?>(){
        var noteDao = noteDao
        override fun doInBackground(vararg params: Note): Long? {
            return noteDao?.insert(params[0])
        }
    }

    //background operation to delete note
    class deleteAsync(noteDao: NoteDao?):AsyncTask<Note,Void,Unit>(){
        var noteDao = noteDao
        override fun doInBackground(vararg params: Note){
            noteDao?.delete(params[0])
        }
    }

    //background operation to  note
    class updateAsync(noteDao: NoteDao?):AsyncTask<Note,Void,Unit>(){
        var noteDao = noteDao
        override fun doInBackground(vararg params: Note){
            noteDao?.update(params[0])
        }
    }

    //background operation to  note
    class deleteAllAsync(noteDao: NoteDao?):AsyncTask<Unit,Unit,Unit>(){
        var noteDao = noteDao
        override fun doInBackground(vararg params:Unit){
            noteDao?.deleteAll()
        }
    }

    //background operation to get all nots
    class getAllNotesAsync(noteDao: NoteDao?):AsyncTask<Unit,Void,LiveData<List<Note>>>(){

        var noteDao = noteDao
        override fun doInBackground(vararg params: Unit?): LiveData<List<Note>>? {
            return noteDao?.getAllNotes()
        }
    }

    class getPinnedNotesAsync(noteDao: NoteDao?):AsyncTask<Unit,Void,LiveData<List<Note>>>(){

        var noteDao = noteDao
        override fun doInBackground(vararg params: Unit?): LiveData<List<Note>>? {
            return noteDao?.getPinnedNotes()
        }
    }

    //background operation to get all nots
    class getArchivedNotesAsync(noteDao: NoteDao?):AsyncTask<Unit,Void,LiveData<List<Note>>>(){

        var noteDao = noteDao
        override fun doInBackground(vararg params: Unit?): LiveData<List<Note>>? {
            return noteDao?.getArchivedNotes()
        }
    }

    class getDeletedNotesAsync(noteDao: NoteDao?):AsyncTask<Unit,Void,LiveData<List<Note>>>(){

        var noteDao = noteDao
        override fun doInBackground(vararg params: Unit?): LiveData<List<Note>>? {
            return noteDao?.getDeletedNotes()
        }
    }


}