package bilbao.ivanlis.kobeta

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.*
import bilbao.ivanlis.kobeta.database.Lesson
import bilbao.ivanlis.kobeta.database.NotebookDb
import bilbao.ivanlis.kobeta.database.NotebookRepository
import kotlinx.coroutines.*
import timber.log.Timber

data class LessonDescriptionUserInput(
    val name: String,
    val description: String
)

class LessonDescriptionViewModel(application: Application, lessonId: Long = -1L):
        AndroidViewModel(application) {

    var repository: NotebookRepository = NotebookRepository(NotebookDb.getInstance(application).notebookDao())
    private val lessonId = lessonId
    // lesson to go to after finishing
    private var _destinationLessonId = lessonId
    val destinationLessonId
        get() = _destinationLessonId

    val lesson = repository.getLesson(lessonId)

    val lessonName = MediatorLiveData<String>()
    val lessonDescription = MediatorLiveData<String>()

    init {
        lessonName.addSource(lesson) {
            if (lessonName.value == null)
                it?.let { lss ->
                    lessonName.value = lss.name
                }
        }

        lessonDescription.addSource(lesson) {
            if (lessonDescription.value == null)
                it?.let { lss ->
                    lessonDescription.value = lss.description
                }
        }
    }

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _saveData = MutableLiveData<Boolean>()
    val saveData: LiveData<Boolean>
        get() = _saveData

    private val _navigateToLessonDetails = MutableLiveData<Boolean>()
    val navigateToLessonDetails: LiveData<Boolean>
        get() = _navigateToLessonDetails



    init {
        _saveData.value = false
        _navigateToLessonDetails.value = false
    }

    fun onSaveClicked() {
        _saveData.value = true
    }

    fun onNavigateToLessonDetail() { _navigateToLessonDetails.value = true }


    fun onSaveData(userInput: LessonDescriptionUserInput) {

        onSaveDataComplete()

        if (userInput.name.isEmpty()) {
            //TODO: exception!!!
            return
        }

        if (lessonId >= 0) {
            lesson.value?.let {
                val updatedLesson = Lesson(id = it.id, name = userInput.name, creationDateTime = it.creationDateTime,
                    description = userInput.description)
                Timber.d("Updating lesson ${it.id}")
                uiScope.launch {
                    withContext(Dispatchers.IO) {
                        repository.updateLesson(updatedLesson)
                    }
                }.invokeOnCompletion {
                    onNavigateToLessonDetail()
                }
                //onSaveDataComplete()
            }
            //TODO: if null, exception
        }
        else {
            Timber.d("Inserting new lesson...")
            uiScope.launch {
                _destinationLessonId = withContext(Dispatchers.IO) {
                    repository.insertLesson(
                        Lesson(name = userInput.name, description = userInput.description))
                }

            }.invokeOnCompletion {
                Timber.d("Inserted lesson id = $destinationLessonId")
                // once done, navigate to the lesson's word list
                onNavigateToLessonDetail()
            }
            //onSaveDataComplete()
        }

    }


    private fun onSaveDataComplete() { _saveData.value = false }

    fun onNavigateToLessonDetailComplete() { _navigateToLessonDetails.value = false }
}

class LessonDescriptionViewModelFactory(
    private val application: Application,
    private val lessonId: Long): ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        Timber.d("Creating view-model for ${lessonId}")

        if (modelClass.isAssignableFrom(LessonDescriptionViewModel::class.java))
            return LessonDescriptionViewModel(application, lessonId) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
