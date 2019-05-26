package bilbao.ivanlis.kobeta


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import bilbao.ivanlis.kobeta.databinding.FragmentLessonsListBinding
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_lessons_list.view.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class LessonsListFragment : Fragment() {

    lateinit var tempLessonDetailsBtn: View

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
            //TODO: navigate to lesson details
        })
        binding.lessonsList.adapter = adapter

        lessonsListViewModel.lessonItemsForList.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
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
