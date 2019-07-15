package bilbao.ivanlis.daftar.database

import android.content.Context
import timber.log.Timber
import java.lang.Exception
import kotlin.random.Random
import kotlin.random.nextLong

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
        nextExerciseIndex = 0L
    }

    fun advance() {
        if (nextExerciseIndex < numExercises - 1)
            nextExerciseIndex += 1L
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

    var nextExerciseIndex: Long
        get() = sharedPreferences.getLong("nextExercise", -1L)
        set(value: Long) {
            sharedPreferences.edit().putLong("nextExercise", value).apply()
        }


    private var wordIdsToTrain: MutableList<Long> = arrayListOf()

    var randomGenerator = Random(System.currentTimeMillis())

    fun initialize(wordIds: List<Long>, exercisesPerWord: Long) {

        if (exercisesPerWord <= 0)
            throw TrainingProcessException("exercisesPerWord must be positive")

        if (wordIds.isEmpty())
            throw TrainingProcessException("wordIds cannot be empty")

        clear()

        numExercises = exercisesPerWord * wordIds.size

        // generate a random sequence of size numExercises, samples from wordIds
        for (i in 0 until numExercises) {
            val valueToAdd = wordIds[randomGenerator.nextLong(0, wordIds.size.toLong()).toInt()]
            Timber.d("Putting value $valueToAdd, index $i")
            wordIdsToTrain.add(i.toInt(), valueToAdd)
            sharedPreferences.edit().putLong(i.toString(), valueToAdd).apply()
        }
    }

    fun getWordIdCorrespondingToExercise(exerciseIndex: Int): Long {
        Timber.d("Getting exerciseIndex by index $exerciseIndex...")
        val wordId = sharedPreferences.getLong(exerciseIndex.toString(), -1L)
        if (wordId < 0)
            throw TrainingProcessException("exerciseIndex out of bounds")
        Timber.d("exerciseIndex = $wordId")
        return wordId
    }
}

open class TrainingProcessException(whatMsg: String): Exception(whatMsg)

class TrainingProcessFinishedException(whatMsg: String): TrainingProcessException(whatMsg)
//class TrainingProcessLessonNotSet(whatMsg: String): TrainingProcessException(whatMsg)