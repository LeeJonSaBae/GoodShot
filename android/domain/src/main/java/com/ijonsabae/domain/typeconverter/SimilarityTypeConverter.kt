package com.ijonsabae.domain.typeconverter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.ijonsabae.domain.model.Similarity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@ProvidedTypeConverter
class SimilarityTypeConverter {
    private val json = Json {ignoreUnknownKeys = true}
    @TypeConverter
    fun similarityToJson(value: Similarity): String{
        return json.encodeToString(value)
    }

    @TypeConverter
    fun jsonToSimilarity(value: String): Similarity{
        return json.decodeFromString(value)
    }
}
