package cenergy.central.com.pwb_store.dialogs.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.dialogs.FilterView
import cenergy.central.com.pwb_store.dialogs.ProductFilterListener
import kotlinx.android.synthetic.main.item_product_filter_checkbox.view.*
import kotlinx.android.synthetic.main.item_product_filter_title.view.*
import kotlinx.android.synthetic.main.item_product_filter_topic.view.*

class ProductFilterAdapter : ListAdapter<FilterView, RecyclerView.ViewHolder>(ProductFilterViewDiffUtil()) {

    var listener: ProductFilterListener? = null

    class ProductFilterViewDiffUtil : DiffUtil.ItemCallback<FilterView>() {
        override fun areItemsTheSame(oldItem: FilterView, newItem: FilterView): Boolean {
            return oldItem.viewType == newItem.viewType
        }

        override fun areContentsTheSame(oldItem: FilterView, newItem: FilterView): Boolean {
            return if (oldItem is FilterView.FilterTileView && newItem is FilterView.FilterTileView) {
                oldItem.title == newItem.title
            } else if (oldItem is FilterView.FilterTopicView && newItem is FilterView.FilterTopicView) {
                oldItem.filterItem.value == newItem.filterItem.value
                        && oldItem.filterItem.count == newItem.filterItem.count
            } else if (oldItem is FilterView.FilterCheckBoxView && newItem is FilterView.FilterCheckBoxView) {
                oldItem.filterItem.value == newItem.filterItem.value
                        && oldItem.filterItem.count == newItem.filterItem.count
                        && oldItem.selected == newItem.selected
            } else {
                false
            }
        }
    }

    override fun getItemViewType(position: Int): Int = getItem(position).viewType

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            PRODUCT_FILTER_TITLE_VIEW -> {
                ProductFilterTitleViewHolder(inflater.inflate(
                        R.layout.item_product_filter_title, parent, false))
            }
            PRODUCT_FILTER_TOPIC_VIEW -> {
                ProductFilterTopicViewHolder(inflater.inflate(
                        R.layout.item_product_filter_topic, parent, false))
            }
            PRODUCT_FILTER_CHECKBOX_VIEW -> {
                ProductFilterCheckboxViewHolder(inflater.inflate(
                        R.layout.item_product_filter_checkbox, parent, false))
            }
            else -> {
                throw Exception("No Found ViewType $viewType")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is ProductFilterTitleViewHolder -> {
                holder.bind(item as FilterView.FilterTileView, listener)
            }
            is ProductFilterTopicViewHolder -> {
                holder.bind(item as FilterView.FilterTopicView, listener)
            }

            is ProductFilterCheckboxViewHolder -> {
                holder.bind(item as FilterView.FilterCheckBoxView, listener)
            }
        }
    }

    fun setFilterListener(listener: ProductFilterListener?) {
        this.listener = listener
    }

    companion object {
        const val PRODUCT_FILTER_TITLE_VIEW = 1
        const val PRODUCT_FILTER_TOPIC_VIEW = 2
        const val PRODUCT_FILTER_CHECKBOX_VIEW = 3

        const val FORMAT_FILTER_COUNT = "(%d)"
    }

    abstract class ProductFilterViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: T, listener: ProductFilterListener?)
    }

    inner class ProductFilterTitleViewHolder(itemView: View) :
            ProductFilterViewHolder<FilterView.FilterTileView>(itemView) {
        private val tvTitle = itemView.tvFilterTitle
        override fun bind(item: FilterView.FilterTileView, listener: ProductFilterListener?) {
            tvTitle.text = item.title
        }
    }

    inner class ProductFilterTopicViewHolder(itemView: View) :
            ProductFilterViewHolder<FilterView.FilterTopicView>(itemView) {
        private val tvFilterName = itemView.tvFilterCategory
        private val tvFilterCount = itemView.tvFilterTopicCount
        override fun bind(item: FilterView.FilterTopicView, listener: ProductFilterListener?) {
            tvFilterName.text = item.filterItem.label
            tvFilterCount.text = String.format(FORMAT_FILTER_COUNT, item.filterItem.count)
            itemView.setOnClickListener {
                listener?.onTopicClickListener(item.filterItem)
            }
        }
    }

    inner class ProductFilterCheckboxViewHolder(itemView: View) :
            ProductFilterViewHolder<FilterView.FilterCheckBoxView>(itemView) {
        private val filterCheckbox = itemView.filterCheckbox
        private val tvFilterCount = itemView.tvFilterCount
        override fun bind(item: FilterView.FilterCheckBoxView, listener: ProductFilterListener?) {
            filterCheckbox.text = item.filterItem.label
            tvFilterCount.text = String.format(FORMAT_FILTER_COUNT, item.filterItem.count)
            //in some cases, it will prevent unwanted situations
            filterCheckbox.setOnCheckedChangeListener(null)
            filterCheckbox.isChecked = item.selected
            filterCheckbox.setOnCheckedChangeListener { _, checked ->
                if (checked){
                    item.selected = true
                    listener?.onSelectFilter(item)
                } else {
                    item.selected = false
                    listener?.onUnSelectFilter(item)
                }
            }
        }
    }
}