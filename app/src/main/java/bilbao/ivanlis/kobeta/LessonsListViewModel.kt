package bilbao.ivanlis.kobeta

import android.app.Application
import android.util.Log.d
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import bilbao.ivanlis.kobeta.database.Lesson
import bilbao.ivanlis.kobeta.database.NotebookDao
import bilbao.ivanlis.kobeta.database.NotebookDb
import bilbao.ivanlis.kobeta.database.NotebookRepository
import bilbao.ivanlis.kobeta.utils.formatLessons
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.sql.CommonDataSource

class LessonsListViewModel (application: Application):
        AndroidViewModel(application) {

    var repository: NotebookRepository //= NotebookRepository(NotebookDb.getInstance(application).notebookDao())

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    init {
        d("LessonsListViewModel", "Creating repository...")
        repository = NotebookRepository(NotebookDb.getInstance(application).notebookDao())
        d("LessonsListViewModel", "Repository ready.")
        uiScope.launch {
            repository.insertLesson(Lesson(name = "Really fake lesson", creationDateTime = System.currentTimeMillis()))
        }
    }



    val lessons = repository.getAllLessons()

    val lessonStrings = Transformations.map(lessons) { lessons->
        formatLessons(lessons, application.resources)
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