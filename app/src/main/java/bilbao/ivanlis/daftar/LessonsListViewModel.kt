package bilbao.ivanlis.daftar

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Environment
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import bilbao.ivanlis.daftar.database.NotebookDb
import bilbao.ivanlis.daftar.database.NotebookRepository
import bilbao.ivanlis.daftar.database.Word
import bilbao.ivanlis.daftar.database.WordInitialFormTranslation
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.File
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

    init {
        _navigateToNewLesson.value = false
        _randomLessonId.value = -1L
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

    private fun selectLessonId(idList: List<Long>) = idList[Random.nextInt(0, idList.size)]

    fun onRandomLessonNavigateComplete() {
        _randomLessonId.value = -1L
    }

    fun onExportTraining(/*wordFile: String = "words.csv", scoreFile: String = "scores.csv"*/) {

        //TODO: store
        uiScope.launch {
            withContext(Dispatchers.IO) {
                val allWords = repository.extractAllWordsInitialForms()
                Timber.d("About to store ${allWords.size} words...")
                //TODO: store allWords in words.csv
                writeWords(allWords)

                val allScores = repository.extractAllScores()
                Timber.d("About to store ${allScores.size} scores...")
                //TODO: store allScores in scores.csv
            }
        }

        Toast.makeText(getApplication(), R.string.saved_exclamation, Toast.LENGTH_LONG).show()
    }

    private fun writeWords(wordList: List<WordInitialFormTranslation>, fileName: String = "words.csv") {
        //TODO: get the Downloads directory

//        val writeExternalStoragePermission = ContextCompat.checkSelfPermission(getApplication(),
//            Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        if (writeExternalStoragePermission!= PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(getApplication(),
//                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
//                MY_PERMISSIONS_REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);
//        }


        val file = File(Environment.getExternalStorageDirectory(), fileName)

        Timber.d("The file will be $file")
//        //if (!file?.mkdirs())
//        if (!file.exists())
//            file.createNewFile()
//
//
//            //TODO: try-catch
//        file.bufferedWriter().use {
//            for (word in wordList) {
//                it.write("${word.wordId}, ${word.spelling}\n")
//            }
//        }
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