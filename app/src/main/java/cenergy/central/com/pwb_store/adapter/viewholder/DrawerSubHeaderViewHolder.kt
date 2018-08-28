package cenergy.central.com.pwb_store.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.manager.bus.event.CompareMenuBus
import cenergy.central.com.pwb_store.view.PowerBuyTextView
import org.greenrobot.eventbus.EventBus

/**
 * Created by napabhat on 9/19/2017 AD.
 */

class DrawerSubHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    private var mIcon = itemView.findViewById<ImageView>(R.id.icon)
    private var mTitle = itemView.findViewById<PowerBuyTextView>(R.id.title)
    private var mLinearLayout = itemView.findViewById<LinearLayout>(R.id.layout_compare)

    fun bindItem(title: String) {
        mTitle.text = title
    }

    companion object {
        private val TAG = DrawerSubHeaderViewHolder::class.java.simpleName
    }
}
