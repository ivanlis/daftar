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
import androidx.recyclerview.widget.LinearLayoutManager
import bilbao.ivanlis.daftar.constants.*
import bilbao.ivanlis.daftar.databinding.FragmentLessonDetailsBinding
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
class LessonDetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentLessonDetailsBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_lesson_details, container, false)

        val args = arguments
        val lessonId = requireNotNull(when(args != null) {
            true -> LessonDetailsFragmentArgs.fromBundle(args).lessonId
            false -> null
        })
        val mode = args?.let { argsNotNull ->
            LessonDetailsFragmentArgs.fromBundle(argsNotNull).mode ?: LessonDetailsMode.EDIT
        } ?: LessonDetailsMode.EDIT

        Timber.d("lessonId = $lessonId")

        val application = requireNotNull(this.activity).application
        val viewModelFactory = LessonDetailsViewModelFactory(application, lessonId, mode)

        val viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(LessonDetailsViewModel::class.java)
        binding.lessonDetailsViewModel = viewModel

        //binding.lessonNameText.text = viewModel.lessonName
        viewModel.lessonName.observe(viewLifecycleOwner, Observer {
                binding.lessonNameText.text = it
        })

        // prepare recycler view
        binding.initialFormsList.layoutManager = LinearLayoutManager(activity)
        val adapter = InitialFormItemAdapter(InitialFormItemListener {
            Timber.d("Clicked on word: id = ${it.wordId}, part of speech name = ${it.partOfSpeechName}")

            NavHostFragment.findNavController(this).navigate(
                when(it.partOfSpeechName) {
                    POS_VERB -> LessonDetailsFragmentDirections
                        .actionLessonDetailsFragmentToVerbFragment(wordId = it.wordId, lessonId = lessonId)
                    POS_NOUN -> LessonDetailsFragmentDirections
                        .actionLessonDetailsFragmentToNounFragment(wordId = it.wordId, lessonId = lessonId)
                    //TODO: exception on unknown part of speech name
                    else -> LessonDetailsFragmentDirections
                        .actionLessonDetailsFragmentToParticleFragment(wordId = it.wordId, lessonId = lessonId)
                }
            )
        })
        binding.initialFormsList.adapter = adapter

        viewModel.initialForms.observe(viewLifecycleOwner, Observer {
            it?.let {it1 ->
                adapter.submitList(it1)
            }
        })

        viewModel.navigateToLessonDescription.observe(viewLifecycleOwner, Observer {
            it?.let {navigateFlag ->
                if (navigateFlag) {
                    Timber.d("Navigating to lesson description, id = $lessonId")

                    NavHostFragment.findNavController(this).navigate(
                        LessonDetailsFragmentDirections.actionLessonDetailsFragmentToLessonDescriptionFragment(lessonId)
                    )
                    viewModel.onNavigateToLessonDescriptionComplete()
                }
            }
        })

        viewModel.showDeletionDialog.observe(viewLifecycleOwner, Observer {
            it?.let {flagValue ->

                if (flagValue) {
                    viewModel.onShowDeletionDialogComplete()
                    showDeletionDialog(viewModel)
                }

            }
        })

        viewModel.executeDelete.observe(this, Observer {
            it?.let { flagValue ->
                if (flagValue) {
                    //Toast.makeText(this.context, "DELETE", Toast.LENGTH_SHORT).show()
                    //viewModel.onExecuteDeleteComplete()
                    viewModel.onExecuteDeleteLesson()
                    // navigate to lessons list
                    //Snackbar.make(binding.root, R.string.message_lesson_deleted, Snackbar.LENGTH_SHORT).show()
                    NavHostFragment.findNavController(this).navigate(
                        LessonDetailsFragmentDirections.actionLessonDetailsFragmentToLessonsListFragment())
                    Toast.makeText(this.context, getString(R.string.message_lesson_deleted), Toast.LENGTH_SHORT).show()
                }
            }
        })

        viewModel.complainEmptyLesson.observe(this, Observer {
            it?.let { flagValue ->
                if (flagValue) {
                    viewModel.onComplainEmptyLessonComplete()
                    Snackbar.make(view!!, getString(R.string.no_words_in_lesson_yet), Snackbar.LENGTH_LONG).show()
                }
            }
        })

        viewModel.navigateToFirstExercise.observe(this, Observer {
            it?.let { flagValue ->
                if (flagValue) {
                    viewModel.onNavigateToFirstExerciseComplete()

                    Timber.d("firstExerciseData: ${viewModel.firstExerciseData}")

                    viewModel.firstExerciseData?.let {
                        when (it.posName) {
                            POS_VERB -> NavHostFragment.findNavController(this).navigate(
                                LessonDetailsFragmentDirections.actionLessonDetailsFragmentToVerbFragment(
                                    it.wordId, lessonId, WordScreenMode.ANSWER))
                            POS_NOUN -> NavHostFragment.findNavController(this).navigate(
                                LessonDetailsFragmentDirections.actionLessonDetailsFragmentToNounFragment(
                                    it.wordId, lessonId, WordScreenMode.ANSWER))
                            POS_PARTICLE -> NavHostFragment.findNavController(this).navigate(
                                LessonDetailsFragmentDirections.actionLessonDetailsFragmentToParticleFragment(
                                    it.wordId, lessonId, WordScreenMode.ANSWER))
                        }
                    }
                }
            }
        })

        binding.lifecycleOwner = this


        binding.newWordFab.setOnClickListener {
            Timber.d("Navigating to new word, lesson id $lessonId")
            NavHostFragment.findNavController(this).navigate(
                LessonDetailsFragmentDirections.actionLessonDetailsFragmentToNewWordFragment(lessonId)
            )
        }

        return binding.root

    }

    private fun showDeletionDialog(vm: LessonDetailsViewModel) {
        val deletionDialogFragment = DeletionDialogFragment(getString(R.string.do_you_want_to_delete_this_lesson),
            getString(R.string.choice_yes), getString(R.string.choice_no), vm)

        deletionDialogFragment.show(fragmentManager!!, "lesson_deletion_dialog")
    }

    override fun onResume() {
        super.onResume()

        val mainActivity = activity as MainActivity
        mainActivity.setActionBarTitle(R.string.title_lesson_contents)
    }
}

