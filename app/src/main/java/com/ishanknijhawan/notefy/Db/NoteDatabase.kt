package com.ishanknijhawan.notefy.Db

import android.content.Context
import android.os.AsyncTask
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ishanknijhawan.notefy.Dao.NoteDao
import com.ishanknijhawan.notefy.Entity.Note


@Database(entities = [Note::class],version = 2)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao() : NoteDao
}