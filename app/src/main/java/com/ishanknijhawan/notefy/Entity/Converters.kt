package com.ishanknijhawan.notefy.Entity

import androidx.room.TypeConverter
import com.google.gson.Gson

class Converters {

    @TypeConverter
    fun listToJson(value: MutableList<Inception>?): String = Gson().toJson(value)

    @TypeConverter
    fun jsonToList(value: String) = Gson().fromJson(value, Array<Inception>::class.java).toMutableList()
}