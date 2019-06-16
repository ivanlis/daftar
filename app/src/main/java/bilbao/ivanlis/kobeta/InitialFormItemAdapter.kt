package bilbao.ivanlis.kobeta

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import bilbao.ivanlis.kobeta.database.WordInitialFormTranslation
import bilbao.ivanlis.kobeta.databinding.ListItemInitialFormBinding


class InitialFormItemAdapter(private val clickListener: InitialFormItemListener):
    ListAdapter<WordInitialFormTranslation, InitialFormItemAdapter.ViewHolder>(InitialFormItemDiffCallback()) {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null)
            holder.bind(item, clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(private val binding: ListItemInitialFormBinding):
            RecyclerView.ViewHolder(binding.root) {

        fun bind(item: WordInitialFormTranslation, clickListener: InitialFormItemListener) {
            binding.wordInitialFormTranslation = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {

                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemInitialFormBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}



class InitialFormItemDiffCallback: DiffUtil.ItemCallback<WordInitialFormTranslation>() {
    override fun areItemsTheSame(oldItem: WordInitialFormTranslation, newItem: WordInitialFormTranslation): Boolean {
        return oldItem.wordId == newItem.wordId
    }

    override fun areContentsTheSame(oldItem: WordInitialFormTranslation, newItem: WordInitialFormTranslation): Boolean {
        return oldItem == newItem
    }
}


class InitialFormItemListener(val clickListener: (initialFormItem: WordInitialFormTranslation) -> Unit) {
    fun onClick(initialFormItem: WordInitialFormTranslation) = clickListener(initialFormItem)
}
