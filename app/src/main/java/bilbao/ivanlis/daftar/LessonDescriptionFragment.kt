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


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

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
            true -> LessonDetailsFragmentArgs.fromBundle(args).lessonId
            false -> null
        })

        Timber.d("lessonId = $lessonId")

        val application = requireNotNull(this.activity).application
        val viewModelFactory = LessonDescriptionViewModelFactory(application, lessonId)

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

        viewModel.navigateToLessonDetails.observe(this, Observer {
            it?.let { flagValue ->
                if (flagValue) {
                    Timber.d("Navigating to lesson details for ${viewModel.destinationLessonId}")

                    //Toast.makeText(this.context, R.string.saved_exclamation, Toast.LENGTH_SHORT).show()
                    //Snackbar.make(binding.root, R.string.saved_exclamation, Snackbar.LENGTH_SHORT).show()

                    NavHostFragment.findNavController(this).navigate(
                        LessonDescriptionFragmentDirections.actionLessonDescriptionFragmentToLessonDetailsFragment(
                            viewModel.destinationLessonId
                        )
                    )
                    viewModel.onNavigateToLessonDetailComplete()
                }
            }
        })


        binding.lifecycleOwner = this

        return binding.root

    }


}
