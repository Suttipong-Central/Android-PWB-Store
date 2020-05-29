package cenergy.central.com.pwb_store.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.realm.RealmController
import cenergy.central.com.pwb_store.view.PowerBuyTextView

class DrawerUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val storeName: PowerBuyTextView = itemView.findViewById(R.id.storeNameTextView)

    fun setViewHolder() {
        val userInformation = RealmController.getInstance().userInformation
        if (userInformation.store != null) {
            storeName.text = userInformation.store!!.storeName ?: "-"
        }
    }
}
