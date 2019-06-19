package bilbao.ivanlis.kobeta


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.databinding.DataBindingUtil
import bilbao.ivanlis.kobeta.databinding.FragmentNewWordBinding
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
                }
            }
        }


        return binding.root

    }


}
