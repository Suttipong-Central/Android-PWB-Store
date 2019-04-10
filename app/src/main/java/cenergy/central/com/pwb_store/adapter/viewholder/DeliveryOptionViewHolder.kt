package cenergy.central.com.pwb_store.adapter.viewholder

import android.annotation.SuppressLint
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.manager.listeners.DeliveryOptionsListener
import cenergy.central.com.pwb_store.model.DeliveryOption
import cenergy.central.com.pwb_store.model.DeliveryType
import java.util.*


class DeliveryOptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private var icon: ImageView = itemView.findViewById(R.id.ivDeliveryIcon)
    private var carrierTitle: TextView = itemView.findViewById(R.id.tv_carrier_title)
    private var methodTitle: TextView = itemView.findViewById(R.id.tv_method_title)
    private var amount: TextView = itemView.findViewById(R.id.tv_amount)
    private var button: TextView = itemView.findViewById(R.id.choose_delivery_option)

    @SuppressLint("SetTextI18n")
    fun bindItem(deliveryOption: DeliveryOption, listener: DeliveryOptionsListener?) {
        val unit = itemView.context.getString(R.string.baht)
        val deliveryType = DeliveryType.fromString(deliveryOption.methodCode)
        when (deliveryType) {
            DeliveryType.EXPRESS -> {
                setImage(R.drawable.ic_fast_delivery_selected)
                carrierTitle.text = itemView.context.getString(R.string.express)
            }
            DeliveryType.STANDARD -> {
                setImage(R.drawable.ic_delivery_standard)
                carrierTitle.text = itemView.context.getString(R.string.standard)
            }
            DeliveryType.STORE_PICK_UP -> {
                setImage(R.drawable.ic_store)
                carrierTitle.text = itemView.context.getString(R.string.collect)
            }
            DeliveryType.HOME -> {
                setImage(R.drawable.ic_hdl)
                carrierTitle.text = itemView.context.getString(R.string.home_delivery)
            }
        }
        if (deliveryType == DeliveryType.EXPRESS) {
            val calendar = Calendar.getInstance()

            if (calendar.get(Calendar.HOUR_OF_DAY) < 12) {
                methodTitle.text = itemView.context.getString(R.string.between15_20)
            } else {
                methodTitle.text = deliveryOption.methodTitle
            }
        } else {
            methodTitle.text = deliveryOption.methodTitle
        }
        amount.text = "$unit${deliveryOption.amount}"
        amount.visibility = if (deliveryOption.amount > 0) View.VISIBLE else View.GONE
        if (deliveryOption.available) {
            itemView.setOnClickListener {
                listener?.onSelectedOptionListener(deliveryOption)
            }
            button.setOnClickListener {
                listener?.onSelectedOptionListener(deliveryOption)
            }
        } else {
            itemView.isEnabled = false
            button.isClickable = false
        }
    }

    private fun setImage(imageDrawable: Int) {
        icon.setImageDrawable(ContextCompat.getDrawable(itemView.context, imageDrawable))
    }
}
