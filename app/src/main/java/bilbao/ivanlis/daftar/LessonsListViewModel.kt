package bilbao.ivanlis.daftar

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Environment
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import bilbao.ivanlis.daftar.database.*
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class LessonsListViewModel (application: Application):
        AndroidViewModel(application) {

    private val repository = NotebookRepository(NotebookDb.getInstance(application).notebookDao())

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _navigateToNewLesson = MutableLiveData<Boolean>()
    val navigateToNewLesson: LiveData<Boolean>
        get() = _navigateToNewLesson

    private val _randomLessonId = MutableLiveData<Long>()
    val randomLessonId: LiveData<Long>
        get() = _randomLessonId

    private val _scoreSaved = MutableLiveData<Boolean>()
    val scoreSaved: LiveData<Boolean>
        get() = _scoreSaved

    private val _errorSavingScore = MutableLiveData<Boolean>()
    val errorSavingScore: LiveData<Boolean>
        get() = _errorSavingScore

    private val _askForExternalStoragePerm = MutableLiveData<Boolean>()
    val askForExternalStoragePerm: LiveData<Boolean>
        get() = _askForExternalStoragePerm

    init {
        _navigateToNewLesson.value = false
        _randomLessonId.value = -1L
        _scoreSaved.value = false
        _errorSavingScore.value = false
        _askForExternalStoragePerm.value = false
    }

    val lessonItemsForList = repository.getLessonItemsForList()


    fun onNewLessonClicked() {
        _navigateToNewLesson.value = true
    }

    fun onNewLessonNavigateComplete() {
        _navigateToNewLesson.value = false
    }

    fun onTrainClicked() {
        lessonItemsForList.value?.let { itemList ->
            if (itemList.isNotEmpty()) {
                Timber.d("Choosing between ${itemList.size} ids...")
                val lessonIds = itemList.map { it.id }
                _randomLessonId.value = selectLessonId(lessonIds)
            }
        }
    }

    fun onScoreSavedShowComplete() {
        _scoreSaved.value = false
    }

    fun onErrorSavingScoreComplete() {
        _errorSavingScore.value = false
    }

    private fun selectLessonId(idList: List<Long>) = idList[Random.nextInt(0, idList.size)]

    fun onRandomLessonNavigateComplete() {
        _randomLessonId.value = -1L
    }

    fun onExportTraining(/*wordFile: String = "words.csv", scoreFile: String = "scores.csv"*/) {

        uiScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val allWords = repository.extractAllWordsInitialForms()
                    Timber.d("About to store ${allWords.size} words...")
                    writeWords(allWords)

                    val allScores = repository.extractAllScores()
                    Timber.d("About to store ${allScores.size} scores...")
                    writeScores(allScores)
                }
                _scoreSaved.value = true
            }
            catch(exc: Exception) {
                _errorSavingScore.value = true
            }
        }

    }

    fun onAskForExternalStoragePerm() {

        if (ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            _askForExternalStoragePerm.value = true
        }
        else {
            onExportTraining()
        }
    }

    fun onAskForExternalStoragePermComplete() {
        _askForExternalStoragePerm.value = false
    }

    private fun writeWords(wordList: List<WordInitialFormTranslation>, fileName: String = "word.csv") {

        try {

//            val file = File(getApplication<Application>().filesDir,
//                fileName)
            val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
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

    private fun writeScores(scoreList: List<Score>, fileName: String = "score.csv") {
        try {
//            val file = File(getApplication<Application>().filesDir,
//                fileName)
            val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                fileName)

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

class LessonListViewModelFactory(
    //private val dataSource: NotebookDao,
    private val application: Application): ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LessonsListViewModel::class.java)) {
            return LessonsListViewModel(application) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}