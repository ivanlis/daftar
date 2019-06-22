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
        WordViewModel(application, wordId) {


    val verbForms = repository.extractArabicVerbForms(wordId)

    override fun onSaveData(userInput: WordFormInput) {

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
