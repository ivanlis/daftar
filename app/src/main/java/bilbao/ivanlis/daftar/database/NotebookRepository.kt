package bilbao.ivanlis.daftar.database

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import timber.log.Timber

class NotebookRepository(private val notebookDao: NotebookDao) {

    @WorkerThread
    fun getLesson(lessonId: Long) = notebookDao.getLesson(lessonId)

    @WorkerThread
    fun getLessonName(lessonId: Long): LiveData<String> = notebookDao.getLessonName(lessonId)

    @WorkerThread
    fun insertLesson(lesson: Lesson): Long = notebookDao.insertLesson(lesson)
    @WorkerThread
    suspend fun deleteLesson(lesson: Lesson) { notebookDao.deleteLesson(lesson) }
    @WorkerThread
    fun deleteLessonById(lessonId: Long) { notebookDao.deleteLessonById(lessonId) }
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
    fun deleteWordById(wordId: Long) { notebookDao.deleteWordById(wordId) }
    @WorkerThread
    suspend fun updateWord(word: Word) { notebookDao.updateWord(word) }
    @WorkerThread
    fun updateWordById(wordId: Long, translation: String) { notebookDao.updateWordById(wordId, translation) }

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
    suspend fun updateWordRecordByWordAndForm(wordId: Long, formId: Long, spelling: String) {
        notebookDao.updateWordRecordByWordAndForm(wordId, formId, spelling)
    }

    @WorkerThread
    suspend fun insertScore(score: Score) = notebookDao.insertScore(score)
    @WorkerThread
    suspend fun deleteScore(score: Score) { notebookDao.deleteScore(score) }
    @WorkerThread
    suspend fun updateScore(score: Score) { notebookDao.updateScore(score) }


    @WorkerThread
    fun registerWordRecord(wordId: Long, languageName: String, formName: String, spelling: String)
            = notebookDao.registerWordRecord(wordId, languageName, formName, spelling)

}