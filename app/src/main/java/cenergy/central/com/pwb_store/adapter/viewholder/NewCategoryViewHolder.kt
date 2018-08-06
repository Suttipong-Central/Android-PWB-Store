package cenergy.central.com.pwb_store.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import cenergy.central.com.pwb_store.R

class NewCategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var itemImg = itemView.findViewById<ImageView>(R.id.list_item_sub_header_product_img)
    var itemText = itemView.findViewById<TextView>(R.id.list_item_sub_header_product_text)

}