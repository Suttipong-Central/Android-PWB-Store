package cenergy.central.com.pwb_store.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.model.StoreAvailable
import cenergy.central.com.pwb_store.view.PowerBuyTextView

class AvailableDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val storeCode = itemView.findViewById<PowerBuyTextView>(R.id.textStoreCode)
    private val name = itemView.findViewById<PowerBuyTextView>(R.id.textName)
    private val tel = itemView.findViewById<PowerBuyTextView>(R.id.textTelephone)
    private val stock = itemView.findViewById<PowerBuyTextView>(R.id.textStock)


    fun setViewHolder(storeAvailable: StoreAvailable) {
        storeCode.text = storeAvailable.sellerCode
        name.text = storeAvailable.name
        tel.text = if(storeAvailable.contactPhone.isNotBlank()) storeAvailable.contactPhone else "-"
        stock.text = storeAvailable.qty.toString()
    }
}
