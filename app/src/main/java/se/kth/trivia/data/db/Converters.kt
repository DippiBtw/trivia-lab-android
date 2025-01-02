package se.kth.trivia.data.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import se.kth.trivia.data.model.TriviaQuestion

class Converters {

    private val gson = Gson()

    // Converters for List<TriviaQuestion>
    @TypeConverter
    fun fromTriviaQuestionList(value: List<TriviaQuestion>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toTriviaQuestionList(value: String): List<TriviaQuestion> {
        val listType = object : TypeToken<List<TriviaQuestion>>() {}.type
        return gson.fromJson(value, listType)
    }

    // Converters for List<String>
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType)
    }
}
