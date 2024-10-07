package com.ijonsabae.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ijonsabae.data.dao.SwingFeedbackCommentDao
import com.ijonsabae.data.dao.SwingFeedbackDao
import com.ijonsabae.domain.model.SwingFeedback
import com.ijonsabae.domain.model.SwingFeedbackComment
import com.ijonsabae.data.typeconverter.SimilarityTypeConverter
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors
import javax.inject.Inject

@Database(
    entities = [SwingFeedback::class, SwingFeedbackComment::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(SimilarityTypeConverter::class)
abstract class SwingFeedbackDatabase: RoomDatabase() {
    abstract fun swingFeedbackDao(): SwingFeedbackDao
    abstract fun swingFeedbackCommentDao(): SwingFeedbackCommentDao

    companion object {
        fun getInstance(context: Context): SwingFeedbackDatabase = Room
            .databaseBuilder(context, SwingFeedbackDatabase::class.java, "swing_feedback.db")
            .addTypeConverter(SimilarityTypeConverter())
            .addCallback(object : Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    Executors.newSingleThreadExecutor().execute {
                        runBlocking {
                            getInstance(context)
                        }
                    }
                }
            })
            .build()
    }
}