package bilbao.ivanlis.kobeta

import android.app.Application
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class NounFragmentViewModel(application: Application, wordId: Long):
    WordViewModel(application, wordId) {

    private val nounForms = repository.extractArabicNounForms(wordId)

    val nounTranslation = MediatorLiveData<String>()
    val nounSingular = MediatorLiveData<String>()
    val nounPlural = MediatorLiveData<String>()

    init {
        nounTranslation.addSource(nounForms) {
            if (nounTranslation.value == null)
                it?.let { nForms ->
                    nounTranslation.value = nForms.translation
                }
        }
        nounSingular.addSource(nounForms) {
            if (nounSingular.value == null)
                it.let { nForms ->
                    nounSingular.value = nForms.singularForm
                }
        }
        nounPlural.addSource(nounForms) {
            if (nounPlural.value == null)
                it.let { nForms ->
                    nounPlural.value = nForms.pluralForm
                }
        }
    }

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