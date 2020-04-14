package cenergy.central.com.pwb_store.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.activity.interfaces.PaymentProtocol
import cenergy.central.com.pwb_store.adapter.PaymentTransferAdapter
import cenergy.central.com.pwb_store.adapter.PwbArrayAdapter
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_payment_transfers.*
import java.util.*

class PaymentTransfersFragment : Fragment() {
    private lateinit var paymentProtocol: PaymentProtocol
    private var bankAgentItems = arrayListOf<PaymentTransferAdapter.AgentItem>()
    private var bankChannels = listOf<String>()
    private val bankChannelAdapter by lazy { PwbArrayAdapter(context!!, R.layout.layout_text_item, arrayListOf()) }
    private val paymentTransferAdapter by lazy {
        PaymentTransferAdapter {
            if (it.isBankAgent()) {
                // update selected
                this.bankAgentItems.forEach { item ->
                    item.isSelect = item.agent.agentId == it.agentId
                }
                updateAgentItems(this.bankAgentItems)

                // update bank channel
                this.bankChannels = it.getChannels()
                updateBankChannels(this.bankChannels)
            }
        }
    }

    private fun updateBankChannels(bankChannels: List<String>) {
        paymentChannelOptions.setText("")
        bankChannelAdapter.setItems(bankChannels)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        paymentProtocol = context as PaymentProtocol
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bankAgents = paymentProtocol.getPaymentAgents().filter {
            it.isBankAgent()
        }

        this.bankAgentItems.clear()
        bankAgents.mapTo(bankAgentItems, {
            PaymentTransferAdapter.AgentItem(it)
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_payment_transfers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTab()
        setupOptionRecycler()
        payNowButton.setOnClickListener {
            initPay()
        }
    }

    private fun initPay() {
        val item = paymentTransferAdapter.getItemSelected()
        if (item.agent.isBankAgent()) {
            Toast.makeText(context, item.agent.name, Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateAgentItems(bankAgentItems: ArrayList<PaymentTransferAdapter.AgentItem>) {
        paymentTransferAdapter.source = bankAgentItems
    }

    private fun setupOptionRecycler() {
        agentRecycler?.apply {
            layoutManager = GridLayoutManager(context, 5)
            setHasFixedSize(true)
            adapter = paymentTransferAdapter
        }
        // Show only bank agents
        updateAgentItems(bankAgentItems)

        // setup bank channel
        bankChannelAdapter.setCallback(object : PwbArrayAdapter.PwbArrayAdapterListener{
            override fun onItemClickListener(item: String) {
                paymentChannelOptions.setError(null) // clear error
                paymentChannelOptions.setText(item)
                hideKeyboard()
            }
        })
        paymentChannelOptions.setAdapter(bankChannelAdapter)
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

    private fun hideKeyboard() {
        val inputManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocus = activity?.currentFocus
        if (currentFocus != null) {
            inputManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
            inputManager.hideSoftInputFromInputMethod(currentFocus.windowToken, 0)
        }
    }
}