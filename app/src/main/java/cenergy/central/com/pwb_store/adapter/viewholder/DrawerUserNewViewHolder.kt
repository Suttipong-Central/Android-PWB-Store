package cenergy.central.com.pwb_store.adapter.viewholder

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.StoreAdapter
import cenergy.central.com.pwb_store.manager.UserInfoManager
import cenergy.central.com.pwb_store.model.StoreDao
import cenergy.central.com.pwb_store.model.StoreList
import cenergy.central.com.pwb_store.view.PowerBuyListDialog
import cenergy.central.com.pwb_store.view.PowerBuyTextView

/**
 * Created by napabhat on 9/6/2017 AD.
 */
class DrawerUserNewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val imgProfile: ImageView = itemView.findViewById(R.id.image_view_profile)
    private val fullName: PowerBuyTextView = itemView.findViewById(R.id.txt_view_full_name)
    private val storeName: PowerBuyTextView = itemView.findViewById(R.id.txt_store)
    private val layout: LinearLayout = itemView.findViewById(R.id.layout_header_user)

    fun setViewHolder(context: Context) {
        imgProfile.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_person))
        fullName.text = "Anuphab Suwannamas"
        storeName.text = "Central world"
        layout.setOnClickListener {
            //TODO Open user detail
        }
    }
}
