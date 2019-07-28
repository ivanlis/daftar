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
import bilbao.ivanlis.daftar.constants.POS_NOUN
import bilbao.ivanlis.daftar.constants.POS_PARTICLE
import bilbao.ivanlis.daftar.constants.POS_VERB
import bilbao.ivanlis.daftar.databinding.FragmentNewWordBinding
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
class NewWordFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentNewWordBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_word,
            container, false)

        val args = arguments
        val lessonId = requireNotNull(when(args != null) {
            true -> NewWordFragmentArgs.fromBundle(args).lessonId
            false -> null
        })

        Timber.d("Fragment to add a word to lesson $lessonId")

        binding.pastEdit.visibility = View.GONE
        binding.textPast.visibility = View.GONE
        binding.nonpastEdit.visibility = View.GONE
        binding.textNonpast.visibility = View.GONE
        binding.verbnounEdit.visibility = View.GONE
        binding.textVerbnoun.visibility = View.GONE
        binding.singularEdit.visibility = View.GONE
        binding.textSingular.visibility = View.GONE
        binding.pluralEdit.visibility = View.GONE
        binding.textPlural.visibility = View.GONE
        binding.particleEdit.visibility = View.GONE
        binding.textParticle.visibility = View.GONE

        binding.posSelected = ""


        binding.radiogroupPos.setOnCheckedChangeListener { _, checkedId ->

            when (checkedId) {
                R.id.radiobutton_verb -> {
                    Timber.d("Verb radiobutton checked")
                    binding.pastEdit.visibility = View.VISIBLE
                    binding.textPast.visibility = View.VISIBLE
                    binding.nonpastEdit.visibility = View.VISIBLE
                    binding.textNonpast.visibility = View.VISIBLE
                    binding.verbnounEdit.visibility = View.VISIBLE
                    binding.textVerbnoun.visibility = View.VISIBLE
                    binding.singularEdit.visibility = View.GONE
                    binding.textSingular.visibility = View.GONE
                    binding.pluralEdit.visibility = View.GONE
                    binding.textPlural.visibility = View.GONE
                    binding.particleEdit.visibility = View.GONE
                    binding.textParticle.visibility = View.GONE

                    binding.posSelected = POS_VERB
                }
                R.id.radiobutton_noun -> {
                    Timber.d("Noun radiobutton checked")
                    binding.pastEdit.visibility = View.GONE
                    binding.textPast.visibility = View.GONE
                    binding.nonpastEdit.visibility = View.GONE
                    binding.textNonpast.visibility = View.GONE
                    binding.verbnounEdit.visibility = View.GONE
                    binding.textVerbnoun.visibility = View.GONE
                    binding.singularEdit.visibility = View.VISIBLE
                    binding.textSingular.visibility = View.VISIBLE
                    binding.pluralEdit.visibility = View.VISIBLE
                    binding.textPlural.visibility = View.VISIBLE
                    binding.particleEdit.visibility = View.GONE
                    binding.textParticle.visibility = View.GONE

                    binding.posSelected = POS_NOUN
                }
                R.id.radiobutton_particle -> {
                    Timber.d("Particle radiobutton checked")
                    binding.pastEdit.visibility = View.GONE
                    binding.textPast.visibility = View.GONE
                    binding.nonpastEdit.visibility = View.GONE
                    binding.textNonpast.visibility = View.GONE
                    binding.verbnounEdit.visibility = View.GONE
                    binding.textVerbnoun.visibility = View.GONE
                    binding.singularEdit.visibility = View.GONE
                    binding.textSingular.visibility = View.GONE
                    binding.pluralEdit.visibility = View.GONE
                    binding.textPlural.visibility = View.GONE
                    binding.particleEdit.visibility = View.VISIBLE
                    binding.textParticle.visibility = View.VISIBLE

                    binding.posSelected = POS_PARTICLE
                }
            }
        }


        val application = requireNotNull(this.activity).application
        val viewModelFactory = NewWordViewModelFactory(application, lessonId)

        val viewModel = ViewModelProviders
            .of(this, viewModelFactory)
            .get(NewWordViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this


        viewModel.saveData.observe(this, Observer {
            if (it) {
                val posSelected = binding.posSelected
                if (posSelected != null) {

                    if (posSelected.isEmpty()) {
                        Snackbar.make(view!!, R.string.error_you_have_to_select_pos, Snackbar.LENGTH_INDEFINITE).show()
                        return@Observer
                    }

                    if (binding.translationEdit.text.isEmpty()) {
                        Snackbar.make(view!!, R.string.error_translation_is_mandatory, Snackbar.LENGTH_INDEFINITE).show()
                        return@Observer
                    }

                    when(posSelected) {
                        POS_VERB -> {
                            if (binding.pastEdit.text.isEmpty()) {
                                Snackbar.make(view!!, R.string.error_past_form_is_mandatory, Snackbar.LENGTH_INDEFINITE)
                                    .show()
                                return@Observer
                            }
                        }
                        POS_NOUN -> {
                            if (binding.singularEdit.text.isEmpty()) {
                                Snackbar.make(view!!, R.string.error_singular_form_is_mandatory, Snackbar.LENGTH_INDEFINITE)
                                    .show()
                                return@Observer
                            }
                        }
                        POS_PARTICLE -> {
                            if (binding.particleEdit.text.isEmpty()) {
                                Snackbar.make(view!!, R.string.error_particle_is_necessary, Snackbar.LENGTH_INDEFINITE)
                                    .show()
                                return@Observer
                            }
                        }
                    }

                    val userInput = WordFormInput(
                        lessonId, posSelected,
                        binding.pastEdit.text.toString(), binding.nonpastEdit.text.toString(),
                        binding.verbnounEdit.text.toString(),
                        binding.singularEdit.text.toString(), binding.pluralEdit.text.toString(),
                        binding.particleEdit.text.toString(), binding.translationEdit.text.toString()
                    )

                    viewModel.onSaveData(userInput)

                    NavHostFragment.findNavController(this).navigate(
                        NewWordFragmentDirections.actionNewWordFragmentToLessonDetailsFragment(lessonId)
                    )

                }
            }
        })

//        binding.buttonSave.setOnClickListener {
//            viewModel.onSaveClicked()
//        }

        return binding.root

    }


}
