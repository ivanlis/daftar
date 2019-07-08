package bilbao.ivanlis.daftar

import android.app.Application
import androidx.lifecycle.*
import bilbao.ivanlis.daftar.database.NotebookDb
import bilbao.ivanlis.daftar.database.NotebookRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class LessonsListViewModel (application: Application):
        AndroidViewModel(application) {

    private val repository = NotebookRepository(NotebookDb.getInstance(application).notebookDao())

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _navigateToNewLesson = MutableLiveData<Boolean>()
    val navigateToNewLesson: LiveData<Boolean>
        get() = _navigateToNewLesson

    init {
        _navigateToNewLesson.value = false
    }

    val lessonItemsForList = repository.getLessonItemsForList()


    fun onNewLessonClicked() {
        _navigateToNewLesson.value = true
    }

    fun onNewLessonNavigateComplete() {
        _navigateToNewLesson.value = false
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