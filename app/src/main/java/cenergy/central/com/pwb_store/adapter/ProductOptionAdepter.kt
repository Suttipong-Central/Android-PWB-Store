package cenergy.central.com.pwb_store.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import cenergy.central.com.pwb_store.model.ProductValue
import kotlinx.android.synthetic.main.layout_text_item.view.*

class ProductOptionAdepter(private val mContext: Context, private var mLayoutResourceId: Int) : ArrayAdapter<ProductValue>(mContext, mLayoutResourceId) {

    private var items: List<ProductValue> = arrayListOf()
    private var listener: OptionClickListener? = null

    fun setItems(items: List<ProductValue>) {
        this.items = items
        notifyDataSetChanged()
    }

    fun setCallback(listener: OptionClickListener) {
        this.listener = listener
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): ProductValue? {
        return items[position]
    }

    override fun getPosition(item: ProductValue?): Int {
        return items.indexOf(item)
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rootView = LayoutInflater.from(mContext).inflate(mLayoutResourceId, parent, false)
        val size = items[position]
        val titleTextView = rootView.tvTitle
        titleTextView.text = size.valueExtension?.label ?: ""
        titleTextView.setOnClickListener {
            listener?.onOptionClickListener(size)
        }
        return rootView
    }

    interface OptionClickListener {
        fun onOptionClickListener(size: ProductValue)
    }
}