package cenergy.central.com.pwb_store.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.interfaces.SelectedImageListener
import cenergy.central.com.pwb_store.extensions.setImageUrl
import cenergy.central.com.pwb_store.model.ProductDetailImageItem
import java.util.*

class GalleryIndicatorAdapter : RecyclerView.Adapter<GalleryIndicatorViewHolder>() {

    var productImageList: ArrayList<ProductDetailImageItem> = arrayListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var listener: SelectedImageListener? = null

    fun setSelectedImageListener(listener: SelectedImageListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryIndicatorViewHolder {
        return GalleryIndicatorViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_gallery_indicator, parent, false))
    }

    override fun getItemCount(): Int {
        return productImageList.size
    }

    override fun onBindViewHolder(holder: GalleryIndicatorViewHolder, position: Int) {
        holder.bindView(productImageList[position], listener)
    }
}

class GalleryIndicatorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val itemImage = itemView.findViewById<ImageView>(R.id.itemImage)
    fun bindView(productDetailImageItem: ProductDetailImageItem, listener: SelectedImageListener?) {
        productDetailImageItem.imgUrl?.let { itemImage.setImageUrl(it, R.drawable.ic_placeholder) }
        itemImage.setOnClickListener { listener?.onClickImageListener(adapterPosition) }


    }
}