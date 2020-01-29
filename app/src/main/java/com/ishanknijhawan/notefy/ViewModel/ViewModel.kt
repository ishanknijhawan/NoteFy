package com.ishanknijhawan.notefy.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.ishanknijhawan.notefy.Entity.Note
import com.ishanknijhawan.notefy.Repository.Repository

class ViewModel(app: Application): AndroidViewModel(app) {
    val repository: Repository = Repository(app)

    fun insert(note: Note):Long?{
        return repository.insert(note)
    }

    fun delete(note: Note){
        repository.delete(note)
    }

    fun update(note: Note){
        repository.update(note)
    }

    fun deleteAllNotes(){
        repository.deleteAllNotes()
    }

    fun getAllNotes(): LiveData<List<Note>> {
        return repository.getAllNotes()
    }

    fun getArchivedNotes(): LiveData<List<Note>> {
        return repository.getArchivedNotes()
    }

    fun getDeletedNotes(): LiveData<List<Note>> {
        return repository.getDeletedNotes()
    }
}