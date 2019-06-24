package bilbao.ivanlis.kobeta


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
import bilbao.ivanlis.kobeta.databinding.FragmentLessonDetailsBinding
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
//        // Inflate the layout for this fragment
//        val inflatedLayout = inflater.inflate(R.layout.fragment_lesson_details, container, false)
//
//        val tempText = inflatedLayout.findViewById<TextView>(R.id.tempText)
//
//        val args = arguments
//
//        val lessonId = when(args != null) {
//            true -> LessonDetailsFragmentArgs.fromBundle(args).lessonId
//            false -> null
//        }
//
//        tempText.text = "Info on lesson ${lessonId} will be shown here."
//
//        return inflatedLayout

        val binding: FragmentLessonDetailsBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_lesson_details, container, false)

        val args = arguments
        val lessonId = requireNotNull(when(args != null) {
            true -> LessonDetailsFragmentArgs.fromBundle(args).lessonId
            false -> null
        })

        //d("LessonDetailsFragment", "lessonId = ${lessonId}")
        Timber.d("lessonId = $lessonId")

        val application = requireNotNull(this.activity).application
        val viewModelFactory = LessonDetailsViewModelFactory(application, lessonId)

        val viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(LessonDetailsViewModel::class.java)
        binding.lessonDetailsViewModel = viewModel

        //binding.lessonNameText.text = viewModel.lessonName
        viewModel.lessonName.observe(viewLifecycleOwner, Observer {
            //it?.let {
                binding.lessonNameText.text = it
            //}
        })

        //TODO: prepare recycler view
        binding.initialFormsList.layoutManager = LinearLayoutManager(activity)
        val adapter = InitialFormItemAdapter(InitialFormItemListener {
            Timber.d("Clicked on word: id = ${it.wordId}, part of speech name = ${it.partOfSpeechName}")
            // the destination depends on the part of speech
//            when(it.partOfSpeechName) {
//                "verb" -> LessonDetailsFragmentDirections.actionLessonDetailsFragmentToVerbFragment(it.wordId)
//                "noun" -> LessonDetailsFragmentDirections.actionLessonDetailsFragmentToNounFragment(it.wordId)
//                "particle" -> LessonDetailsFragmentDirections.actionLessonDetailsFragmentToParticleFragment(it.wordId)
//            }

            NavHostFragment.findNavController(this).navigate(
                when(it.partOfSpeechName) {
                    "verb" -> LessonDetailsFragmentDirections.actionLessonDetailsFragmentToVerbFragment(it.wordId)
                    "noun" -> LessonDetailsFragmentDirections.actionLessonDetailsFragmentToNounFragment(it.wordId)
                    //TODO: exception on unknown part of speech name
                    else -> LessonDetailsFragmentDirections.actionLessonDetailsFragmentToParticleFragment(it.wordId)
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
                    //TODO: show dialog
                    val dialogResult = true

                    viewModel.onNavigateToLessonDescriptionComplete()
                    viewModel.processDeletionDialogResult(dialogResult)
                    // navigate to lessons list
                    NavHostFragment.findNavController(this).navigate(
                        LessonDetailsFragmentDirections.actionLessonDetailsFragmentToLessonsListFragment())
                    Toast.makeText(this.context,"Lesson deleted", Toast.LENGTH_LONG).show()
                }
            }
        })

        binding.lifecycleOwner = this


        binding.buttonNew.setOnClickListener {
            Timber.d("Navigating to new word, lesson id $lessonId")
            NavHostFragment.findNavController(this).navigate(
                LessonDetailsFragmentDirections.actionLessonDetailsFragmentToNewWordFragment(lessonId)
            )
        }

        return binding.root

    }
}

