package cenergy.central.com.pwb_store.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.model.District
import cenergy.central.com.pwb_store.model.Postcode
import cenergy.central.com.pwb_store.model.Province
import cenergy.central.com.pwb_store.model.SubDistrict
import kotlinx.android.synthetic.main.layout_text_item.view.*

class AddressAdapter(private val mContext: Context, private var mLayoutResourceId: Int,
                     private var items: List<AddressItem>) : ArrayAdapter<AddressAdapter.AddressItem>(mContext, mLayoutResourceId, items) {
    private var listener: FilterClickListener? = null
    private var cacheItems: List<AddressItem> = arrayListOf()

    fun setItems(items: List<AddressItem>) {
        this.items = items
        this.cacheItems = items
        notifyDataSetChanged()
    }

    fun setCallback(listener: FilterClickListener) {
        this.listener = listener
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): AddressItem? {
        return items[position]
    }

    override fun getPosition(item: AddressItem?): Int {
        return items.indexOf(item)
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val rootView = LayoutInflater.from(mContext).inflate(mLayoutResourceId, parent, false)
        val item = items[position]
        val titleTextView = rootView.tvTitle
        titleTextView.text = when (item) {
            is Province -> {item.name}
            is District -> {item.name}
            is SubDistrict -> {item.name}
            is Postcode -> {item.postcode}
            else -> ""
        }
        rootView.setOnClickListener { listener?.onItemClickListener(item) }
        return rootView
    }

    interface FilterClickListener {
        fun onItemClickListener(item: AddressItem)
    }

    interface AddressItem
}

