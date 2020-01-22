package com.ishanknijhawan.notefy.Entity

import android.graphics.Color
import android.icu.text.CaseMap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note_table")
data class Note(
    @PrimaryKey(autoGenerate = true)
    var id: Long?=null,
    val title: String,
    val description: String,
    val label: String,
    val bookmark: Boolean,
    val archive: Boolean,
    var color: Int = Color.parseColor("#FFFFFF")
)