package bilbao.ivanlis.kobeta

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import bilbao.ivanlis.kobeta.database.NotebookDb
import bilbao.ivanlis.kobeta.database.NotebookRepository
import bilbao.ivanlis.kobeta.database.Word
import bilbao.ivanlis.kobeta.database.WordRecord
import kotlinx.coroutines.*
import timber.log.Timber


class VerbFragmentViewModel(application: Application, wordId: Long):
        AndroidViewModel(application) {

    var repository: NotebookRepository = NotebookRepository(NotebookDb.getInstance(application).notebookDao())
    private val wordId = wordId
    val verbForms = repository.extractArabicVerbForms(wordId)

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _saveData = MutableLiveData<Boolean>()
    val saveData: MutableLiveData<Boolean>
        get() = _saveData

    init {
        _saveData.value = false
    }

    fun onSaveClicked() {
        _saveData.value = true
    }

    fun onSaveData(userInput: WordFormInput) {

        if (verbForms.value == null)
            return

        _saveData.value?.let {
            if (!it)
                return
        }

        Timber.d("onSaveData()")

        uiScope.launch {

            withContext(Dispatchers.IO) {
                repository.updateWordById(wordId, userInput.translation)

                verbForms.value?.let {verbFormsVal ->
                    repository.updateWordRecordByWordAndForm(wordId, verbFormsVal.pastFormId, userInput.pastForm)
                    repository.updateWordRecordByWordAndForm(wordId, verbFormsVal.nonpastFormId, userInput.nonpastForm)
                    repository.updateWordRecordByWordAndForm(wordId, verbFormsVal.verbalNounFormId, userInput.verbnounForm)
                }
            }
        }
        onSaveDataComplete()
    }

    private fun onSaveDataComplete() { _saveData.value = false }
}

class VerbFragmentViewModelFactory(private val application: Application, private val wordId: Long): ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VerbFragmentViewModel::class.java)) {
            return VerbFragmentViewModel(application, wordId) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
