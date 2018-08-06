package cenergy.central.com.pwb_store.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.viewholder.NewCategoryViewHolder
import java.util.zip.Inflater

class NewCategoryAdapter : RecyclerView.Adapter<NewCategoryViewHolder>(){

    var arrayList = arrayListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewCategoryViewHolder {
        val rootView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_sub_header_product,parent,false)
        return NewCategoryViewHolder(rootView)
    }

    override fun getItemCount(): Int {
        return 6
    }

    override fun onBindViewHolder(holder: NewCategoryViewHolder, position: Int) {
        holder.itemText.text = "test"
    }

}