package bilbao.ivanlis.daftar

import android.app.Application
import android.view.View
import androidx.lifecycle.*
import bilbao.ivanlis.daftar.constants.POS_NOUN
import bilbao.ivanlis.daftar.constants.POS_PARTICLE
import bilbao.ivanlis.daftar.constants.POS_VERB
import bilbao.ivanlis.daftar.database.TrainingProcess
import java.lang.IllegalArgumentException

class EvaluationViewModel(application: Application, val trueValues: WordFormInput, val userValues: WordFormInput):
    AndroidViewModel(application) {

    private val trainingProcess = TrainingProcess.getInstance(application)

    private val _navigateToNextExercise = MutableLiveData<Boolean>()
    val navigateToNextExercise: LiveData<Boolean>
        get() = _navigateToNextExercise

    private val _verbVisible = MutableLiveData<Int>()
    val verbVisible: LiveData<Int>
        get() = _verbVisible

    private val _nounVisible = MutableLiveData<Int>()
    val nounVisible: LiveData<Int>
        get() = _nounVisible

    private val _particleVisible = MutableLiveData<Int>()
    val particleVisible: LiveData<Int>
        get() = _particleVisible

    init {
        _navigateToNextExercise.value = false
        _verbVisible.value = posToVerbVisibility(trueValues.posChosen)
        _nounVisible.value = posToNounVisibility(trueValues.posChosen)
        _particleVisible.value = posToParticleVisibility(trueValues.posChosen)
    }

    fun onNavigateToNextExercise() {
        _navigateToNextExercise.value = true
    }

    fun onNavigateToNextExerciseComplete() {
        _navigateToNextExercise.value = false
    }

    fun posToVerbVisibility(pos: String) =
        when(pos) {
            POS_VERB -> View.VISIBLE
            else -> View.GONE
        }
    fun posToNounVisibility(pos: String) =
        when(pos) {
            POS_NOUN -> View.VISIBLE
            else -> View.GONE
        }
    fun posToParticleVisibility(pos: String) =
        when(pos) {
            POS_PARTICLE -> View.VISIBLE
            else -> View.GONE
        }
}

class EvaluationViewModelFactory(private val application: Application,
                                 private val trueValues: WordFormInput,
                                 private val userValues: WordFormInput):
    ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(EvaluationViewModel::class.java)) {
            return EvaluationViewModel(application, trueValues, userValues) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

