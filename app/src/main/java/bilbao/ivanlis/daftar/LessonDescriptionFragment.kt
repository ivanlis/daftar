package bilbao.ivanlis.daftar


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import bilbao.ivanlis.daftar.databinding.FragmentLessonDescriptionBinding
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber


/**
 * A simple [Fragment] subclass.
 *
 */
class LessonDescriptionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val binding: FragmentLessonDescriptionBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_lesson_description, container, false)

        val args = arguments
        val lessonId = requireNotNull(when(args != null) {
            true -> LessonDescriptionFragmentArgs.fromBundle(args).lessonId
            false -> null
        })

        val afterModification = args?.let { argsNotNull ->
            LessonDescriptionFragmentArgs.fromBundle(argsNotNull).afterModification
        } ?: false

        Timber.d("lessonId = $lessonId, afterModification = $afterModification")

        val application = requireNotNull(this.activity).application
        val viewModelFactory = LessonDescriptionViewModelFactory(application, lessonId, afterModification)

        val viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(LessonDescriptionViewModel::class.java)

        binding.viewModel = viewModel

        viewModel.saveData.observe(this, Observer {
            it?.let { flagValue ->
                if (flagValue)
                {
                    viewModel.onSaveDataComplete()

                    if (binding.lessonNameEdit.text.isEmpty()) {
                        Snackbar.make(view!!,
                            R.string.error_lesson_name_is_necessary, Snackbar.LENGTH_INDEFINITE).show()
                        return@Observer
                    }

                    val userInput = LessonDescriptionUserInput(
                        name = binding.lessonNameEdit.text.toString(),
                        description = binding.descriptionEdit.text.toString()
                    )
                    viewModel.onSaveData(userInput)
                }
            }
        })

        viewModel.saveDataDone.observe(this, Observer {
            it?.let { flagValue ->
                if (flagValue) {
                    Timber.d("Navigating to lesson details for ${viewModel.destinationLessonId}")

                    viewModel.onSaveDataDoneComplete()

                    // If we were editing an existing lesson, just reload this fragment.
                    if (viewModel.lessonId >= 0) {
                        NavHostFragment.findNavController(this).navigate(
                            LessonDescriptionFragmentDirections.actionLessonDescriptionFragmentSelf(
                                viewModel.lessonId, true)
                        )
                    }
                    else { // If we were adding a new lesson, navigate to its details.
                        NavHostFragment.findNavController(this).navigate(
                            LessonDescriptionFragmentDirections.actionLessonDescriptionFragmentToLessonDetailsFragment(
                                viewModel.destinationLessonId))
                    }
                }
            }
        })

        viewModel.afterModification.observe(this, Observer {
            it?.let { flagValue ->
                if (flagValue) {
                    viewModel.onAfterModificationComplete()
                    view?.let { viewNotNull ->
                        Snackbar.make(viewNotNull, R.string.saved_exclamation, Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        })


        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        val mainActivity = activity as MainActivity
        mainActivity.setActionBarTitle(R.string.title_lesson_description)
    }
}
