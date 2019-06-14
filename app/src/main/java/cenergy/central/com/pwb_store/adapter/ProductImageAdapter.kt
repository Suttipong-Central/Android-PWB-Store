package cenergy.central.com.pwb_store.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.interfaces.ProductImageListener
import cenergy.central.com.pwb_store.extensions.setImageUrl
import cenergy.central.com.pwb_store.model.ProductDetailImageItem

/**
 * Created by Anuphap Suwannamas on 2/10/2018 AD.
 * Email: Anupharpae@gmail.com
 */

class ProductImageAdapter(private val listener: ProductImageListener,private val productImageList: List<ProductDetailImageItem>) : RecyclerView.Adapter<ProductImageAdapter.ProductImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_product_image, parent, false)
        return ProductImageViewHolder(view)
    }

    override fun getItemCount(): Int = productImageList.size


    override fun onBindViewHolder(holder: ProductImageViewHolder, position: Int) {
        val productImage = productImageList[position]
        holder.bindItem(productImage)
        holder.itemView.setOnClickListener {
            listener.onProductImageClickListener(productImage)
        }
    }

    inner class ProductImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val ivProductImage = itemView.findViewById<ImageView>(R.id.ivProductImage)
        fun bindItem(productImage: ProductDetailImageItem) {
            ivProductImage?.setImageUrl(productImage.imgUrl)
        }
    }
}