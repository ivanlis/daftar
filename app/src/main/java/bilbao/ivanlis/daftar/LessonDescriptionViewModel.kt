package bilbao.ivanlis.daftar

import android.app.Application
import androidx.lifecycle.*
import bilbao.ivanlis.daftar.database.Lesson
import bilbao.ivanlis.daftar.database.NotebookDb
import bilbao.ivanlis.daftar.database.NotebookRepository
import kotlinx.coroutines.*
import timber.log.Timber

data class LessonDescriptionUserInput(
    val name: String,
    val description: String
)

class LessonDescriptionViewModel(application: Application, lessonId: Long = -1L, afterModification: Boolean = false):
        AndroidViewModel(application) {

    var repository: NotebookRepository = NotebookRepository(NotebookDb.getInstance(application).notebookDao())
    private val _lessonId = lessonId
    val lessonId
        get() = _lessonId
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

    private val _saveDataDone = MutableLiveData<Boolean>()
    val saveDataDone: LiveData<Boolean>
        get() = _saveDataDone

    private val _afterModification = MutableLiveData<Boolean>()
    val afterModification: LiveData<Boolean>
        get() = _afterModification

    init {
        _saveData.value = false
        _saveDataDone.value = false
        _afterModification.value = afterModification
    }

    fun onSaveClicked() {
        _saveData.value = true
    }

    private fun onSaveDataDone() { _saveDataDone.value = true }


    fun onSaveData(userInput: LessonDescriptionUserInput) {

        if (userInput.name.isEmpty()) {
            //TODO: exception!!!
            return
        }

        if (_lessonId >= 0) {
            lesson.value?.let {
                val updatedLesson = Lesson(id = it.id, name = userInput.name, creationDateTime = it.creationDateTime,
                    description = userInput.description)
                Timber.d("Updating lesson ${it.id}")
                uiScope.launch {
                    withContext(Dispatchers.IO) {
                        repository.updateLesson(updatedLesson)
                    }
                    onSaveDataDone()
                }
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

                Timber.d("Inserted lesson id = $destinationLessonId")
                // once done, navigate to the lesson's word list
                onSaveDataDone()
            }
        }

    }


    fun onSaveDataComplete() { _saveData.value = false }
    fun onSaveDataDoneComplete() { _saveDataDone.value = false }
    fun onAfterModificationComplete() { _afterModification.value = false }
}

class LessonDescriptionViewModelFactory(
    private val application: Application,
    private val lessonId: Long,
    private val afterModification: Boolean = false): ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        Timber.d("Creating view-model for ${lessonId}")

        if (modelClass.isAssignableFrom(LessonDescriptionViewModel::class.java))
            return LessonDescriptionViewModel(application, lessonId, afterModification) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
