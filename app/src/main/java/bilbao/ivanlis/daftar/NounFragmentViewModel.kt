package bilbao.ivanlis.daftar

import android.app.Application
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import bilbao.ivanlis.daftar.constants.WordScreenMode


class NounFragmentViewModel(application: Application, wordId: Long, mode: WordScreenMode = WordScreenMode.EDIT,
                            userAnswer: WordFormInput? = null):
    WordViewModel(application, wordId, mode, userAnswer) {

    val nounForms = repository.extractArabicNounForms(wordId)

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
            when (screenMode) {
                WordScreenMode.EDIT -> {
                    if (modeToContentFromDBDisplay(screenMode) && nounSingular.value == null)
                        it.let { nForms ->
                            nounSingular.value = nForms.singularForm
                        }
                }
                WordScreenMode.EVALUATE -> {
                    it.let { nForms ->
                        userAnswer?.let { userAns ->
                            nounSingular.value = composeEvaluationValue(nForms.singularForm, userAns.singularForm)
                        }
                    }
                }
                else -> { }
            }
        }
        nounPlural.addSource(nounForms) {
            when (screenMode) {
                WordScreenMode.EDIT -> {
                    if (modeToContentFromDBDisplay(screenMode) && nounPlural.value == null)
                        it.let { nForms ->
                            nounPlural.value = nForms.pluralForm
                        }
                }
                WordScreenMode.EVALUATE -> {
                    it.let { nForms ->
                        userAnswer?.let { userAns ->
                            nounPlural.value = composeEvaluationValue(nForms.pluralForm, userAns.pluralForm)
                        }
                    }
                }
                else -> { }
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

class NounFragmentViewModelFactory(private val application: Application,
                                   private val wordId: Long,
                                   private val mode: WordScreenMode = WordScreenMode.EDIT,
                                   private val userAnswer: WordFormInput? = null):
        ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NounFragmentViewModel::class.java)) {
            return NounFragmentViewModel(application, wordId, mode, userAnswer) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}