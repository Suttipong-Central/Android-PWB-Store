package cenergy.central.com.pwb_store.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.interfaces.ProductImageListener
import cenergy.central.com.pwb_store.extensions.setImage
import cenergy.central.com.pwb_store.extensions.setImageUrl
import cenergy.central.com.pwb_store.model.ProductDetailImageItem


class ProductImageAdapter(private val listener: ProductImageListener,
                          private val productImageList: List<ProductDetailImageItem>)
    : RecyclerView.Adapter<ProductImageAdapter.ProductImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_product_image, parent, false)
        return ProductImageViewHolder(view)
    }

    override fun getItemCount(): Int = productImageList.size


    override fun onBindViewHolder(holder: ProductImageViewHolder, position: Int) {
        val productImage = productImageList[position]
        holder.bindItem(productImage)
        holder.itemView.setOnClickListener {
            listener.onProductImageClickListener(position, productImage)
        }
    }

    inner class ProductImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val ivProductImage = itemView.findViewById<ImageView>(R.id.ivProductImage)

        fun bindItem(productImage: ProductDetailImageItem) {
            if (productImage.imgUrl != null) {
                ivProductImage.setImageUrl(productImage.imgUrl!!)
            } else {
                ivProductImage.setImage(R.drawable.ic_placeholder)
            }
        }
    }
}