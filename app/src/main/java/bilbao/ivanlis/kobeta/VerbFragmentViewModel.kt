package bilbao.ivanlis.kobeta

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.*
import timber.log.Timber


class VerbFragmentViewModel(application: Application, wordId: Long):
        WordViewModel(application, wordId) {


    val verbForms = repository.extractArabicVerbForms(wordId)

    override suspend fun executeSave(userInput: WordFormInput) {
        verbForms.value?.let { verbFormsVal ->
            repository.updateWordRecordByWordAndForm(wordId, verbFormsVal.pastFormId, userInput.pastForm)
            repository.updateWordRecordByWordAndForm(wordId, verbFormsVal.nonpastFormId, userInput.nonpastForm)
            repository.updateWordRecordByWordAndForm(wordId, verbFormsVal.verbalNounFormId, userInput.verbnounForm)
        }
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
