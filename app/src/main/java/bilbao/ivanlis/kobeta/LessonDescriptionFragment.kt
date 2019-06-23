package bilbao.ivanlis.kobeta


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import bilbao.ivanlis.kobeta.databinding.FragmentLessonDescriptionBinding
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

        binding.lifecycleOwner = this

        return binding.root

    }


}
