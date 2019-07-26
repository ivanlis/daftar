package bilbao.ivanlis.daftar

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.*
import bilbao.ivanlis.daftar.database.NotebookDb
import bilbao.ivanlis.daftar.database.NotebookRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import timber.log.Timber
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
        Toast.makeText(getApplication(), R.string.saved_exclamation, Toast.LENGTH_LONG).show()
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