package cenergy.central.com.pwb_store.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.fragment.interfaces.StorePickUpListener

class StoreDetailFragment : Fragment() {
    private lateinit var tvTitle: TextView
    private lateinit var tvAddress: TextView
    private lateinit var tvContract: TextView
    private lateinit var tvOpenStore: TextView
    private lateinit var selectedButton: CardView

    private var listener: StorePickUpListener? = null

    companion object {
        fun newInstance(): StoreDetailFragment {
            val fragment = StoreDetailFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as StorePickUpListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = LayoutInflater.from(context).inflate(R.layout.fragment_store_detail, container, false)
        tvTitle = rootView.findViewById(R.id.store_name_title)
        tvAddress = rootView.findViewById(R.id.store_address_txt)
        tvContract = rootView.findViewById(R.id.store_contact_txt)
        tvOpenStore = rootView.findViewById(R.id.store_open_txt)
        selectedButton = rootView.findViewById(R.id.select_button)

        return rootView
    }

    fun updateStoreDetail(store: String) {
        tvTitle.text = store
        tvAddress.text = store
        tvContract.text = store
        tvOpenStore.text = store
        selectedButton.setOnClickListener {
            listener?.onSeletedStore(store)
        }
    }
}