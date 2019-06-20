package cenergy.central.com.pwb_store.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.activity.AvailableProtocol
import cenergy.central.com.pwb_store.adapter.AvailableStoreAdapter
import cenergy.central.com.pwb_store.model.StoreAvailable
import cenergy.central.com.pwb_store.realm.RealmController

class AvailableFragment : Fragment() {

    private var listener: AvailableProtocol? = null
    private val userInformation = RealmController.getInstance().userInformation
    private lateinit var recyclerView: RecyclerView
    private var availableStoreAdapter: AvailableStoreAdapter? = null

    private var storeAvailableList: List<StoreAvailable> = arrayListOf()

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as AvailableProtocol
        listener?.let {
            this.storeAvailableList = it.getStoreAvailable()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_avaliable, container, false)
        initInstances(rootView)
        return rootView
    }

    private fun initInstances(rootView: View) {
        // Init 'View' instance(s) with rootView.findViewById here
        recyclerView = rootView.findViewById(R.id.recycler_view)
        availableStoreAdapter = AvailableStoreAdapter()
        recyclerView.setHasFixedSize(true)
        recyclerView.isNestedScrollingEnabled = true
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = availableStoreAdapter
        if (userInformation!!.store != null) {
            availableStoreAdapter!!.setCompareAvailable(userInformation.store!!.retailerId, storeAvailableList)
        } else {
            availableStoreAdapter!!.setCompareAvailable("", storeAvailableList)
        }
    }

    companion object {
        fun newInstance(): AvailableFragment {
            val fragment = AvailableFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}
