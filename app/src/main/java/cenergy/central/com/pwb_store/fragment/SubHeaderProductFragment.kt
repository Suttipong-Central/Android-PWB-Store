package cenergy.central.com.pwb_store.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.NewCategoryAdapter
import cenergy.central.com.pwb_store.model.ProductFilterHeader
import cenergy.central.com.pwb_store.view.PowerBuyTextView

class SubHeaderProductFragment : Fragment() {

    private lateinit var productFilterHeader: ProductFilterHeader

    companion object {
        private const val ARG_PRODUCTFILTERHEADER = "title"

        fun newInstance(productFilterHeader: ProductFilterHeader): SubHeaderProductFragment {
            val fragment = SubHeaderProductFragment()
            val args = Bundle()
            args.putParcelable(ARG_PRODUCTFILTERHEADER, productFilterHeader)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            productFilterHeader = arguments!!.getParcelable(ARG_PRODUCTFILTERHEADER)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.activity_sub_header_product, container, false)
        initInstances(rootView)
        return rootView
    }

    private fun initInstances(rootView: View) {
        val subHeaderRecycler = rootView.findViewById<RecyclerView>(R.id.sub_header_recycler)
        val subHeaderTitle = rootView.findViewById<PowerBuyTextView>(R.id.sub_header_title_text)
        subHeaderTitle.text = productFilterHeader.name
        val adapter = NewCategoryAdapter()
        val gridLayoutManager = GridLayoutManager(rootView.context, 3, LinearLayoutManager.VERTICAL, false)
        subHeaderRecycler.layoutManager = gridLayoutManager
        subHeaderRecycler.adapter = adapter
    }
}
