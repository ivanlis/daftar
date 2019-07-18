package bilbao.ivanlis.daftar

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import bilbao.ivanlis.daftar.constants.WordScreenMode
import bilbao.ivanlis.daftar.database.*
import bilbao.ivanlis.daftar.dialog.DeletionDialogFragment
import kotlinx.coroutines.*
import timber.log.Timber

abstract class WordViewModel(application: Application, wordId: Long, mode: WordScreenMode = WordScreenMode.EDIT):
    AndroidViewModel(application), DeletionDialogFragment.DeletionDialogListener {

    var repository: NotebookRepository = NotebookRepository(NotebookDb.getInstance(application).notebookDao())
    protected val wordId = wordId

    protected var viewModelJob = Job()
    protected val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    protected val _saveData = MutableLiveData<Boolean>()
    val saveData: LiveData<Boolean>
        get() = _saveData

    protected val _navigateToEvaluation = MutableLiveData<Boolean>()
    val navigateToEvaluation: LiveData<Boolean>
        get() = _navigateToEvaluation

    private val _executeDelete = MutableLiveData<Boolean>()
    val executeDelete: LiveData<Boolean>
        get() = _executeDelete

    private val _deleteRequest = MutableLiveData<Boolean>()
    val deleteRequest: LiveData<Boolean>
        get() = _deleteRequest

    private val _currentMode = MutableLiveData<WordScreenMode>()
    val currentMode: LiveData<WordScreenMode>
        get() = _currentMode

    var nextExerciseData: WordPartOfSpeech? = null

    val saveButtonVisibility: LiveData<Int> = Transformations.map(currentMode) {
        modeToSaveButtonVisibility(it)
    }

    val answerButtonVisibility: LiveData<Int> = Transformations.map(currentMode) {
        modeToAnswerButtonVisibility(it)
    }

    val deleteButtonVisibility: LiveData<Int> = Transformations.map(currentMode) {
        modeToDeleteButtonVisibility(it)
    }

    val nextButtonVisibility: LiveData<Int> = Transformations.map(currentMode) {
        modeToNextButtonVisibility(it)
    }

    init {
        _saveData.value = false
        _executeDelete.value = false
        _navigateToEvaluation.value = false
        _currentMode.value = mode
    }

    val trainingProcess = TrainingProcess.getInstance(application)

    fun onSaveClicked() {
        _saveData.value = true
    }

    fun onAnswerClicked() {
        _navigateToEvaluation.value = true
    }

    fun onNextClicked() {

        try {
            trainingProcess.advance()
            val nextExerciseWordId = trainingProcess.getWordIdCorrespondingToExercise(
                trainingProcess.nextExerciseIndex.toInt())

            uiScope.launch {
                nextExerciseData = withContext(Dispatchers.IO) {
                    repository.extractWordPartOfSpeech(nextExerciseWordId)
                }
                //_navigateToEvaluation.value = true
                //TODO: event to go to the next word (ANSWER mode)
            }
        }
        catch (e: TrainingProcessFinishedException)
        {
            nextExerciseData = null
            //_navigateToEvaluation.value = true
        }
    }

    fun onNextCompleted() {
        _navigateToEvaluation.value = false
    }

    override fun onConfirmedDeleteRequest() {
        _executeDelete.value = true
    }

    fun onExecuteDeleteComplete() {
        _executeDelete.value = false
    }

    fun onDeleteRequest() {
        _deleteRequest.value = true
    }

    fun onDeleteRequestComplete() {
        _deleteRequest.value = false
    }

    fun onSaveData(userInput: WordFormInput) {

//        _saveData.value?.let {
//            if (!it)
//                return
//        }

        Timber.d("onSaveData()")

        uiScope.launch {

            withContext(Dispatchers.IO) {
                repository.updateWordById(wordId, userInput.translation)
                executeSave(userInput)
            }
        }
    }

    fun onSaveDataComplete() { _saveData.value = false }

    fun onExecuteDeleteWord() {

        onExecuteDeleteComplete()

        Timber.d("Deleting word $wordId...")
        uiScope.launch {
            withContext(Dispatchers.IO) {
                repository.deleteWordById(wordId)
            }
        }
    }

    protected abstract suspend fun executeSave(userInput: WordFormInput)

    fun modeToSaveButtonVisibility(mode: WordScreenMode?) =
            when(mode) {
                WordScreenMode.EDIT -> View.VISIBLE
                WordScreenMode.ANSWER -> View.GONE
                WordScreenMode.EVALUATE -> View.GONE
                null -> View.VISIBLE
            }

    fun modeToAnswerButtonVisibility(mode: WordScreenMode) =
            when(mode) {
                WordScreenMode.EDIT -> View.GONE
                WordScreenMode.ANSWER -> View.VISIBLE
                WordScreenMode.EVALUATE -> View.GONE
            }

    fun modeToDeleteButtonVisibility(mode: WordScreenMode) =
            when(mode) {
                WordScreenMode.EDIT -> View.VISIBLE
                WordScreenMode.ANSWER -> View.GONE
                WordScreenMode.EVALUATE -> View.GONE
            }

    fun modeToNextButtonVisibility(mode: WordScreenMode) =
            when(mode) {
                WordScreenMode.EDIT -> View.GONE
                WordScreenMode.ANSWER -> View.GONE
                WordScreenMode.EVALUATE -> View.VISIBLE
            }

    fun computeNextFragmentMode(mode: WordScreenMode) =
            when(mode) {
                WordScreenMode.EDIT -> WordScreenMode.EDIT
                WordScreenMode.ANSWER -> WordScreenMode.EVALUATE
                WordScreenMode.EVALUATE -> WordScreenMode.ANSWER
            }
}