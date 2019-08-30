package bilbao.ivanlis.daftar.database

import android.app.Application
import android.widget.Toast
import bilbao.ivanlis.daftar.R
import bilbao.ivanlis.daftar.constants.TRAINING_SCORE_FILE
import bilbao.ivanlis.daftar.constants.TRAINING_WORD_FILE
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat

class DataExporter(private val application: Application, private val repository: NotebookRepository) {

    private var exporterJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + exporterJob)

    fun exportTraining(wordFile: String = TRAINING_WORD_FILE,
                       scoreFile: String = TRAINING_SCORE_FILE) {

        uiScope.launch {
//            try {
                withContext(Dispatchers.IO) {
                    val allWords = repository.extractAllWordsInitialForms()
                    Timber.d("About to store ${allWords.size} words...")
                    writeWords(allWords, wordFile)

                    val allScores = repository.extractAllScores()
                    Timber.d("About to store ${allScores.size} scores...")
                    writeScores(allScores, scoreFile)
                }
//                Toast.makeText(application, R.string.saved_exclamation, Toast.LENGTH_LONG).show()
//            }
//            catch(exc: Exception) {
//                Toast.makeText(application, R.string.error_saving_scores, Toast.LENGTH_LONG).show()
//            }
        }
    }

    private fun writeWords(wordList: List<WordInitialFormTranslation>, fileName: String) {
        try {
            val file = File(application.filesDir,
                fileName)

            Timber.d("The file will be $file")
            Timber.d("File exists: ${file.exists()}")
            if (!file.exists())
                file.createNewFile()
            Timber.d("File exists: ${file.exists()}")


            file.bufferedWriter().use {
                for (word in wordList) {
                    it.appendln("\u200E${word.wordId},\"\u200F${word.spelling}\u200E\"")
                }
                it.flush()
            }
        }
        catch (exc: Exception) {
            Timber.e("Exception: ${exc.message}")
            throw(exc)
        }
    }

    private fun writeScores(scoreList: List<Score>, fileName: String) {
        try {
            val file = File(application.filesDir, fileName)

            Timber.d("The file will be $file")
            Timber.d("File exists: ${file.exists()}")
            if (!file.exists())
                file.createNewFile()
            Timber.d("File exists: ${file.exists()}")

            file.bufferedWriter().use {
                for (score in scoreList) {
                    val scoreDateTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ").format(score.dateTime)
                    it.appendln("${score.id},${score.wordId},${score.scoreValue},\"$scoreDateTime\"")
                }
                it.flush()
            }
        }
        catch (exc: Exception) {
            Timber.e("Exception: ${exc.message}")
            throw(exc)
        }
    }
}