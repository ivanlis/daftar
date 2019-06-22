package bilbao.ivanlis.kobeta

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class NounFragmentViewModel(application: Application, wordId: Long):
    WordViewModel(application, wordId) {

    val nounForms = repository.extractArabicNounForms(wordId)

    override suspend fun executeSave(userInput: WordFormInput) {
        nounForms.value?.let {nounFormsVal ->
            repository.updateWordRecordByWordAndForm(wordId, nounFormsVal.singularFormId, userInput.singularForm)
            repository.updateWordRecordByWordAndForm(wordId, nounFormsVal.pluralFormId, userInput.pluralForm)
        }
    }
}

class NounFragmentViewModelFactory(private val application: Application, private val wordId: Long):
        ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NounFragmentViewModel::class.java)) {
            return NounFragmentViewModel(application, wordId) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}