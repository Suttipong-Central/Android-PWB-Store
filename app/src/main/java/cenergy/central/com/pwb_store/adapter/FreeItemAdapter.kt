package cenergy.central.com.pwb_store.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.model.CacheFreeItem
import com.bumptech.glide.Glide

class FreeItemAdapter(var context: Context): RecyclerView.Adapter<FreeItemViewHolder>(){
    var freeItems: List<CacheFreeItem> = listOf()
    set(value) {
        field = value
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FreeItemViewHolder {
        return FreeItemViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item_freebie, parent, false))
    }

    override fun getItemCount(): Int {
        return freeItems.size
    }

    override fun onBindViewHolder(holder: FreeItemViewHolder, position: Int) {
        holder.bind(context, freeItems[position])
    }
}

class FreeItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    private val freeItemImage: ImageView = itemView.findViewById(R.id.freeBieImage)
    fun bind(context: Context, freeItem: CacheFreeItem) {
        Log.d("Image", freeItem.imageUrl)
        Glide.with(context)
                .load(freeItem.imageUrl)
                .placeholder(R.drawable.ic_placeholder)
                .crossFade()
                .fitCenter()
                .into(freeItemImage)
    }
}