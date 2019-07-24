package bilbao.ivanlis.daftar

import android.app.Application
import android.os.Parcelable
import androidx.lifecycle.*
import bilbao.ivanlis.daftar.constants.*
import bilbao.ivanlis.daftar.database.NotebookDb
import bilbao.ivanlis.daftar.database.NotebookRepository
import bilbao.ivanlis.daftar.database.Word
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.*
import timber.log.Timber

@Parcelize
data class WordFormInput(
    val lessonId: Long = 0L,
    val posChosen: String = "",
    val pastForm: String = "",
    val nonpastForm: String = "",
    val verbnounForm: String = "",
    val singularForm: String = "",
    val pluralForm: String = "",
    val particleForm: String = "",
    val translation: String = "",
    val wordId: Long = -1L
) : Parcelable

class NewWordViewModel(application: Application, lessonId: Long) :
    AndroidViewModel(application) {

    private val lessonId = lessonId

    var repository: NotebookRepository = NotebookRepository(NotebookDb.getInstance(application).notebookDao())

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _saveData = MutableLiveData<Boolean>()
    val saveData: LiveData<Boolean>
        get() = _saveData

    init {
        _saveData.value = false
    }

    fun onSaveClicked() {
        _saveData.value = true
    }

    fun onSaveData(userInput: WordFormInput) {

        onSaveComplete()

        uiScope.launch {

            Timber.d("onSaveData()")
            val wordId = withContext(Dispatchers.IO) {
                    repository.insertWord(Word(translation = userInput.translation, lessonId = userInput.lessonId))
            }
            Timber.d("New wordId = $wordId, pos: ${userInput.posChosen}")

            when(userInput.posChosen) {
                POS_VERB -> {
                    Timber.d("Inserting verb")
                    withContext(Dispatchers.IO) {
                        repository.registerWordRecord(wordId,
                            LANG_ARABIC,
                            FORM_PAST, userInput.pastForm)
                        repository.registerWordRecord(wordId,
                            LANG_ARABIC,
                            FORM_NONPAST, userInput.nonpastForm)
                        repository.registerWordRecord(wordId,
                            LANG_ARABIC,
                            FORM_VERBALNOUN, userInput.verbnounForm)
                    }
                }
                POS_NOUN -> {
                    Timber.d("Inserting noun")
                    withContext(Dispatchers.IO) {
                        repository.registerWordRecord(wordId,
                            LANG_ARABIC,
                            FORM_SINGULAR, userInput.singularForm)
                        repository.registerWordRecord(wordId,
                            LANG_ARABIC,
                            FORM_PLURAL, userInput.pluralForm)
                    }
                }
                POS_PARTICLE -> {
                    Timber.d("Inserting particle")
                    withContext(Dispatchers.IO) {
                        repository.registerWordRecord(wordId,
                            LANG_ARABIC,
                            FORM_PARTICLE, userInput.particleForm)
                    }
                }
                //TODO: else: exception
            }
        }
    }

    fun onSaveComplete() {
        _saveData.value = false
    }

}

class NewWordViewModelFactory(private val application: Application, private val lessonId: Long)
    :ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewWordViewModel::class.java)) {
            return NewWordViewModel(application, lessonId) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}