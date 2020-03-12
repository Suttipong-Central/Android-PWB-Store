package cenergy.central.com.pwb_store.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import cenergy.central.com.pwb_store.BuildConfig
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.realm.RealmController
import cenergy.central.com.pwb_store.view.PowerBuyTextView

/**
 * Created by napabhat on 9/6/2017 AD.
 */
class DrawerUserNewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

//    private val imgProfile: ImageView = itemView.findViewById(R.id.image_view_profile)
    private val staffId: PowerBuyTextView = itemView.findViewById(R.id.txt_staff_id)
    private val fullName: PowerBuyTextView = itemView.findViewById(R.id.txt_view_full_name)
    private val storeName: PowerBuyTextView = itemView.findViewById(R.id.txt_store)

    fun setViewHolder() {
        if (BuildConfig.FLAVOR != "pwbOmniTV"){
            val userInformation = RealmController.getInstance().userInformation
//        imgProfile.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_person))
            staffId.text = userInformation.user?.staffId ?: ""
            fullName.text = userInformation.user?.username ?: ""
            if (userInformation.store != null) {
                storeName.text = userInformation.store!!.storeName ?: ""
            }
        }
    }
}
