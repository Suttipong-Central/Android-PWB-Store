package cenergy.central.com.pwb_store.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.layout_text_item.view.*

class QtyAdapter(private val mContext: Context, private var mLayoutResourceId: Int,
                           private var items: ArrayList<Int>) : ArrayAdapter<Int>(mContext, mLayoutResourceId, items) {

    private var listener: QtyClickListener? = null

    fun setItems(maxQty: Int) {
        if (maxQty > 10) {
            for (i in 1..10){items.add(i)}
        } else {
            for (i in 1..maxQty){items.add(i)}
        }
        notifyDataSetChanged()
    }

    fun setCallback(listener: QtyClickListener) {
        this.listener = listener
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Int? {
        return items[position]
    }

    override fun getPosition(item: Int?): Int {
        return items.indexOf(item)
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rootView = LayoutInflater.from(mContext).inflate(mLayoutResourceId, parent, false)
        val qty = items[position]
        rootView.tvTitle.text = qty.toString()
        rootView.setOnClickListener {
            listener?.onQtyClickListener(qty)
        }
        return rootView
    }

    interface QtyClickListener {
        fun onQtyClickListener(qty: Int)
    }
}