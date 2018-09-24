package cenergy.central.com.pwb_store.dialogs.adapter.viewholder

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.dialogs.interfaces.StoreOrShippingClickListener
import cenergy.central.com.pwb_store.extensions.setImage
import cenergy.central.com.pwb_store.model.DialogOption

class StoreOrShippingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val icon: ImageView = itemView.findViewById(R.id.img_icon)
    val description: TextView = itemView.findViewById(R.id.txt_description)

    fun onByView(option: DialogOption, storeOrShippingClickListener: StoreOrShippingClickListener?) {
        if (option.icon != 0) {
            icon.setImage(option.icon)
        } else {
            icon.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_question_mark))
            icon.setColorFilter(ContextCompat.getColor(itemView.context, R.color.powerBuyOrange))
        }
        description.text = itemView.context.resources.getString(option.description)
        itemView.setOnClickListener {
            storeOrShippingClickListener?.onStoreOrShippingClick(option)
        }
    }
}
