package bilbao.ivanlis.daftar

import android.app.Application
import androidx.lifecycle.*
import bilbao.ivanlis.daftar.database.NotebookDb
import bilbao.ivanlis.daftar.database.NotebookRepository
import bilbao.ivanlis.daftar.database.TrainingProcess
import bilbao.ivanlis.daftar.dialog.DeletionDialogFragment
import kotlinx.coroutines.*
import timber.log.Timber


class LessonDetailsViewModel(application: Application, private val lessonId: Long) :
    AndroidViewModel(application), DeletionDialogFragment.DeletionDialogListener {

    var repository: NotebookRepository = NotebookRepository(NotebookDb.getInstance(application).notebookDao())
    val initialForms = repository.extractInitialFormsForLesson(lessonId)

    //val thisLessonWordIds = repository.extractWordIdsForLesson(lessonId)

//            LiveData<List<Long>> = Transformations.map(initialForms) {
//        it.map { element ->
//            element.wordId
//        }
//    }

    val lessonName = repository.getLessonName(lessonId)

    private val _navigateToLessonDescription = MutableLiveData<Boolean>()
    val navigateToLessonDescription: LiveData<Boolean>
        get() = _navigateToLessonDescription

    private val _showDeletionDialog = MutableLiveData<Boolean>()
    val showDeletionDialog: LiveData<Boolean>
        get() = _showDeletionDialog

    private val _executeDelete = MutableLiveData<Boolean>()
    val executeDelete: LiveData<Boolean>
        get() = _executeDelete

    init {
        _navigateToLessonDescription.value = false
        _showDeletionDialog.value = false
        _executeDelete.value = false
    }

    val trainingProcess = TrainingProcess.getInstance(application)

    fun onNavigateToLessonDescription() {
        _navigateToLessonDescription.value = true
    }

    fun onNavigateToLessonDescriptionComplete() {
        _navigateToLessonDescription.value = false
    }

    fun onDeleteRequest() {
        _showDeletionDialog.value = true
    }

    fun onShowDeletionDialogComplete() {
        _showDeletionDialog.value = false
    }

    override fun onConfirmedDeleteRequest() {
        _executeDelete.value = true
    }

    fun onExecuteDeleteComplete() {
        _executeDelete.value = false
    }


    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    fun onExecuteDeleteLesson() {

        onExecuteDeleteComplete()

        Timber.d("Deleting lesson $lessonId...")
        uiScope.launch {
            withContext(Dispatchers.IO) {
                repository.deleteLessonById(lessonId)
            }
        }
    }

    fun initializeTrainingProcess() {

        uiScope.launch {
            val wordIds = withContext(Dispatchers.IO) {
                repository.extractWordIdsForLesson(lessonId)
            }

            if (wordIds.isNotEmpty()) {
                trainingProcess.initialize(wordIds, 15)

                Timber.d("Passed ids: $wordIds")
                for (i in 0 until 15)
                    Timber.d("$i -> ${trainingProcess.getWordIdCorrespondingToExercise(i)}")
            }
        }

    }
}

class LessonDetailsViewModelFactory(
    private val application: Application, private val lessonId: Long
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LessonDetailsViewModel::class.java)) {
            return LessonDetailsViewModel(application, lessonId) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}