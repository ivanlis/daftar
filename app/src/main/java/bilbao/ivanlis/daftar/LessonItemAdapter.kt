package bilbao.ivanlis.daftar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import bilbao.ivanlis.daftar.database.LessonItemForList
import bilbao.ivanlis.daftar.databinding.ListItemLessonBinding

class LessonItemAdapter(private val clickListener: LessonItemListener) :
    ListAdapter<LessonItemForList, LessonItemAdapter.ViewHolder>(LessonItemDiffCallback()) {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ListItemLessonBinding):
            RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LessonItemForList, clickListener: LessonItemListener) {
            binding.lessonItem = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {

                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemLessonBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class LessonItemDiffCallback : DiffUtil.ItemCallback<LessonItemForList>() {
    override fun areItemsTheSame(oldItem: LessonItemForList, newItem: LessonItemForList): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: LessonItemForList, newItem: LessonItemForList): Boolean {
        return oldItem == newItem
    }
}

class LessonItemListener(val clickListener: (id: Long) -> Unit) {
    fun onClick(lessonItem: LessonItemForList) = clickListener(lessonItem.id)
}
