package cenergy.central.com.pwb_store.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import cenergy.central.com.pwb_store.R

class StoreDetailFragment : Fragment() {

    lateinit var tvTitle : TextView
    companion object {
        fun newInstance(): StoreDetailFragment {
            val fragment = StoreDetailFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = LayoutInflater.from(context).inflate(R.layout.fragment_store_detail, container)
        tvTitle = rootView.findViewById(R.id.store_name_title)
        return rootView
    }

    fun updateStoreDetail(store: String) {
        tvTitle.text = store
    }
}