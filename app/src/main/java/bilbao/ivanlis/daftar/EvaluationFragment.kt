package bilbao.ivanlis.daftar


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders

import bilbao.ivanlis.daftar.R
import bilbao.ivanlis.daftar.databinding.FragmentEvaluationBinding

/**
 * A simple [Fragment] subclass.
 *
 */
class EvaluationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val binding: FragmentEvaluationBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_evaluation,
            container, false)

        val args = arguments

        val trueValues = requireNotNull(args?.let {
            EvaluationFragmentArgs.fromBundle(args).trueValues
        })

        val userValues = requireNotNull(args?.let {
            EvaluationFragmentArgs.fromBundle(args).userValues
        })

        val application = requireNotNull(this.activity).application
        val viewModelFactory = EvaluationViewModelFactory(application, trueValues, userValues)

        val viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(EvaluationViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this




        return binding.root
    }


}
