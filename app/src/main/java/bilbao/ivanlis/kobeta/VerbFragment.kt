package bilbao.ivanlis.kobeta


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import bilbao.ivanlis.kobeta.databinding.FragmentVerbBinding
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

        Timber.d("Verb, wordId = $wordId")

        val application = requireNotNull(this.activity).application
        val viewModelFactory = VerbFragmentViewModelFactory(application, wordId)

        val viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(VerbFragmentViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.saveData.observe(this, Observer {
            it?.let {
                viewModel.onSaveData(WordFormInput(
                    pastForm = binding.pastEdit.text.toString(),
                    nonpastForm = binding.nonpastEdit.text.toString(),
                    verbnounForm = binding.verbnounEdit.text.toString(),
                    translation = binding.translationEdit.text.toString()))
            }
        })

        return binding.root
    }
}
