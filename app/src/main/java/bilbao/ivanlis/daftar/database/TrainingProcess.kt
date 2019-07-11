package bilbao.ivanlis.daftar.database

import android.content.Context
import java.lang.Exception

class TrainingProcess private constructor(context: Context, fileName: String) {

    companion object {
        @Volatile
        private var INSTANCE: TrainingProcess? = null

        fun getInstance(context: Context, fileName: String = "bilbao.ivanlis.daftar.training"): TrainingProcess {

            return INSTANCE ?: synchronized(this) {
                val instance = TrainingProcess(context, fileName)
                INSTANCE = instance
                instance
            }
        }
    }

    // key-value storage
    private val sharedPreferences = context.getSharedPreferences(
        fileName, Context.MODE_PRIVATE)

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }

    fun resetCount() {
        nextExercise = 0L
    }

    fun advance() {
        if (nextExercise < numExercises - 1)
            nextExercise += 1L
        else
            throw TrainingProcessFinishedException("No more exercises")
    }

    var lessonToStudy: Long
        get() = sharedPreferences.getLong("lessonToStudy", -1L)
        set(lessonId: Long) {
            sharedPreferences.edit().putLong("lessonToStudy", lessonId).apply()
        }

    var numExercises: Long
        get() = sharedPreferences.getLong("numExercises", -1L)
        set(value: Long) {
            sharedPreferences.edit().putLong("numExercises", value).apply()
        }

    var nextExercise: Long
        get() = sharedPreferences.getLong("nextExercise", -1L)
        set(value: Long) {
            sharedPreferences.edit().putLong("nextExercise", value).apply()
        }

}

open class TrainingProcessException(whatMsg: String): Exception(whatMsg)

class TrainingProcessFinishedException(whatMsg: String): TrainingProcessException(whatMsg)
//class TrainingProcessLessonNotSet(whatMsg: String): TrainingProcessException(whatMsg)