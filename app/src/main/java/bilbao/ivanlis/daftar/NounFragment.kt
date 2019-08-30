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
import bilbao.ivanlis.daftar.constants.POS_PARTICLE
import bilbao.ivanlis.daftar.constants.POS_VERB
import bilbao.ivanlis.daftar.constants.WordScreenMode
import bilbao.ivanlis.daftar.databinding.FragmentNounBinding
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
class NounFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentNounBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_noun, container, false)

        val args = arguments
        val wordId = requireNotNull(when(args != null) {
            true -> NounFragmentArgs.fromBundle(args).wordId
            false -> null
        })
        val lessonId = requireNotNull(when(args != null) {
            true -> VerbFragmentArgs.fromBundle(args).lessonId
            false -> null
        })

        val mode = args?.let {
            NounFragmentArgs.fromBundle(it).mode
        } ?: WordScreenMode.EDIT

        val userAnswer = args?.let {
            NounFragmentArgs.fromBundle(it).userInput
        }

        Timber.d("Noun, wordId = $wordId")

        val application = requireNotNull(this.activity).application
        val viewModelFactory = NounFragmentViewModelFactory(application, wordId, mode, userAnswer)

        val viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(NounFragmentViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.saveData.observe(this, Observer {
            it?.let { flagValue ->

                if (!flagValue)
                    return@Observer

                viewModel.onSaveDataComplete()

                if (binding.singularEdit.text.isEmpty()) {
                    Snackbar.make(view!!, R.string.error_singular_form_is_mandatory, Snackbar.LENGTH_INDEFINITE).show()
                    return@Observer
                }
                if (binding.translationEdit.text.isEmpty()) {
                    Snackbar.make(view!!, R.string.error_translation_is_mandatory, Snackbar.LENGTH_INDEFINITE).show()
                    return@Observer
                }

                viewModel.onSaveData(WordFormInput(
                    singularForm = binding.singularEdit.text.toString(),
                    pluralForm = binding.pluralEdit.text.toString(),
                    translation = binding.translationEdit.text.toString()
                ))

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
                        NounFragmentDirections.actionNounFragmentToLessonDetailsFragment(lessonId)
                    )
                    Toast.makeText(this.context, getString(R.string.message_word_deleted), Toast.LENGTH_SHORT).show()
                }
            }
        })

        viewModel.navigateToEvaluation.observe(this, Observer {
            it?.let { flagValue ->
                if (flagValue) {
                    viewModel.onAnswerCompleted()
                    val userInput = WordFormInput(lessonId = lessonId, posChosen = POS_NOUN,
                        singularForm = binding.singularEdit.text.toString(),
                        pluralForm = binding.pluralEdit.text.toString(),
                        translation = binding.translationEdit.text.toString(),
                        wordId = wordId)

                    viewModel.nounForms.value?.let { nounForms ->

                        val trueInput = WordFormInput(
                            lessonId = lessonId, posChosen = POS_NOUN,
                            singularForm = nounForms.singularForm,
                            pluralForm = nounForms.pluralForm,
                            translation = nounForms.translation,
                            wordId = wordId)

                        NavHostFragment.findNavController(this).navigate(
                            NounFragmentDirections.actionNounFragmentToEvaluationFragment2(trueInput, userInput, lessonId)
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

    override fun onResume() {
        super.onResume()

        val mainActivity = activity as MainActivity
        mainActivity.setActionBarTitle(R.string.title_noun_forms)
    }
}
