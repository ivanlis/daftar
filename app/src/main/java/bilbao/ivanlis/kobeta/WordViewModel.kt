package bilbao.ivanlis.kobeta

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
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
    val saveData: LiveData<Boolean>
        get() = _saveData

    protected val _deleteRecord = MutableLiveData<Boolean>()
    val deleteRecord: LiveData<Boolean>
        get() = _deleteRecord

    init {
        _saveData.value = false
        _deleteRecord.value = false
    }

    fun onSaveClicked() {
        _saveData.value = true
    }

    fun onDeleteRequest() {
        _deleteRecord.value = true
    }

    fun onSaveData(userInput: WordFormInput) {

        _saveData.value?.let {
            if (!it)
                return
        }

        onSaveDataComplete()

        Timber.d("onSaveData()")

        uiScope.launch {

            withContext(Dispatchers.IO) {
                repository.updateWordById(wordId, userInput.translation)
                executeSave(userInput)
            }
        }

        Toast.makeText(this.getApplication(), R.string.saved_exclamation, Toast.LENGTH_LONG).show()
    }

    private fun onSaveDataComplete() { _saveData.value = false }
    private fun onDeleteRecordComplete() { _deleteRecord.value = false }

    protected abstract suspend fun executeSave(userInput: WordFormInput)
}