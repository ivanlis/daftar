package bilbao.ivanlis.kobeta

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import bilbao.ivanlis.kobeta.database.NotebookDb
import bilbao.ivanlis.kobeta.database.NotebookRepository
import kotlinx.coroutines.*
import timber.log.Timber

abstract class WordViewModel(application: Application, wordId: Long):
    AndroidViewModel(application) {

    var repository: NotebookRepository = NotebookRepository(NotebookDb.getInstance(application).notebookDao())
    protected val wordId = wordId

    protected var viewModelJob = Job()
    protected val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    protected val _saveData = MutableLiveData<Boolean>()
    val saveData: MutableLiveData<Boolean>
        get() = _saveData

    init {
        _saveData.value = false
    }

    fun onSaveClicked() {
        _saveData.value = true
    }

    fun onSaveData(userInput: WordFormInput) {

        _saveData.value?.let {
            if (!it)
                return
        }

        Timber.d("onSaveData()")

        uiScope.launch {

            withContext(Dispatchers.IO) {
                repository.updateWordById(wordId, userInput.translation)

//                verbForms.value?.let {verbFormsVal ->
//                    repository.updateWordRecordByWordAndForm(wordId, verbFormsVal.pastFormId, userInput.pastForm)
//                    repository.updateWordRecordByWordAndForm(wordId, verbFormsVal.nonpastFormId, userInput.nonpastForm)
//                    repository.updateWordRecordByWordAndForm(wordId, verbFormsVal.verbalNounFormId, userInput.verbnounForm)
                executeSave(userInput)
            }
        }

        onSaveDataComplete()
    }

    protected fun onSaveDataComplete() { _saveData.value = false }
    protected abstract suspend fun executeSave(userInput: WordFormInput)
}