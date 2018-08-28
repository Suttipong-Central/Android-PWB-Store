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
import cenergy.central.com.pwb_store.adapter.HistoryAdapter
import cenergy.central.com.pwb_store.manager.listeners.HistoryClickListener
import cenergy.central.com.pwb_store.manager.listeners.PaymentClickListener
import cenergy.central.com.pwb_store.realm.RealmController
import cenergy.central.com.pwb_store.view.PowerBuyTextView

class PaymentHistoryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var txtResult: PowerBuyTextView
    private var listener: HistoryClickListener? = null
    private var orderResponses = RealmController.getInstance().orderResponses

    companion object {
        fun newInstance(): PaymentHistoryFragment {
            val fragment = PaymentHistoryFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as HistoryClickListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = LayoutInflater.from(context).inflate(R.layout.fragment_history_payment, container, false)
        setupView(rootView)
        return rootView
    }

    private fun setupView(rootView: View) {
        recyclerView = rootView.findViewById(R.id.recycler_view_history)
        txtResult = rootView.findViewById(R.id.txt_result)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = listener?.let { HistoryAdapter(it, this.orderResponses) }
        if (this.orderResponses.size > 0) {
            recyclerView.visibility = View.VISIBLE
            txtResult.visibility = View.INVISIBLE
        } else {
            recyclerView.visibility = View.INVISIBLE
            txtResult.visibility = View.VISIBLE
        }
    }
}