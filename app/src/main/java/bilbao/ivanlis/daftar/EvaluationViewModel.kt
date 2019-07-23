package bilbao.ivanlis.daftar

import android.app.Application
import androidx.lifecycle.*
import bilbao.ivanlis.daftar.database.TrainingProcess
import java.lang.IllegalArgumentException

class EvaluationViewModel(application: Application, val trueValues: WordFormInput, val userValues: WordFormInput):
    AndroidViewModel(application) {

    private val trainingProcess = TrainingProcess.getInstance(application)

    private val _navigateToNextExercise = MutableLiveData<Boolean>()
    val navigateToNextExercise: LiveData<Boolean>
        get() = _navigateToNextExercise

    init {
        _navigateToNextExercise.value = false
    }

    fun onNavigateToNextExercise() {
        _navigateToNextExercise.value = true
    }

    fun onNavigateToNextExerciseComplete() {
        _navigateToNextExercise.value = false
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

