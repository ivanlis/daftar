package bilbao.ivanlis.daftar


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import bilbao.ivanlis.daftar.constants.POS_NOUN
import bilbao.ivanlis.daftar.constants.POS_VERB
import bilbao.ivanlis.daftar.constants.WordScreenMode
import bilbao.ivanlis.daftar.databinding.FragmentVerbBinding
import bilbao.ivanlis.daftar.dialog.DeletionDialogFragment
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class VerbFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentVerbBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_verb, container, false)

        val args = arguments
        val wordId = requireNotNull(when(args != null) {
            true -> VerbFragmentArgs.fromBundle(args).wordId
            false -> null
        })
        val lessonId = requireNotNull(when(args != null) {
            true -> VerbFragmentArgs.fromBundle(args).lessonId
            false -> null
        })

        val mode = args?.let {
            VerbFragmentArgs.fromBundle(it).mode
        } ?: WordScreenMode.EDIT

        val userAnswer = args?.let {
            VerbFragmentArgs.fromBundle(it).userInput
        }

        Timber.d("Verb, wordId = $wordId")

        val application = requireNotNull(this.activity).application
        val viewModelFactory = VerbFragmentViewModelFactory(application, wordId, mode, userAnswer)

        val viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(VerbFragmentViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.saveData.observe(this, Observer {
            it?.let { flagValue ->

                if (!flagValue)
                    return@Observer

                viewModel.onSaveDataComplete()

                if (binding.pastEdit.text.isEmpty()) {
                    Snackbar.make(view!!, R.string.error_past_form_is_mandatory, Snackbar.LENGTH_INDEFINITE).show()
                    return@Observer
                }
                if (binding.translationEdit.text.isEmpty()) {
                    Snackbar.make(view!!, R.string.error_translation_is_mandatory, Snackbar.LENGTH_INDEFINITE).show()
                    return@Observer
                }

                viewModel.onSaveData(WordFormInput(
                    pastForm = binding.pastEdit.text.toString(),
                    nonpastForm = binding.nonpastEdit.text.toString(),
                    verbnounForm = binding.verbnounEdit.text.toString(),
                    translation = binding.translationEdit.text.toString()))

                Snackbar.make(view!!, R.string.saved_exclamation, Snackbar.LENGTH_LONG).show()
            }
        })

        viewModel.deleteRequest.observe(this, Observer {

            it?.let { flagValue ->
                if (flagValue) {
                    viewModel.onDeleteRequestComplete()
                    showDeletionDialog(viewModel)
                }
            }
        })

        viewModel.executeDelete.observe(this, Observer {

            it?.let {flagValue ->
                if (flagValue) {
                    viewModel.onExecuteDeleteWord()
                    NavHostFragment.findNavController(this).navigate(
                        VerbFragmentDirections.actionVerbFragmentToLessonDetailsFragment(lessonId)
                    )
                    Toast.makeText(this.context, getString(R.string.message_word_deleted), Toast.LENGTH_SHORT).show()
                }
            }
        })

        viewModel.navigateToNext.observe(this, Observer {

            it?.let { flagValue ->
                if (flagValue) {
                    viewModel.onNavigateToNextComplete()
                    viewModel.nextExerciseData?.let { posData ->
                        NavHostFragment.findNavController(this).navigate(

                            when(posData.posName) {
                                POS_VERB -> VerbFragmentDirections.actionVerbFragmentSelf(posData.wordId,
                                    lessonId, WordScreenMode.ANSWER)
                                POS_NOUN -> VerbFragmentDirections.actionVerbFragmentToNounFragment(posData.wordId,
                                    lessonId, WordScreenMode.ANSWER)
                                else -> VerbFragmentDirections.actionVerbFragmentToParticleFragment(posData.wordId,
                                    lessonId, WordScreenMode.ANSWER)
                            }
                        )
                    } ?: run {
                        NavHostFragment.findNavController(this).navigate(
                            VerbFragmentDirections.actionVerbFragmentToTrainingFinishedFragment())
                    }
                }
            }
        })

        viewModel.navigateToEvaluation.observe(this, Observer {

            it?.let { flagValue ->
                if (flagValue) {
                    viewModel.onAnswerCompleted()
                    val userIput = WordFormInput(lessonId = lessonId, posChosen = POS_VERB,
                        pastForm = binding.pastEdit.text.toString(),
                        nonpastForm = binding.nonpastEdit.text.toString(),
                        verbnounForm = binding.verbnounEdit.text.toString(),
                        translation = binding.translationEdit.text.toString())
                    //NavHostFragment.findNavController(this).navigate(
                    //    VerbFragmentDirections.actionVerbFragmentSelf(wordId, lessonId, WordScreenMode.EVALUATE, userIput))
                    //Toast.makeText(this.context, "Navigated to EVALUATE", Toast.LENGTH_LONG).show()

                    viewModel.verbForms.value?.let {verbForms ->

                        val trueInput = WordFormInput(
                            lessonId = lessonId, posChosen = POS_VERB,
                            pastForm = verbForms.pastForm, nonpastForm = verbForms.nonpastForm,
                            verbnounForm = verbForms.verbalNounForm, translation = verbForms.translation
                        )

                        NavHostFragment.findNavController(this).navigate(
                            VerbFragmentDirections.actionVerbFragmentToEvaluationFragment2(trueInput, userIput)
                        )
                    }
                }
            }
        })

        return binding.root
    }

    private fun showDeletionDialog(vm: DeletionDialogFragment.DeletionDialogListener) {
        val deletionDialogFragment = DeletionDialogFragment(getString(R.string.do_you_want_to_delete_this_word),
            getString(R.string.choice_yes), getString(R.string.choice_no), vm)

        deletionDialogFragment.show(fragmentManager!!, "lesson_deletion_dialog")
    }
}
