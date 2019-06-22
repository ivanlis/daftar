package bilbao.ivanlis.kobeta


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import bilbao.ivanlis.kobeta.databinding.FragmentParticleBinding
import kotlinx.android.synthetic.main.fragment_particle.*
import timber.log.Timber


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class ParticleFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentParticleBinding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_particle, container, false)

        val args = arguments
        val wordId = requireNotNull(when(args != null) {
            true -> ParticleFragmentArgs.fromBundle(args).wordId
            false -> null
        })

        Timber.d("Particle, wordId = $wordId")

        val application = requireNotNull(this.activity).application
        val viewModelFactory = ParticleFragmentViewModelFactory(application, wordId)

        val viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(ParticleFragmentViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.saveData.observe(this, Observer {
            viewModel.onSaveData(WordFormInput(
                particleForm = particle_edit.text.toString(),
                translation = translation_edit.text.toString()
            ))
        })

        return binding.root
    }


}
