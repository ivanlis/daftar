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
import bilbao.ivanlis.daftar.databinding.FragmentLessonsListBinding
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class LessonsListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment
        //val inflatedLayout = inflater.inflate(R.layout.fragment_lessons_list, container, false)

//        tempLessonDetailsBtn = inflatedLayout.findViewById(R.id.tempLessonDetailsBtn)
//        tempLessonDetailsBtn.setOnClickListener{
//            Navigation.findNavController(it).navigate(R.id.action_lessonsListFragment_to_lessonDetailsFragment)
//        }

        //return inflatedLayout


        val binding: FragmentLessonsListBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_lessons_list, container, false)

//        binding.tempLessonDetailsBtn.setOnClickListener{
//            Navigation.findNavController(it).navigate(LessonsListFragmentDirections
//                .actionLessonsListFragmentToLessonDetailsFragment(-1))
//        }

        val application = requireNotNull(this.activity).application
        val viewModelFactory = LessonListViewModelFactory(application)


        val lessonsListViewModel = ViewModelProviders.of(
            this, viewModelFactory).get(LessonsListViewModel::class.java)

        binding.lessonsListViewModel = lessonsListViewModel


        // preparing things for the recycler view
        val manager = LinearLayoutManager(activity)
        binding.lessonsList.layoutManager = manager

        val adapter = LessonItemAdapter(LessonItemListener {
            //Navigation.findNavController(activity).navigate(R.id.action_lessonsListFragment_to_lessonDetailsFragment)
            findNavController(this).navigate(
                LessonsListFragmentDirections.actionLessonsListFragmentToLessonDetailsFragment(it)
            )
        })
        binding.lessonsList.adapter = adapter

        lessonsListViewModel.lessonItemsForList.observe(viewLifecycleOwner, Observer {
            it?.let {it1 ->
                adapter.submitList(it1)
            }
        })


//        binding.newLessonButton.setOnClickListener {
//            lessonsListViewModel.onNewLessonClicked()
//        }

        lessonsListViewModel.navigateToNewLesson.observe(this, Observer {
            it?.let { flagValue ->
                if (flagValue) {
                    NavHostFragment.findNavController(this).navigate(
                        LessonsListFragmentDirections.actionLessonsListFragmentToLessonDescriptionFragment()
                    )
                    lessonsListViewModel.onNewLessonNavigateComplete()
                }
            }
        })

        lessonsListViewModel.randomLessonId.observe(this, Observer {
            it?.let { randomLessonId ->
                if (randomLessonId >= 0) {
                    lessonsListViewModel.onRandomLessonNavigateComplete()
                    NavHostFragment.findNavController(this).navigate(
                        //TODO: pass it the TRAINING mode
                        LessonsListFragmentDirections.actionLessonsListFragmentToLessonDetailsFragment(randomLessonId)
                    )
                }
            }
        })


        binding.lifecycleOwner = this

//        //TODO: substitute by the recycler view
//        lessonsListViewModel.lessonStrings.observe(viewLifecycleOwner, Observer {
//            if (it != null)
//                binding.tempLessonList.layout.lessonItem.text = it.toString()
//        })

        return binding.root
    }

}
