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

import bilbao.ivanlis.daftar.R
import bilbao.ivanlis.daftar.constants.POS_NOUN
import bilbao.ivanlis.daftar.constants.POS_VERB
import bilbao.ivanlis.daftar.constants.WordScreenMode
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

        val lessonId = requireNotNull(args?.let {
            EvaluationFragmentArgs.fromBundle(args).lessonId
        })

        val application = requireNotNull(this.activity).application
        val viewModelFactory = EvaluationViewModelFactory(application, trueValues, userValues)

        val viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(EvaluationViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this


        viewModel.navigateToNextExercise.observe(this, Observer {
            it?.let { flagValue ->
                if (flagValue) {
                    viewModel.onNavigateToNextExerciseComplete()
                    viewModel.nextExerciseData?.let { posData ->
                        NavHostFragment.findNavController(this).navigate(
                            when (posData.posName) {
                                POS_VERB -> EvaluationFragmentDirections.actionEvaluationFragment2ToVerbFragment(
                                    posData.wordId, lessonId, WordScreenMode.ANSWER
                                )
                                POS_NOUN -> EvaluationFragmentDirections.actionEvaluationFragment2ToNounFragment(
                                    posData.wordId, lessonId, WordScreenMode.ANSWER
                                )
                                else -> EvaluationFragmentDirections.actionEvaluationFragment2ToParticleFragment(
                                    posData.wordId, lessonId, WordScreenMode.ANSWER
                                )
                            }
                        )
                    } ?: run {
                        NavHostFragment.findNavController(this).navigate(
                            EvaluationFragmentDirections.actionEvaluationFragment2ToTrainingFinishedFragment())
                    }
                }
            }
        })


        return binding.root
    }


}
