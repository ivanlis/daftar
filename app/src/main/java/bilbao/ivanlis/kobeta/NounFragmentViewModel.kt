package bilbao.ivanlis.kobeta

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import bilbao.ivanlis.kobeta.database.NotebookDb
import bilbao.ivanlis.kobeta.database.NotebookRepository

class NounFragmentViewModel(application: Application, wordId: Long):
    WordViewModel(application, wordId) {

    //private val wordId = wordId
    val nounForms = repository.extractArabicNounForms(wordId)

    override suspend fun executeSave(userInput: WordFormInput) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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