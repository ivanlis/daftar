package bilbao.ivanlis.daftar

import android.app.Application
import android.transition.Visibility
import android.view.View
import androidx.lifecycle.*
import bilbao.ivanlis.daftar.constants.EXERCISES_PER_WORD
import bilbao.ivanlis.daftar.constants.LessonDetailsMode
import bilbao.ivanlis.daftar.database.NotebookDb
import bilbao.ivanlis.daftar.database.NotebookRepository
import bilbao.ivanlis.daftar.database.TrainingProcess
import bilbao.ivanlis.daftar.dialog.DeletionDialogFragment
import kotlinx.coroutines.*
import timber.log.Timber


class LessonDetailsViewModel(application: Application, private val lessonId: Long,
                             mode: LessonDetailsMode = LessonDetailsMode.EDIT) :
    AndroidViewModel(application), DeletionDialogFragment.DeletionDialogListener {

    var repository: NotebookRepository = NotebookRepository(NotebookDb.getInstance(application).notebookDao())
    val initialForms = repository.extractInitialFormsForLesson(lessonId)

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

    private val _complainEmptyLesson = MutableLiveData<Boolean>()
    val complainEmptyLesson: LiveData<Boolean>
        get() = _complainEmptyLesson

    private val _mode = MutableLiveData<LessonDetailsMode>()
    val currentMode: LiveData<LessonDetailsMode>
        get() = _mode

    val deleteButtonVisibility: LiveData<Int> = Transformations.map(currentMode) {
        modeToDeleteButtonVisibility(it)
    }

    val addWordButtonVisibility: LiveData<Int> = Transformations.map(currentMode) {
        modeToAddWordButtonVisibility(it)
    }

    val editDescriptionButtonVisibility: LiveData<Int> = Transformations.map(currentMode) {
        modeToEditDescriptionButtonVisibility(it)
    }

    val startTrainingButtonVisibility: LiveData<Int> = Transformations.map(currentMode) {
        modeToStartTrainingButtonVisibility(it)
    }

    val trainButtonVisibility: LiveData<Int> = Transformations.map(currentMode) {
        modeToTrainButtonVisibility(it)
    }

    init {
        _navigateToLessonDescription.value = false
        _showDeletionDialog.value = false
        _executeDelete.value = false
        _complainEmptyLesson.value = false
        _mode.value = mode
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

    fun onComplainEmptyLessonComplete() {
        _complainEmptyLesson.value = false
    }

    fun modeToDeleteButtonVisibility(mode: LessonDetailsMode?) =
        when(mode) {
            LessonDetailsMode.EDIT -> View.VISIBLE
            LessonDetailsMode.TRAIN -> View.GONE
            null -> View.VISIBLE
        }

    fun modeToAddWordButtonVisibility(mode: LessonDetailsMode?) =
        when(mode) {
            LessonDetailsMode.EDIT -> View.VISIBLE
            LessonDetailsMode.TRAIN -> View.GONE
            null -> View.VISIBLE
        }

    fun modeToEditDescriptionButtonVisibility(mode: LessonDetailsMode?) =
            when(mode) {
                LessonDetailsMode.EDIT -> View.VISIBLE
                LessonDetailsMode.TRAIN -> View.GONE
                null -> View.VISIBLE
            }

    fun modeToStartTrainingButtonVisibility(mode: LessonDetailsMode?) =
            when(mode) {
                LessonDetailsMode.EDIT -> View.GONE
                LessonDetailsMode.TRAIN -> View.VISIBLE
                null -> View.GONE
            }

    fun modeToTrainButtonVisibility(mode: LessonDetailsMode?) =
            when(mode) {
                LessonDetailsMode.EDIT -> View.VISIBLE
                LessonDetailsMode.TRAIN -> View.GONE
                null -> View.GONE
            }

    fun toggleMode() {
        when(_mode.value) {
            LessonDetailsMode.EDIT -> _mode.value = LessonDetailsMode.TRAIN
            LessonDetailsMode.TRAIN -> _mode.value = LessonDetailsMode.EDIT
        }
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
                trainingProcess.initialize(wordIds, EXERCISES_PER_WORD)

                Timber.d("Passed ids: $wordIds")
                for (i in 0 until trainingProcess.numExercises)
                    Timber.d("$i -> ${trainingProcess.getWordIdCorrespondingToExercise(i.toInt())}")
            }
            else {
                _complainEmptyLesson.value = true
            }
        }

        ////TODO: temporary
        //toggleMode()
    }

    fun onTrainClicked() {
        initializeTrainingProcess()
        _mode.value = LessonDetailsMode.TRAIN
    }
}

class LessonDetailsViewModelFactory(
    private val application: Application, private val lessonId: Long,
    private val mode: LessonDetailsMode = LessonDetailsMode.EDIT
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LessonDetailsViewModel::class.java)) {
            return LessonDetailsViewModel(application, lessonId, mode) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}