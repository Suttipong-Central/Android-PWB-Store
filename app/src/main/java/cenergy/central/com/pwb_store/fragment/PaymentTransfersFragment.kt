package cenergy.central.com.pwb_store.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.activity.interfaces.PaymentProtocol
import cenergy.central.com.pwb_store.adapter.PaymentTransferAdapter
import cenergy.central.com.pwb_store.model.response.PaymentAgent
import cenergy.central.com.pwb_store.model.response.PaymentAgentType
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_payment_transfers.*

class PaymentTransfersFragment : Fragment() {
    private lateinit var paymentProtocol: PaymentProtocol
    private val paymentTransferAdapter by lazy { PaymentTransferAdapter() }
    private var paymentAgents = listOf<PaymentAgent>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        paymentProtocol = context as PaymentProtocol
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.paymentAgents = paymentProtocol.getPaymentAgents()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_payment_transfers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTab()
        setupOptionRecycler()
    }

    private fun setupOptionRecycler() {
        agentRecycler?.apply {
            layoutManager = GridLayoutManager(context, 5)
            setHasFixedSize(true)
            adapter = paymentTransferAdapter
        }

        // Show only bank transfer
        paymentTransferAdapter.items = paymentAgents.filter {
            PaymentAgentType.fromString(it.type) == PaymentAgentType.BANK
        }
    }

    private fun setupTab() {
        paymentTransferTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position == 0) {
                    paymentTransferTabLayout.getTabAt(1)?.select()
                    return
                }
            }
        })
        paymentTransferTabLayout.getTabAt(1)?.select()
    }
}