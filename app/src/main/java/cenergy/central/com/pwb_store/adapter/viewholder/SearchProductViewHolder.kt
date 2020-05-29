package cenergy.central.com.pwb_store.adapter.viewholder

import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cenergy.central.com.pwb_store.BuildConfig
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.manager.bus.event.BarcodeBus
import cenergy.central.com.pwb_store.manager.bus.event.HomeBus
import cenergy.central.com.pwb_store.manager.bus.event.SearchEventBus
import cenergy.central.com.pwb_store.view.PowerBuyEditText
import org.greenrobot.eventbus.EventBus

class SearchProductViewHolder(itemView: View, private val canBack: Boolean)
    : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    private val mSearchView: PowerBuyEditText = itemView.findViewById(R.id.edit_text_search)
    private val mBarCode: ImageView = itemView.findViewById(R.id.image_view_barcode)
    private val backButton: ImageView = itemView.findViewById(R.id.image_back_button)

    override fun onClick(v: View) {
        when (v) {
            mBarCode -> {
                EventBus.getDefault().post(BarcodeBus(true))
            }
            backButton -> {
                EventBus.getDefault().post(HomeBus(true))
            }
        }
    }

    fun bind() {
        if (canBack){
            backButton.visibility = View.VISIBLE
            backButton.setOnClickListener(this)
        } else {
            backButton.visibility = View.GONE
            backButton.setOnClickListener(null)
        }
        if (BuildConfig.FLAVOR !== "pwbOmniTv") {
            mBarCode.setOnClickListener(this)
        } else {
            mBarCode.setOnClickListener(null)
            mBarCode.visibility = View.GONE
        }
        mSearchView.hint = itemView.context.getString(R.string.search_hint)
        mSearchView.setOnEditorActionListener(SearchOnEditorActionListener())
    }

    private inner class SearchOnEditorActionListener : TextView.OnEditorActionListener {
        override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent?): Boolean {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                EventBus.getDefault().post(SearchEventBus(true, mSearchView.text.toString()))
            }
            return true
        }
    }
}
