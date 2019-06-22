package bilbao.ivanlis.kobeta


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import bilbao.ivanlis.kobeta.databinding.FragmentNounBinding
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

        Timber.d("Noun, wordId = $wordId")

        val application = requireNotNull(this.activity).application
        val viewModelFactory = NounFragmentViewModelFactory(application, wordId)

        val viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(NounFragmentViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.saveData.observe(this, Observer {
            it?.let {
                viewModel.onSaveData(WordFormInput(
                    singularForm = binding.singularEdit.text.toString(),
                    pluralForm = binding.singularEdit.text.toString(),
                    translation = binding.translationEdit.text.toString()
                ))
            }
        })

        return binding.root
    }


}
