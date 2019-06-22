package bilbao.ivanlis.kobeta

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ParticleFragmentViewModel(application: Application, wordId: Long):
        WordViewModel(application, wordId) {

    val particleForms = repository.extractArabicParticleForms(wordId)

    override suspend fun executeSave(userInput: WordFormInput) {
        particleForms.value?.let { particleFormsVal ->
            repository.updateWordRecordByWordAndForm(wordId, particleFormsVal.particleFormId, userInput.particleForm)
        }
    }
}

class ParticleFragmentViewModelFactory(private val application: Application, private val wordId: Long):
        ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(ParticleFragmentViewModel::class.java)) {
            return ParticleFragmentViewModel(application, wordId) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
