package bilbao.ivanlis.kobeta.database

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class NotebookRepository(private val notebookDao: NotebookDao) {

    @WorkerThread
    fun getLessonName(lessonId: Long): LiveData<String> = notebookDao.getLessonName(lessonId)

    @WorkerThread
    suspend fun insertLesson(lesson: Lesson): Long {
        return withContext(Dispatchers.IO) {
            notebookDao.insertLesson(lesson)
        }
    }
    @WorkerThread
    suspend fun deleteLesson(lesson: Lesson) { notebookDao.deleteLesson(lesson) }
    @WorkerThread
    suspend fun updateLesson(lesson: Lesson) { notebookDao.updateLesson(lesson) }
    @WorkerThread
    fun getAllLessons(): LiveData<List<Lesson>> {
        Timber.d("Calling notebookDao.getAllLessons()...")
//        val lessons = withContext(Dispatchers.IO) {
//            notebookDao.getAllLessons()
//        }
        val lessons = notebookDao.getAllLessons()
        Timber.d("Lessons extracted: ${lessons.value?.size}")
        return lessons
    }
    @WorkerThread
    fun getLessonItemsForList(): LiveData<List<LessonItemForList>> {
        return notebookDao.getLessonItemsForList()
    }

    @WorkerThread
    suspend fun insertWord(word: Word) = notebookDao.insertWord(word)
    @WorkerThread
    suspend fun deleteWord(word: Word) { notebookDao.deleteWord(word) }
    @WorkerThread
    suspend fun updateWord(word: Word) { notebookDao.updateWord(word) }

    @WorkerThread
    fun extractInitialFormsForLesson(lessonId: Long) = notebookDao.extractInitialFormsForLesson(lessonId)
    @WorkerThread
    fun extractArabicVerbForms(wordId: Long) = notebookDao.extractArabicVerbForms(wordId)
    @WorkerThread
    fun extractArabicNounForms(wordId: Long) = notebookDao.extractArabicNounForms(wordId)
    @WorkerThread
    fun extractArabicParticleForms(wordId: Long) = notebookDao.extractArabicParticleForms(wordId)

    @WorkerThread
    suspend fun insertWordRecord(wordRecord: WordRecord) = notebookDao.insertWordRecord(wordRecord)
    @WorkerThread
    suspend fun deleteWordRecord(wordRecord: WordRecord) { notebookDao.deleteWordRecord(wordRecord) }
    @WorkerThread
    suspend fun updateWordRecord(wordRecord: WordRecord) { notebookDao.updateWordRecord(wordRecord) }

    @WorkerThread
    suspend fun insertScore(score: Score) = notebookDao.insertScore(score)
    @WorkerThread
    suspend fun deleteScore(score: Score) { notebookDao.deleteScore(score) }
    @WorkerThread
    suspend fun updateScore(score: Score) { notebookDao.updateScore(score) }

}