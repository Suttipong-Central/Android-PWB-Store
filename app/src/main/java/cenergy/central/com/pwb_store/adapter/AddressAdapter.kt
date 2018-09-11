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


/**
 * Created by Anuphap Suwannamas on 7/9/2018 AD.
 * Email: Anupharpae@gmail.com
 */

class AddressAdapter(private val mContext: Context, private val mLayoutResourceId: Int,
                     private var items: List<Pair<Long, String>>) : ArrayAdapter<Pair<Long, String>>(mContext, mLayoutResourceId, items) {
    private var listener: FilterClickListener? = null

    fun setItems(items: List<Pair<Long, String>>) {
        this.items = items
        Log.d("MainActivity", "update adapter --> ${items.size}")
        notifyDataSetChanged()
    }

    fun setCallback(listener: FilterClickListener) {
        this.listener = listener
    }

//    fun setItems(items: List<Pair<Long, String>>) {
//        items.clear();
//        items.addAll(newlist)
//        this.notifyDataSetChanged()
//    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Pair<Long, String>? {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return items[position].first
    }

    override fun getPosition(item: Pair<Long, String>?): Int {
        return items.indexOf(item)
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val rootView = LayoutInflater.from(mContext).inflate(mLayoutResourceId, parent, false)
        val item = items[position]
        val titleTextView = rootView.findViewById<TextView>(R.id.tvTitle)
        titleTextView.text = item.second
        rootView.setOnClickListener { listener?.onItemClickListener(item) }
        return rootView
    }

    override fun getFilter(): Filter {
        return ListFilter()
    }


    inner class ListFilter : Filter() {
        private var filterItems: List<Pair<Long, String>>? = null

        override fun performFiltering(prefix: CharSequence?): FilterResults {
            val results = Filter.FilterResults()
            if (filterItems == null) {
                filterItems = ArrayList<Pair<Long, String>>(items)
                items = ArrayList<Pair<Long, String>>(items)
            }

            if (prefix == null || prefix.isEmpty()) {
                results.values = filterItems
                results.count = filterItems!!.size
            } else {
                val searchStrLowerCase = prefix.toString().toLowerCase()
                val matchValues = ArrayList<Pair<Long, String>>()
                for (dataItem in items) {
                    if (dataItem.second.toLowerCase().startsWith(searchStrLowerCase)) {
                        matchValues.add(Pair(first = dataItem.first, second = dataItem.second))
                    }
                }

                results.values = matchValues
                results.count = matchValues.size
            }

            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            items = if (results!!.values != null && results.count > 0) {
                results.values as ArrayList<Pair<Long, String>>
            } else {
                items
            }

            Log.d("MainActivity", "publishResults: ${results.values}")
            if (results.values != null && results.count > 0) {
                notifyDataSetChanged()
            } else {
                notifyDataSetInvalidated()
            }
        }
    }

    interface FilterClickListener {
        fun onItemClickListener(item: Pair<Long, String>)
    }
}

