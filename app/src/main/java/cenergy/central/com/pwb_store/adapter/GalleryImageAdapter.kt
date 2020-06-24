package cenergy.central.com.pwb_store.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.manager.Contextor
import cenergy.central.com.pwb_store.model.ProductDetailImageItem
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import kotlinx.android.extensions.LayoutContainer
import java.util.ArrayList

class GalleryImageAdapter : RecyclerView.Adapter<GalleryImageViewHolder>() {

    var productImageList: ArrayList<ProductDetailImageItem> = arrayListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryImageViewHolder =
            GalleryImageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_gallery_image, parent, false))

    override fun getItemCount(): Int = productImageList.size

    override fun onBindViewHolder(holderImage: GalleryImageViewHolder, position: Int) {
        holderImage.bind(productImageList[position])
    }
}

class GalleryImageViewHolder(override val containerView: View): RecyclerView.ViewHolder(containerView), LayoutContainer {
    fun bind(productDetailImageItem: ProductDetailImageItem) {
        val itemImage = containerView.findViewById<PhotoView>(R.id.itemImage)
        Glide.with(Contextor.getInstance().context)
                .load(productDetailImageItem.imgUrl)
                .placeholder(R.drawable.ic_placeholder)
                .crossFade()
                .fitCenter()
                .into(itemImage)
    }
}