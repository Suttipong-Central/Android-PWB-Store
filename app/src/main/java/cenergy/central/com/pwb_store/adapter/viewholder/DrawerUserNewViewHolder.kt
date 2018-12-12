package cenergy.central.com.pwb_store.adapter.viewholder

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.realm.RealmController
import cenergy.central.com.pwb_store.view.PowerBuyTextView

/**
 * Created by napabhat on 9/6/2017 AD.
 */
class DrawerUserNewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val imgProfile: ImageView = itemView.findViewById(R.id.image_view_profile)
    private val fullName: PowerBuyTextView = itemView.findViewById(R.id.txt_view_full_name)
    private val storeName: PowerBuyTextView = itemView.findViewById(R.id.txt_store)

    fun setViewHolder() {
        val userInformation = RealmController.getInstance().userInformation
//        imgProfile.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_person))
        fullName.text = userInformation.user?.name ?: ""
        if (userInformation.store != null) {
            storeName.text = userInformation.store!!.storeName ?: ""
        }
    }
}
