package bilbao.ivanlis.daftar

import android.app.Application
import android.content.res.Resources
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.lifecycle.*
import bilbao.ivanlis.daftar.constants.POS_NOUN
import bilbao.ivanlis.daftar.constants.POS_PARTICLE
import bilbao.ivanlis.daftar.constants.POS_VERB
import bilbao.ivanlis.daftar.database.*
import kotlinx.coroutines.*
import timber.log.Timber
import java.lang.IllegalArgumentException

data class EvaluatedWordFormInput(val lessonId: Long = 0L,
                                  val posChosen: String,
                                  val pastForm: Spannable,
                                  val nonpastForm: Spannable,
                                  val verbnounForm: Spannable,
                                  val singularForm: Spannable,
                                  val pluralForm: Spannable,
                                  val particleForm: Spannable,
                                  val translation: Spannable)

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

    var nextExerciseData: WordPartOfSpeech? = null

    private val _evaluated = MutableLiveData<EvaluatedWordFormInput>()
    val evaluated: LiveData<EvaluatedWordFormInput>
        get() = _evaluated

    val evaluatedPast = Transformations.map(evaluated) {
        it?.let { evaluatedForms ->
            evaluatedForms.pastForm
        }
    }
    val evaluatedNonpast = Transformations.map(evaluated) {
        it?.let { evaluatedForms ->
            evaluatedForms.nonpastForm
        }
    }
    val evaluatedVerbnoun = Transformations.map(evaluated) {
        it?.let { evaluatedForms ->
            evaluatedForms.verbnounForm
        }
    }
    val evaluatedSingular = Transformations.map(evaluated) {
        it?.let { evaluatedForms ->
            evaluatedForms.singularForm
        }
    }
    val evaluatedPlural = Transformations.map(evaluated) {
        it?.let { evaluatedForms ->
            evaluatedForms.pluralForm
        }
    }
    val evaluatedParticle = Transformations.map(evaluated) {
        it?.let { evaluatedForms ->
            evaluatedForms.particleForm
        }
    }
    val evaluatedTranslation = Transformations.map(evaluated) {
        it?.let { evaluatedForms ->
            evaluatedForms.translation
        }
    }

    var repository: NotebookRepository = NotebookRepository(NotebookDb.getInstance(application).notebookDao())
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)


    init {
        _navigateToNextExercise.value = false
        _verbVisible.value = posToVerbVisibility(trueValues.posChosen)
        _nounVisible.value = posToNounVisibility(trueValues.posChosen)
        _particleVisible.value = posToParticleVisibility(trueValues.posChosen)
        _evaluated.value = evaluate(trueValues, userValues)
    }

    fun onNavigateToNextExercise() {
        try {
            trainingProcess.advance()
            val nextExerciseWordId = trainingProcess.getWordIdCorrespondingToExercise(
                trainingProcess.nextExerciseIndex.toInt())
            Timber.d("About to go to ex. [${trainingProcess.nextExerciseIndex}] -> $nextExerciseWordId")


            uiScope.launch {
                nextExerciseData = withContext(Dispatchers.IO) {
                    repository.extractWordPartOfSpeech(nextExerciseWordId)
                }
                _navigateToNextExercise.value = true
            }
        }
        catch (e: TrainingProcessFinishedException)
        {
            nextExerciseData = null
            _navigateToNextExercise.value = true
        }
    }

    fun onNavigateToNextExerciseComplete() {
        _navigateToNextExercise.value = false
    }

    private fun posToVerbVisibility(pos: String) =
        when(pos) {
            POS_VERB -> View.VISIBLE
            else -> View.GONE
        }
    private fun posToNounVisibility(pos: String) =
        when(pos) {
            POS_NOUN -> View.VISIBLE
            else -> View.GONE
        }
    private fun posToParticleVisibility(pos: String) =
        when(pos) {
            POS_PARTICLE -> View.VISIBLE
            else -> View.GONE
        }

    private fun evaluate(trueValues: WordFormInput, userValues: WordFormInput): EvaluatedWordFormInput {
//        return EvaluatedWordFormInput(
//            lessonId = trueValues.lessonId,
//            posChosen = trueValues.posChosen
//        )



        when(trueValues.posChosen) {
            POS_VERB -> {
                return EvaluatedWordFormInput(lessonId = trueValues.lessonId,
                    posChosen = trueValues.posChosen,
                    pastForm = evaluateString(trueValues.pastForm, userValues.pastForm),
                    nonpastForm = evaluateString(trueValues.nonpastForm, userValues.nonpastForm),
                    verbnounForm = evaluateString(trueValues.verbnounForm, userValues.verbnounForm),
                    singularForm = SpannableString("") as Spannable,
                    pluralForm = SpannableString("") as Spannable,
                    particleForm = SpannableString("") as Spannable,
                    translation = SpannableString(trueValues.translation) as Spannable)
            }
            POS_NOUN -> {
                return EvaluatedWordFormInput(lessonId = trueValues.lessonId,
                    posChosen = trueValues.posChosen,
                    pastForm = SpannableString("") as Spannable,
                    nonpastForm = SpannableString("") as Spannable,
                    verbnounForm = SpannableString("") as Spannable,
                    singularForm = evaluateString(trueValues.singularForm, userValues.singularForm),
                    pluralForm = evaluateString(trueValues.pluralForm, userValues.pluralForm),
                    particleForm = SpannableString("") as Spannable,
                    translation = SpannableString(trueValues.translation) as Spannable)
            }
            else -> {
                return EvaluatedWordFormInput(lessonId = trueValues.lessonId,
                    posChosen = trueValues.posChosen,
                    pastForm = SpannableString("") as Spannable,
                    nonpastForm = SpannableString("") as Spannable,
                    verbnounForm = SpannableString("") as Spannable,
                    singularForm = SpannableString("") as Spannable,
                    pluralForm = SpannableString("") as Spannable,
                    particleForm = evaluateString(trueValues.particleForm, userValues.particleForm),
                    translation = SpannableString(trueValues.translation) as Spannable)
            }
        }
    }

    private fun evaluateString(trueValue: String, userValue: String): Spannable {
//        val trueValueSpan = SpannableString(trueValue)
//        trueValueSpan.setSpan(ForegroundColorSpan(Color.GREEN), 0, trueValueSpan.length,
//            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//        val userValueSpan = SpannableString(userValue)
//        userValueSpan.setSpan(ForegroundColorSpan(Color.RED), 0, trueValueSpan.length,
//            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//
//        return TextUtils.concat(userValueSpan, " ", trueValueSpan) as Spannable

        //val result = SpannableString("$userValue $trueValue")

        var result = SpannableString("")

        if (trueValue.isEmpty())
            return result

        when(userValue == trueValue) {
            true -> {
                result = SpannableString(userValue)
                result.setSpan(ForegroundColorSpan(Color.GREEN),
                    0, userValue.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            false -> {
                val insteadOfUserValue = when(userValue.isEmpty()) {
                    false -> userValue
                    true -> " - "
                }
                result = SpannableString("$insteadOfUserValue $trueValue")
                result.setSpan(ForegroundColorSpan(Color.RED), 0, insteadOfUserValue.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                result.setSpan(ForegroundColorSpan(Color.GREEN), insteadOfUserValue.length + 1, result.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }

        return result
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

