package cenergy.central.com.pwb_store.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.layout_text_item.view.*

class PwbArrayAdapter(private val mContext: Context, private var mLayoutResourceId: Int,
                      private var items: List<String>) : ArrayAdapter<String>(mContext, mLayoutResourceId, items) {
    private var listener: PwbArrayAdapterListener? = null
    private var cacheItems: List<String> = arrayListOf()

    fun setItems(items: List<String>) {
        this.items = items
        this.cacheItems = items
        notifyDataSetChanged()
    }

    fun setCallback(listener: PwbArrayAdapterListener) {
        this.listener = listener
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): String? {
        return items[position]
    }

    override fun getPosition(item: String?): Int {
        return items.indexOf(item)
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rootView = LayoutInflater.from(mContext).inflate(mLayoutResourceId, parent, false)
        val item = items[position]
        val titleTextView = rootView.tvTitle
        titleTextView.text = item
        rootView.setOnClickListener { listener?.onItemClickListener(item) }
        return rootView
    }

    interface PwbArrayAdapterListener {
        fun onItemClickListener(item: String)
    }
}

