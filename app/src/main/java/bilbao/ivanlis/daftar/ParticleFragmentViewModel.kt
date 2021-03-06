package bilbao.ivanlis.daftar

import android.app.Application
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import bilbao.ivanlis.daftar.constants.WordScreenMode

class ParticleFragmentViewModel(application: Application, wordId: Long, mode: WordScreenMode = WordScreenMode.EDIT,
                                userAnswer: WordFormInput? = null):
        WordViewModel(application, wordId, mode, userAnswer) {

    val particleForms = repository.extractArabicParticleForms(wordId)

    val particleTranslation = MediatorLiveData<String>()
    val particleParticle = MediatorLiveData<String>()

    init {
        particleTranslation.addSource(particleForms) {
            if (particleTranslation.value == null)
                it?.let { pForms ->
                    particleTranslation.value = pForms.translation
                }
        }
        particleParticle.addSource(particleForms) {
            when (screenMode) {
                WordScreenMode.EDIT -> {
                    if (modeToContentFromDBDisplay(screenMode) && particleParticle.value == null)
                        it.let { pForms ->
                            particleParticle.value = pForms.particleForm
                        }
                }
                else -> { }
            }
        }
    }

    override suspend fun executeSave(userInput: WordFormInput) {
        particleForms.value?.let { particleFormsVal ->
            repository.updateWordRecordByWordAndForm(wordId, particleFormsVal.particleFormId, userInput.particleForm)
        }
    }
}

class ParticleFragmentViewModelFactory(private val application: Application,
                                       private val wordId: Long,
                                       private val mode: WordScreenMode = WordScreenMode.EDIT,
                                       private val userAnswer: WordFormInput? = null):
        ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(ParticleFragmentViewModel::class.java)) {
            return ParticleFragmentViewModel(application, wordId, mode, userAnswer) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
