package bilbao.ivanlis.kobeta.database

import androidx.annotation.WorkerThread

class NotebookRepository(private val notebookDao: NotebookDao) {

    @WorkerThread
    suspend fun insertLesson(lesson: Lesson) = notebookDao.insertLesson(lesson)
    @WorkerThread
    suspend fun deleteLesson(lesson: Lesson) { notebookDao.deleteLesson(lesson) }
    @WorkerThread
    suspend fun updateLesson(lesson: Lesson) { notebookDao.updateLesson(lesson) }

    @WorkerThread
    suspend fun insertWord(word: Word) = notebookDao.insertWord(word)
    @WorkerThread
    suspend fun deleteWord(word: Word) { notebookDao.deleteWord(word) }
    @WorkerThread
    suspend fun updateWord(word: Word) { notebookDao.updateWord(word) }

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